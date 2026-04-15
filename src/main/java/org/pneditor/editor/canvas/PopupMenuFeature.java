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
import javax.swing.JPopupMenu;
import org.pneditor.editor.PNEditor;
import org.pneditor.editor.Root;
import org.pneditor.petrinet.ArcEdge;
import org.pneditor.petrinet.PlaceNode;
import org.pneditor.petrinet.Subnet;
import org.pneditor.petrinet.Transition;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class PopupMenuFeature implements Feature {

    private Canvas canvas;

    public PopupMenuFeature(Canvas canvas) {
        this.canvas = canvas;
    }

    public void mousePressed(MouseEvent event) {
        int x = event.getX();
        int y = event.getY();
        int mouseButton = event.getButton();

        int realX = x + canvas.getTranslationX();
        int realY = y + canvas.getTranslationY();

        if (mouseButton == MouseEvent.BUTTON3) {
            if (PNEditor.getRoot().getClickedElement() != null
                    && (PNEditor.getRoot().isSelectedTool_Select()
                    || PNEditor.getRoot().isSelectedTool_Place()
                    || PNEditor.getRoot().isSelectedTool_Transition()
                    || PNEditor.getRoot().isSelectedTool_Arc()
                    || PNEditor.getRoot().isSelectedTool_Token() && !(PNEditor.getRoot().getClickedElement() instanceof PlaceNode))) {
                if (PNEditor.getRoot().getClickedElement() instanceof PlaceNode) {
                    showPopup(PNEditor.getRoot().getPlacePopup(), realX, realY);
                    if (!PNEditor.getRoot().getSelection().contains(PNEditor.getRoot().getClickedElement())) {
                        PNEditor.getRoot().getSelection().clear();
                    }
                } else if (PNEditor.getRoot().getClickedElement() instanceof Subnet) {
                    showPopup(PNEditor.getRoot().getSubnetPopup(), realX, realY);
                    if (!PNEditor.getRoot().getSelection().contains(PNEditor.getRoot().getClickedElement())) {
                        PNEditor.getRoot().getSelection().clear();
                    }
                } else if (PNEditor.getRoot().getClickedElement() instanceof Transition) {
                    showPopup(PNEditor.getRoot().getTransitionPopup(), realX, realY);
                    if (!PNEditor.getRoot().getSelection().contains(PNEditor.getRoot().getClickedElement())) {
                        PNEditor.getRoot().getSelection().clear();
                    }
                } else if (PNEditor.getRoot().getClickedElement() instanceof ArcEdge) {
                    showPopup(PNEditor.getRoot().getArcEdgePopup(), realX, realY);
                    if (!PNEditor.getRoot().getSelection().contains(PNEditor.getRoot().getClickedElement())) {
                        PNEditor.getRoot().getSelection().clear();
                    }
                }
            }

            if (PNEditor.getRoot().getClickedElement() == null
                    && PNEditor.getRoot().isSelectedTool_Select()) {
                showPopup(PNEditor.getRoot().getCanvasPopup(), realX, realY);
            }
        }
    }

    private void showPopup(JPopupMenu popupMenu, int clickedX, int clickedY) {
        popupMenu.show(canvas, clickedX - 10, clickedY - 2);
    }

    public void drawForeground(Graphics g) {
    }

    public void drawBackground(Graphics g) {
    }

    public void mouseDragged(int x, int y) {
    }

    public void mouseReleased(int x, int y) {
    }

    public void setHoverEffects(int x, int y) {
    }

    public void setCursor(int x, int y) {
    }

    public void drawMainLayer(Graphics g) {
    }

    public void mouseMoved(int x, int y) {
    }

}
