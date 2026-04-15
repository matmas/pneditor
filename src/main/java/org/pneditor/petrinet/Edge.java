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

import java.awt.Graphics;
import java.awt.Point;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.pneditor.util.GraphicsTools;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public abstract class Edge extends Element implements Cloneable {

    private Node source;
    private Node destination;
    private List<Point> breakPoints = new LinkedList<Point>();

    public List<Point> getBreakPoints() {
        return Collections.unmodifiableList(breakPoints);
    }

    public void removeBreakPoints() {
        breakPoints.clear();
    }

    public List<Point> getBreakPointsCopy() {
        List<Point> newBreakPoints = new LinkedList<Point>();
        for (Point breakPoint : breakPoints) {
            newBreakPoints.add(breakPoint.getLocation()); //getLocation because Point is mutable
        }
        return newBreakPoints;
    }

    public void setBreakPoints(List<Point> breakPoints) {
        this.breakPoints.clear();
        for (Point breakPoint : breakPoints) {
            this.breakPoints.add(breakPoint.getLocation()); //getLocation because Point is mutable
        }
    }

    protected void reverseBreakPoints() {
        Collections.reverse(breakPoints);
    }

    public Node getSource() {
        return source;
    }

    public void setSource(Node source) {
        this.source = source;
    }

    public Node getDestination() {
        return destination;
    }

    public void setDestination(Node destination) {
        this.destination = destination;
    }

    protected Point getLabelPoint(Point arrowTip) {
        final Point labelPoint = new Point();
        if (breakPoints.isEmpty()) {
            labelPoint.x = getStart().x + (arrowTip.x - getStart().x) * 2 / 3;
            labelPoint.y = getStart().y + (arrowTip.y - getStart().y) * 2 / 3 - 3;
        } else {
            final Point lastBreakPoint = breakPoints.get(breakPoints.size() - 1);
            labelPoint.x = lastBreakPoint.x + (arrowTip.x - lastBreakPoint.x) * 1 / 2;
            labelPoint.y = lastBreakPoint.y + (arrowTip.y - lastBreakPoint.y) * 1 / 2 - 3;
        }
        return labelPoint;
    }

    protected Point computeArrowTipPoint() {
        Point arrowTip = new Point(getEnd());
        if (getDestination() == null) {
            return arrowTip;
        } else { //Thanks to http://www.cs.unc.edu/~mcmillan/comp136/Lecture6/Lines.html
            int x0 = getLastBreakPoint().x;
            int y0 = getLastBreakPoint().y;
            int x1 = getEnd().x;
            int y1 = getEnd().y;

            int dy = y1 - y0;
            int dx = x1 - x0;
            int stepx, stepy;

            if (dy < 0) {
                dy = -dy;
                stepy = -1;
            } else {
                stepy = 1;
            }
            if (dx < 0) {
                dx = -dx;
                stepx = -1;
            } else {
                stepx = 1;
            }
            dy <<= 1;
            dx <<= 1;

            if (dx > dy) {
                int fraction = dy - (dx >> 1);
                while (x0 != x1) {
                    if (fraction >= 0) {
                        y0 += stepy;
                        fraction -= dx;
                    }
                    x0 += stepx;
                    fraction += dy;
                    if (getDestination().containsPoint(x0, y0)) {
                        return arrowTip;
                    }
                    arrowTip = new Point(x0, y0);
                }
            } else {
                int fraction = dx - (dy >> 1);
                while (y0 != y1) {
                    if (fraction >= 0) {
                        x0 += stepx;
                        fraction -= dy;
                    }
                    y0 += stepy;
                    fraction += dx;
                    if (getDestination().containsPoint(x0, y0)) {
                        return arrowTip;
                    }
                    arrowTip = new Point(x0, y0);
                }
            }
        }
        return arrowTip;
    }

    @Override
    public Point getStart() {
        return getSource() != null ? getSource().getCenter() : super.getStart();
    }

    @Override
    public Point getEnd() {
        return getDestination() != null ? getDestination().getCenter() : super.getEnd();
    }

    @Override
    public void moveBy(int dx, int dy) {
        super.moveBy(dx, dy);
        for (Point breakPoint : breakPoints) {
            breakPoint.translate(dx, dy);
        }
    }

    public static final int nearTolerance = 10;

    @Override
    public boolean containsPoint(int x, int y) {
        final Point testPos = new Point(x, y);
        Point previous = getStart();
        for (Point breakPoint : breakPoints) {
            if (GraphicsTools.isPointNearSegment(previous, breakPoint, testPos, nearTolerance)) {
                return true;
            }
            previous = breakPoint;
        }
        return GraphicsTools.isPointNearSegment(previous, getEnd(), testPos, nearTolerance);
    }

    public Point addOrGetBreakPoint(Point newPoint) {
        for (Point breakPoint : breakPoints) {
            if (GraphicsTools.isPointNearPoint(newPoint, breakPoint, nearTolerance)) {
                return breakPoint;
            }
        }

        if (breakPoints.isEmpty()) {
            breakPoints.add(newPoint);
        } else {
            Point previous = getStart();
            for (int i = 0; i < breakPoints.size(); i++) {
                if (GraphicsTools.isPointNearSegment(previous, breakPoints.get(i), newPoint, nearTolerance)) {
                    breakPoints.add(i, newPoint);
                    return newPoint;
                }
                previous = breakPoints.get(i);
            }
            if (GraphicsTools.isPointNearSegment(previous, getEnd(), newPoint, nearTolerance)) {
                breakPoints.add(newPoint);
            }
        }
        return newPoint;
    }

    public void addDistantBreakPointToEnd(Point newPoint) {
        breakPoints.add(newPoint);
    }

    public void addDistantBreakPointToBeginning(Point newPoint) {
        breakPoints.add(0, newPoint);
    }

    public void cleanupUnecessaryBreakPoints() {
        Point previous = getStart();
        for (int i = 0; i < breakPoints.size(); i++) {
            Point current = breakPoints.get(i);
            Point next = i < (breakPoints.size() - 1) ? breakPoints.get(i + 1) : getEnd();
            final int tolerance = Math.round(0.1f * (float) previous.distance(next));
            if (GraphicsTools.isPointNearSegment(previous, next, current, tolerance)) {
                breakPoints.remove(i--);
            } else {
                previous = breakPoints.get(i);
            }
        }
    }

    protected Point getLastBreakPoint() {
        Point last = getStart();
        for (Point breakPoint : breakPoints) {
            last = breakPoint;
        }
        return last;
    }

    protected void drawSegmentedLine(Graphics g) {
        g.setColor(color);
        Point previous = getStart();
        for (Point breakPoint : breakPoints) {
            g.drawLine(previous.x, previous.y, breakPoint.x, breakPoint.y);
            previous = breakPoint;
        }
        g.drawLine(previous.x, previous.y, getEnd().x, getEnd().y);
    }
}
