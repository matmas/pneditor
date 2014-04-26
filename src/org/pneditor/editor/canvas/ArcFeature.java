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
import java.util.ArrayList;
import java.util.List;
import org.pneditor.editor.PNEditor;
import org.pneditor.editor.Root;
import org.pneditor.editor.commands.AddArcCommand;
import org.pneditor.editor.commands.AddReferenceArcCommand;
import org.pneditor.editor.commands.SetArcMultiplicityCommand;
import org.pneditor.petrinet.Arc;
import org.pneditor.petrinet.ArcEdge;
import org.pneditor.petrinet.Element;
import org.pneditor.petrinet.Node;
import org.pneditor.petrinet.PlaceNode;
import org.pneditor.petrinet.ReferenceArc;
import org.pneditor.petrinet.Subnet;
import org.pneditor.petrinet.Transition;
import org.pneditor.petrinet.TransitionNode;
import org.pneditor.util.CollectionTools;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
class ArcFeature implements Feature {

    private Canvas canvas;

    ArcFeature(Canvas canvas) {
        this.canvas = canvas;
    }

    private Element sourceElement = null;
    private Arc connectingArc = null;
    private List<Element> backgroundElements = new ArrayList<Element>();
    private boolean started = false;
    private Subnet currentSubnet;

    public void mousePressed(MouseEvent event) {
        int x = event.getX();
        int y = event.getY();
        int mouseButton = event.getButton();

        if (mouseButton == MouseEvent.BUTTON1
                && PNEditor.getRoot().isSelectedTool_Arc()
                && PNEditor.getRoot().getClickedElement() != null
                && PNEditor.getRoot().getClickedElement() instanceof Node
                && !started) {
            sourceElement = PNEditor.getRoot().getDocument().petriNet.getCurrentSubnet().getElementByXY(x, y);
            connectingArc = new Arc((Node) sourceElement);
            backgroundElements.add(connectingArc);
            started = true;
            currentSubnet = PNEditor.getRoot().getDocument().petriNet.getCurrentSubnet();
        }
    }

    public void mouseDragged(int x, int y) {
        if (PNEditor.getRoot().getDocument().petriNet.getCurrentSubnet() != currentSubnet) {
            cancelDragging();
        }

        Element targetElement = PNEditor.getRoot().getDocument().petriNet.getCurrentSubnet().getElementByXY(x, y);

        if (started) {
            if (targetElement != null && (sourceElement instanceof PlaceNode && targetElement instanceof TransitionNode
                    || sourceElement instanceof TransitionNode && targetElement instanceof PlaceNode)) {
                connectingArc.setEnd(targetElement.getCenter().x, targetElement.getCenter().y);
                connectingArc.setDestination((Node) targetElement);
            } else {
                connectingArc.setEnd(x, y);
                connectingArc.setSource(null);
                connectingArc.setDestination(null);
            }
            PNEditor.getRoot().repaintCanvas();
        }
    }

    public void mouseMoved(int x, int y) {
        mouseDragged(x, y);
    }

