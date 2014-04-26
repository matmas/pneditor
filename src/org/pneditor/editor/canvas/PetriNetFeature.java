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
package org.pneditor.editor.canvas;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import javax.swing.JScrollBar;
import org.pneditor.editor.PNEditor;
import org.pneditor.petrinet.DrawingOptions;
import org.pneditor.editor.Root;
import org.pneditor.petrinet.Element;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class PetriNetFeature implements Feature {

    private Canvas canvas;
    private DrawingOptions drawingOptions = new DrawingOptions();

    public PetriNetFeature(Canvas canvas) {
        this.canvas = canvas;
    }

    public void drawMainLayer(Graphics g) {
        for (Element element : PNEditor.getRoot().getDocument().petriNet.getCurrentSubnet().getElements()) {
            if (element.highlightColor != null) {
                Color previousColor = element.getColor();

                element.setColor(element.highlightColor);
                drawingOptions.setMarking(PNEditor.getRoot().getCurrentMarking());
                element.draw(g, drawingOptions); //TODO

                element.setColor(previousColor);
            } else {
                drawingOptions.setMarking(PNEditor.getRoot().getCurrentMarking());
                element.draw(g, drawingOptions); //TODO
            }
        }
    }

    public void drawForeground(Graphics g) {
    }

    public void drawBackground(Graphics g) {
    }

    public void mousePressed(MouseEvent event) {
    }

    public void mouseDragged(int x, int y) {
    }

    public void mouseReleased(int x, int y) {
    }

    public void setHoverEffects(int x, int y) {
    }

    public void setCursor(int x, int y) {
    }

    public void mouseMoved(int x, int y) {
    }

}
