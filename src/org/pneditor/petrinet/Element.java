/*
 * Copyright (C) 2008-2010 Martin Riesz <riesz.martin at gmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.pneditor.petrinet;

import java.awt.*;
import java.io.Serializable;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public abstract class Element implements Serializable, Cloneable {

    private Subnet parentSubnet = null;

    private Point start = new Point();
    private Point end = new Point();
    private transient Point center = new Point();

    public Color color = Color.black;
    public transient Color highlightColor = null;

    public void setParentSubnet(Subnet parentSubnet) {
        this.parentSubnet = parentSubnet;
    }

    public Subnet getParentSubnet() {
        return parentSubnet;
    }

    public Point getStart() {
        if (start == null) {
            start = new Point();
        }
        return start;
    }

    public Point getEnd() {
        if (end == null) {
            end = new Point();
        }
        return end;
    }

    public void setStart(int x, int y) {
        if (start == null) {
            start = new Point();
        }
        start.x = x;
        start.y = y;
    }

    public void setEnd(int x, int y) {
        if (end == null) {
            end = new Point();
        }
        end.x = x;
        end.y = y;
    }

    public int getWidth() {
        return Math.abs(getEnd().x - getStart().x);
    }

    public int getHeight() {
        return Math.abs(getEnd().y - getStart().y);
    }

    private int getCenterX() {
        return getStart().x + (getEnd().x - getStart().x) / 2;
    }

    private int getCenterY() {
        return getStart().y + (getEnd().y - getStart().y) / 2;
    }

    public Point getCenter() {
        if (center == null) {
            center = new Point();
        }
        if (center.x != getCenterX()) {
            center.x = getCenterX();
        }
        if (center.y != getCenterY()) {
            center.y = getCenterY();
        }
        return center;
    }

    public void setCenter(int x, int y) {
        moveBy(x - getCenter().x, y - getCenter().y);
    }

    public void setCenter(Point center) {
        setCenter(center.x, center.y);
    }

    public void setSize(int width, int height) {
        Point prevCenter = getCenter();
        setEnd(getStart().x + width, getStart().y + height);
        setCenter(prevCenter.x, prevCenter.y);
    }

    public void moveBy(int dx, int dy) {
        setStart(getStart().x + dx, getStart().y + dy);
        setEnd(getEnd().x + dx, getEnd().y + dy);
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public boolean containsPoint(int x, int y) {
        int l = Math.min(getStart().x, getEnd().x);
        int t = Math.min(getStart().y, getEnd().y);
        int w = Math.abs(getWidth());
        int h = Math.abs(getHeight());
        if (x >= l && x < l + w && y >= t && y < t + h) {
            return true;
        } else {
            return false;
        }
    }

    abstract public void draw(Graphics g, DrawingOptions drawingOptions);

    public Element getClone() {
        try {
            Element element = (Element) this.clone();
            element.start = this.start.getLocation();
            element.end = this.end.getLocation();
            element.parentSubnet = this.parentSubnet;
            return element;
        } catch (CloneNotSupportedException ex) {
            throw new RuntimeException();
        }
    }
}
