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
import org.pneditor.petrinet.Element;
import org.pneditor.petrinet.ArcEdge;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import org.pneditor.editor.PNEditor;
import org.pneditor.petrinet.Edge;
import org.pneditor.editor.Root;
import org.pneditor.editor.commands.SetEdgeZigzagPointCommand;
import org.pneditor.petrinet.PlaceNode;
import org.pneditor.util.GraphicsTools;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
class EdgeZigzagFeature implements Feature {

    private Canvas canvas;

    EdgeZigzagFeature(Canvas canvas) {
        this.canvas = canvas;
        visualHandle.color = Colors.pointingColor;
        visualHandle.setSize(ArcEdge.nearTolerance, ArcEdge.nearTolerance);
    }

    private Edge edge;
    private Point activeBreakPoint;
    private boolean started = false;
    private VisualHandle visualHandle = new VisualHandle();
    private List<Element> foregroundVisualElements = new ArrayList<Element>();

    private Point startingMouseLocation;
    private List<Point> oldBreakPoints;

    public void mousePressed(MouseEvent event) {
        int x = event.getX();
        int y = event.getY();
        int mouseButton = event.getButton();

        if (mouseButton == MouseEvent.BUTTON1
                && PNEditor.getRoot().getClickedElement() != null
                && (PNEditor.getRoot().isSelectedTool_Select()
                || PNEditor.getRoot().isSelectedTool_Place()
                || PNEditor.getRoot().isSelectedTool_Transition()
                || PNEditor.getRoot().isSelectedTool_Arc()
                || PNEditor.getRoot().isSelectedTool_Token() && !(PNEditor.getRoot().getClickedElement() instanceof PlaceNode))
                && PNEditor.getRoot().getClickedElement() instanceof ArcEdge) {
            if (!PNEditor.getRoot().getSelection().contains(PNEditor.getRoot().getClickedElement())) {
                PNEditor.getRoot().getSelection().clear();
            }
            edge = (Edge) PNEditor.getRoot().getDocument().petriNet.getCurrentSubnet().getElementByXY(x, y);

            oldBreakPoints = edge.getBreakPointsCopy();
            startingMouseLocation = new Point(x, y);
            activeBreakPoint = edge.addOrGetBreakPoint(new Point(startingMouseLocation));
            started = true;
        }
    }

    public void mouseDragged(int x, int y) {
        if (started) {
            activeBreakPoint.move(x, y);
            canvas.repaint();
        }
    }

    public void mouseReleased(int x, int y) {
        if (started) {
            edge.cleanupUnecessaryBreakPoints();

            boolean change = false;
            if (oldBreakPoints.size() != edge.getBreakPoints().size()) {
                change = true;
            } else {
                for (int i = 0; i < edge.getBreakPoints().size(); i++) {
                    if (!edge.getBreakPoints().get(i).equals(oldBreakPoints.get(i))) {
                        change = true;
                        break;
                    }
                }
            }
            if (change) {
                edge.setBreakPoints(oldBreakPoints);
                Point targetLocation = new Point(x, y);
                PNEditor.getRoot().getUndoManager().executeCommand(new SetEdgeZigzagPointCommand(edge, startingMouseLocation, targetLocation));
            }
            started = false;
        }
    }

    public void setHoverEffects(int x, int y) {
        if (PNEditor.getRoot().isSelectedTool_Select()
                || PNEditor.getRoot().isSelectedTool_Place()
                || PNEditor.getRoot().isSelectedTool_Transition()
                || PNEditor.getRoot().isSelectedTool_Arc()
                || PNEditor.getRoot().isSelectedTool_Token()) {
            Element element = PNEditor.getRoot().getDocument().petriNet.getCurrentSubnet().getElementByXY(x, y);
            boolean drawHandle = false;
            if (element instanceof ArcEdge) {
                ArcEdge anArc = (ArcEdge) element;
                final Point mousePos = new Point(x, y);
                for (Point breakPoint : anArc.getBreakPoints()) {
                    if (GraphicsTools.isPointNearPoint(breakPoint, mousePos, ArcEdge.nearTolerance)) {
                        if (!foregroundVisualElements.contains(visualHandle)) {
                            foregroundVisualElements.add(visualHandle);
                        }
                        visualHandle.setCenter(breakPoint.x, breakPoint.y);
                        drawHandle = true;

                        break;
                    }
                }
            }
            if (!drawHandle) {
                foregroundVisualElements.remove(visualHandle);
            }

            if (element != null) {
                canvas.highlightedElements.add(element);
                element.highlightColor = Colors.pointingColor;
                canvas.repaint();
            }
        }
    }

    public void drawForeground(Graphics g) {
        for (Element element : foregroundVisualElements) {
            element.draw(g, null);
        }
    }

    public void setCursor(int x, int y) {
    }

    public void drawBackground(Graphics g) {
    }

    public void drawMainLayer(Graphics g) {
    }

    public void mouseMoved(int x, int y) {
    }
}
