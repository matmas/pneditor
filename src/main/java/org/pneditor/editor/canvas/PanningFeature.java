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

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import org.pneditor.editor.PNEditor;
import org.pneditor.editor.Root;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
class PanningFeature implements Feature {

    private Canvas canvas;

    PanningFeature(Canvas canvas) {
        this.canvas = canvas;
    }

    private int prevDragX;
    private int prevDragY;
    boolean panning;

    public void mousePressed(MouseEvent event) {
        int x = event.getX();
        int y = event.getY();
        int mouseButton = event.getButton();

        if (mouseButton == MouseEvent.BUTTON2
                || mouseButton == MouseEvent.BUTTON1 && event.isControlDown()) {
            int realX = x + canvas.getTranslationX();
            int realY = y + canvas.getTranslationY();

            prevDragX = realX;
            prevDragY = realY;
            panning = true;
        }
    }

    public void mouseDragged(int x, int y) {
        if (panning) {
            int realX = x + canvas.getTranslationX();
            int realY = y + canvas.getTranslationY();

            doThePanning(realX, realY);
            canvas.repaint();
            prevDragX = realX;
            prevDragY = realY;
        }
    }

    public void mouseReleased(int x, int y) {
        if (panning) {
            int realX = x + canvas.getTranslationX();
            int realY = y + canvas.getTranslationY();

            doThePanning(realX, realY);
            canvas.repaint();
            panning = false;
        }
    }

    private void doThePanning(int mouseX, int mouseY) {
        Point viewTranslation = PNEditor.getRoot().getDocument().petriNet.getCurrentSubnet().getViewTranslation();
        viewTranslation.translate(mouseX - prevDragX, mouseY - prevDragY);
        PNEditor.getRoot().getDocument().petriNet.getCurrentSubnet().setViewTranslation(viewTranslation);
    }

    public void setCursor(int x, int y) {
        if (panning) {
            canvas.alternativeCursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
        }
    }

    public void drawForeground(Graphics g) {
    }

    public void drawBackground(Graphics g) {
    }

    public void setHoverEffects(int x, int y) {
    }

    public void drawMainLayer(Graphics g) {
    }

    public void mouseMoved(int x, int y) {
    }
}
