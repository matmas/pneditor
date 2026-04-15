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

import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import org.pneditor.editor.PNEditor;
import org.pneditor.editor.Root;
import org.pneditor.editor.commands.AddTokenCommand;
import org.pneditor.editor.commands.FireTransitionCommand;
import org.pneditor.editor.commands.RemoveTokenCommand;
import org.pneditor.petrinet.Element;
import org.pneditor.petrinet.Marking;
import org.pneditor.petrinet.PlaceNode;
import org.pneditor.petrinet.Transition;
import org.pneditor.util.GraphicsTools;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
class TokenFeature implements Feature {

    private Canvas canvas;

    private Cursor tokenCursor;
    private Cursor fireCursor;

    TokenFeature(Canvas canvas) {
        this.canvas = canvas;
        tokenCursor = GraphicsTools.getCursor("pneditor/canvas/token.gif", new Point(16, 0));
        fireCursor = GraphicsTools.getCursor("pneditor/canvas/fire.gif", new Point(16, 0));
    }

    public void mousePressed(MouseEvent event) {
        int x = event.getX();
        int y = event.getY();
        int mouseButton = event.getButton();
        Marking initialMarking = PNEditor.getRoot().getCurrentMarking();

        if (PNEditor.getRoot().getClickedElement() != null
                && PNEditor.getRoot().isSelectedTool_Token()) {
            Element targetElement = PNEditor.getRoot().getDocument().petriNet.getCurrentSubnet().getElementByXY(x, y);

            if (targetElement instanceof PlaceNode) {
                PlaceNode placeNode = (PlaceNode) targetElement;
                if (mouseButton == MouseEvent.BUTTON1) {
                    PNEditor.getRoot().getUndoManager().executeCommand(new AddTokenCommand(placeNode, initialMarking));
                } else if (mouseButton == MouseEvent.BUTTON3) {
                    if (initialMarking.getTokens(placeNode) > 0) {
                        PNEditor.getRoot().getUndoManager().executeCommand(new RemoveTokenCommand(placeNode, initialMarking));
                    }
                }
            } else if (targetElement instanceof Transition) {
                Transition transition = (Transition) targetElement;
                if (mouseButton == MouseEvent.BUTTON1) {
                    if (initialMarking.isEnabled(transition)) {
                        PNEditor.getRoot().getUndoManager().executeCommand(new FireTransitionCommand(transition, initialMarking));
                    }
                }
            }
        }
    }

    public void setHoverEffects(int x, int y) {
        Element targetElement = PNEditor.getRoot().getDocument().petriNet.getCurrentSubnet().getElementByXY(x, y);
        Marking initialMarking = PNEditor.getRoot().getCurrentMarking();

        if (PNEditor.getRoot().isSelectedTool_Token()) {
            if (targetElement instanceof PlaceNode) {
                canvas.highlightedElements.add(targetElement);
                targetElement.highlightColor = Colors.pointingColor;
                canvas.repaint();
            } else if (targetElement instanceof Transition) {
                if (initialMarking.isEnabled((Transition) targetElement)) {
                    canvas.highlightedElements.add(targetElement);
                    targetElement.highlightColor = Colors.permittedColor;
                    canvas.repaint();
                } else {
                    canvas.highlightedElements.add(targetElement);
                    targetElement.highlightColor = Colors.disallowedColor;
                    canvas.repaint();
                }
            }
        }
    }

    public void drawForeground(Graphics g) {
        Marking initialMarking = PNEditor.getRoot().getCurrentMarking();

        if (PNEditor.getRoot().isSelectedTool_Token()) {
            for (Element element : PNEditor.getRoot().getDocument().petriNet.getCurrentSubnet().getElements()) {
                if (element instanceof Transition) {
                    Transition transition = (Transition) element;
                    if (initialMarking.isEnabled(transition)) {
                        g.setColor(Colors.permittedColor);
                    } else {
                        g.setColor(Colors.disallowedColor);
                    }
                    ((Graphics2D) g).setStroke(new BasicStroke(2f));
                    g.drawRect(transition.getStart().x + 1, transition.getStart().y + 1, transition.getWidth() - 3, transition.getHeight() - 3);
                    ((Graphics2D) g).setStroke(new BasicStroke(1f));
                }
            }
        }
    }

    public void setCursor(int x, int y) {
        Element targetElement = PNEditor.getRoot().getDocument().petriNet.getCurrentSubnet().getElementByXY(x, y);

        if (PNEditor.getRoot().isSelectedTool_Token()
                && targetElement != null) {
            if (targetElement instanceof PlaceNode) {

                canvas.alternativeCursor = tokenCursor;
            } else if (targetElement instanceof Transition) {
                canvas.alternativeCursor = fireCursor;
            }
        }

    }

    public void drawBackground(Graphics g) {
    }

    public void mouseDragged(int x, int y) {
    }

    public void mouseReleased(int x, int y) {
    }

    public void drawMainLayer(Graphics g) {
    }

    public void mouseMoved(int x, int y) {
    }
}
