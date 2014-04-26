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

import java.awt.Color;
import java.awt.Graphics;
import org.pneditor.util.GraphicsTools;
import org.pneditor.util.GraphicsTools.HorizontalAlignment;
import org.pneditor.util.GraphicsTools.VerticalAlignment;

/**
 * Represents a transition in Petri net
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class Transition extends TransitionNode implements Cloneable {

    @Override
    public void draw(Graphics g, DrawingOptions drawingOptions) {
        g.setColor(Color.white);
        g.fillRect(getStart().x, getStart().y, getWidth(), getHeight());
        g.setColor(color);
        g.drawRect(getStart().x, getStart().y, getWidth() - 1, getHeight() - 1);
        drawLabel(g);
    }

    @Override
    protected void drawLabel(Graphics g) {
        if (getLabel() != null && !getLabel().equals("")) {
//			GraphicsTools.drawString(g, getLabel(), getCenter().x, getCenter().y, HorizontalAlignment.center, VerticalAlignment.center, new Font("Times", Font.BOLD, 24));
            GraphicsTools.drawString(g, getLabel(), getCenter().x, getEnd().y, HorizontalAlignment.center, VerticalAlignment.top);
        }
//		GraphicsTools.drawString(g, getId(), getCenter().x, getStart().y, HorizontalAlignment.center, VerticalAlignment.bottom);
    }
}
