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
import java.awt.event.MouseEvent;
import org.pneditor.editor.PNEditor;
import org.pneditor.editor.Root;
import org.pneditor.petrinet.Subnet;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class SubnetFeature implements Feature {

    private Canvas canvas;

    public SubnetFeature(Canvas canvas) {
        this.canvas = canvas;
    }

    public void mousePressed(MouseEvent event) {
        int x = event.getX();
        int y = event.getY();
        boolean doubleclick = event.getClickCount() == 2;
        if (doubleclick) {
            if (PNEditor.getRoot().getClickedElement() instanceof Subnet) {
                PNEditor.getRoot().openSubnet();
            } else if (PNEditor.getRoot().getClickedElement() == null) {
                PNEditor.getRoot().closeSubnet();
            }
        }
    }

    public void drawForeground(Graphics g) {
        if (!PNEditor.getRoot().getDocument().petriNet.isCurrentSubnetRoot()) {
            StringBuilder subnetPath = new StringBuilder("Subnet: ");
            for (Subnet subnet : PNEditor.getRoot().getDocument().petriNet.getOpenedSubnets()) {
                if (subnet != PNEditor.getRoot().getDocument().petriNet.getRootSubnet()) {
                    subnetPath.append(subnet.getLabel());
                    if (subnet != PNEditor.getRoot().getDocument().petriNet.getCurrentSubnet()) {
                        subnetPath.append(" > ");
                    }
                }
            }
            g.setColor(Color.darkGray);
            g.translate(-canvas.getTranslationX(), -canvas.getTranslationY());
            g.drawString(subnetPath.toString(), 2, 2 + g.getFontMetrics().getAscent());
            g.translate(canvas.getTranslationX(), canvas.getTranslationY());
        }
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