    public void mouseReleased(int x, int y) {
        if (PNEditor.getRoot().getDocument().petriNet.getCurrentSubnet() != currentSubnet) {
            cancelDragging();
        }
        Element targetElement = PNEditor.getRoot().getDocument().petriNet.getCurrentSubnet().getElementByXY(x, y);

        if (started) {
            connectingArc.setEnd(x, y);
            if (sourceElement != targetElement) {
                if (targetElement != null) {
                    if (sourceElement instanceof PlaceNode && targetElement instanceof TransitionNode
                            || sourceElement instanceof TransitionNode && targetElement instanceof PlaceNode) {
                        boolean placeToTransition = sourceElement instanceof PlaceNode && targetElement instanceof TransitionNode;
                        PlaceNode placeNode;
                        TransitionNode transitionNode;
                        if (placeToTransition) {
                            placeNode = (PlaceNode) sourceElement;
                            transitionNode = (TransitionNode) targetElement;
                        } else {
                            transitionNode = (TransitionNode) sourceElement;
                            placeNode = (PlaceNode) targetElement;
                        }

                        ArcEdge arcEdge = PNEditor.getRoot().getDocument().petriNet.getCurrentSubnet().getArcEdge(placeNode, transitionNode, placeToTransition);
                        ArcEdge counterArcEdge = PNEditor.getRoot().getDocument().petriNet.getCurrentSubnet().getArcEdge(placeNode, transitionNode, !placeToTransition);
                        if (counterArcEdge instanceof ReferenceArc) {
                            // never attempt make arc in opposite direction of ReferenceArc
                        } else if (arcEdge == null) {
                            // is there is no arc go ahead
                            if (transitionNode instanceof Transition) {
                                PNEditor.getRoot().getUndoManager().executeCommand(new AddArcCommand(placeNode, (Transition) transitionNode, placeToTransition));
                            } else if (transitionNode instanceof Subnet) {
                                PNEditor.getRoot().getUndoManager().executeCommand(new AddReferenceArcCommand(placeNode, (Subnet) transitionNode, PNEditor.getRoot().getDocument().petriNet));
                            } else {
                                throw new RuntimeException("transitionNode not instanceof Transition neither Subnet");
                            }

                            // newly created arcs are always first in subnet
                            PNEditor.getRoot().setClickedElement(CollectionTools.getFirstElement(PNEditor.getRoot().getDocument().petriNet.getCurrentSubnet().getElements()));
                        } else if (!(arcEdge instanceof ReferenceArc)) {
                            Arc arc = (Arc) arcEdge;
							// increase multiplicity
                            // but only if there is no ReferenceArc
                            PNEditor.getRoot().getUndoManager().executeCommand(new SetArcMultiplicityCommand(arc, arc.getMultiplicity() + 1));
                            PNEditor.getRoot().setClickedElement(arcEdge);
                        }
                    }
                }
                cancelDragging();
            }
        }
    }

    public void setHoverEffects(int x, int y) {
        Element targetElement = PNEditor.getRoot().getDocument().petriNet.getCurrentSubnet().getElementByXY(x, y);
        if (PNEditor.getRoot().isSelectedTool_Arc()) {
            if (started) { // Connecting to something...
                if (targetElement == null) { // Connecting to air
                    canvas.highlightedElements.add(sourceElement);
                    sourceElement.highlightColor = Colors.pointingColor;
                    PNEditor.getRoot().repaintCanvas();
                } else { // Connecting to solid element
                    if (sourceElement instanceof PlaceNode && targetElement instanceof TransitionNode
                            || sourceElement instanceof TransitionNode && targetElement instanceof PlaceNode) {
                        canvas.highlightedElements.add(sourceElement);
                        canvas.highlightedElements.add(targetElement);
                        sourceElement.highlightColor = Colors.connectingColor;
                        targetElement.highlightColor = Colors.connectingColor;
                        PNEditor.getRoot().repaintCanvas();
                    } else if (sourceElement == targetElement) {
                        canvas.highlightedElements.add(sourceElement);
                        sourceElement.highlightColor = Colors.pointingColor;
                        PNEditor.getRoot().repaintCanvas();
                    } else if (targetElement instanceof Node) { // Wrong combination
                        canvas.highlightedElements.add(sourceElement);
                        canvas.highlightedElements.add(targetElement);
                        sourceElement.highlightColor = Colors.disallowedColor;
                        targetElement.highlightColor = Colors.disallowedColor;
                        PNEditor.getRoot().repaintCanvas();
                    }
                }
            } else {
                if (targetElement != null) {
                    canvas.highlightedElements.add(targetElement);
                    targetElement.highlightColor = Colors.pointingColor;
                    PNEditor.getRoot().repaintCanvas();
                }
            }
        }
    }

    public void drawBackground(Graphics g) {
        for (Element element : backgroundElements) {
            element.draw(g, null);
        }
    }

    public void drawForeground(Graphics g) {
    }

    public void setCursor(int x, int y) {
    }

    public void drawMainLayer(Graphics g) {
    }

    private void cancelDragging() {
        sourceElement = null;
        backgroundElements.remove(connectingArc);
        started = false;
        PNEditor.getRoot().repaintCanvas();
    }
}
