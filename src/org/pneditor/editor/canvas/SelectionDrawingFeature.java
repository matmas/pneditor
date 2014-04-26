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

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;
import org.pneditor.editor.PNEditor;
import org.pneditor.editor.Root;
import org.pneditor.petrinet.Element;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
class SelectionDrawingFeature implements Feature {

    private Canvas canvas;

    SelectionDrawingFeature(Canvas canvas) {
        this.canvas = canvas;
    }

    private boolean selecting = false;
    private VisualSelection visualSelection = new VisualSelection();
    private Set<Element> previousSelection = new HashSet<Element>();

    public void mousePressed(MouseEvent event) {
        int x = event.getX();
        int y = event.getY();
        int mouseButton = event.getButton();

        if (mouseButton == MouseEvent.BUTTON1
                && PNEditor.getRoot().getClickedElement() == null
                && PNEditor.getRoot().isSelectedTool_Select()) {
            selecting = true;
            visualSelection.setStart(x, y);
            visualSelection.setEnd(x, y);
            canvas.repaint();
            if (event.isShiftDown()) {
                previousSelection.addAll(PNEditor.getRoot().getSelection().getElements());
            } else {
                PNEditor.getRoot().getSelection().clear();
                previousSelection.clear();
            }
        }
    }

    public void mouseDragged(int x, int y) {
        if (selecting) {
            visualSelection.setEnd(x, y);
            canvas.repaint();
        }
    }

    public void mouseReleased(int x, int y) {
        if (selecting) {
            selecting = false;
            canvas.repaint();
        }
    }

    public void setHoverEffects(int x, int y) {
        for (Element selectedElement : PNEditor.getRoot().getSelection()) {
            canvas.highlightedElements.add(selectedElement);
            selectedElement.highlightColor = Colors.selectedColor;
        }

        if (selecting) {
            PNEditor.getRoot().getSelection().clear();
            PNEditor.getRoot().getSelection().addAll(previousSelection);
            for (Element visualElement : PNEditor.getRoot().getDocument().petriNet.getCurrentSubnet().getElements()) {
                if (visualSelection.containsPoint(visualElement.getCenter().x, visualElement.getCenter().y)) {
                    addElementToSelection(visualElement);
                }
            }
            canvas.repaint();
        }
    }

    private void addElementToSelection(Element element) {
        canvas.highlightedElements.add(element);
        element.highlightColor = Colors.selectedColor;

        PNEditor.getRoot().getSelection().add(element);
    }

    public void drawForeground(Graphics g) {
        if (selecting) {
            visualSelection.draw(g, null);
        }
    }

    public void drawBackground(Graphics g) {
    }

    public void setCursor(int x, int y) {
    }

    public void drawMainLayer(Graphics g) {
    }

    public void mouseMoved(int x, int y) {
    }
}
