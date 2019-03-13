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

import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import org.pneditor.util.GraphicsTools;
import org.pneditor.util.GraphicsTools.HorizontalAlignment;
import org.pneditor.util.GraphicsTools.VerticalAlignment;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public abstract class PlaceNode extends Node implements Cloneable {

    public Arc getConnectedArc(Transition transition, boolean placeToTransition) {
        for (Arc arc : getConnectedArcs()) { // TODO: !!! what if the arc is hidden behind ReferenceArc inside Subnet?
            if (arc.getTransition() == transition && arc.isPlaceToTransition() == placeToTransition) {
                return arc;
            }
        }
        return null;
    }

    public Set<ArcEdge> getConnectedArcEdges(TransitionNode transitionNode) {
        Set<ArcEdge> connectedArcEdgesToTransitionNode = new HashSet<ArcEdge>();
        for (ArcEdge arc : getConnectedArcEdges()) {
            if (arc.getTransitionNode() == transitionNode) {
                connectedArcEdgesToTransitionNode.add(arc);
            }
        }
        return connectedArcEdgesToTransitionNode;
    }

    public Set<Arc> getConnectedArcs(Transition transition) {
        Set<Arc> connectedArcsToTransition = new HashSet<Arc>();
        for (Arc arc : getConnectedArcs()) {
            if (arc.getTransitionNode() == transition) {
                connectedArcsToTransition.add(arc);
            }
        }
        return connectedArcsToTransition;
    }

    abstract public boolean isStatic();

    abstract public void setStatic(boolean isStatic);
    
    abstract public int getTokenLimit();
    
    abstract public void setTokenLimit(int tokenLimit);

    @Override
    public void draw(Graphics g, DrawingOptions drawingOptions) {
        if (isStatic()) {
            drawStaticShadow(g);
        }
        if (getTokenLimit()!=0) {
        	drawTokenLimit(g);
        }
        drawPlaceBackground(g);
        drawPlaceBorder(g);
        drawLabel(g);
        drawTokens(g, drawingOptions.getMarking());
    }

    protected void drawStaticShadow(Graphics g) {
        g.setColor(color);
        final int phase = 4;
        g.fillOval(getStart().x + phase, getStart().y + phase, getWidth() - 1, getHeight() - 1);
    }
    
    protected void drawTokenLimit(Graphics g) {
        if (getTokenLimit()!=0) {
            GraphicsTools.drawString(g, Integer.toString(getTokenLimit()), getCenter().x, getStart().y ,  HorizontalAlignment.center, VerticalAlignment.bottom);
        }
    }

    protected void drawPlaceBackground(Graphics g) {
        g.setColor(Color.white);
        g.fillOval(getStart().x, getStart().y, getWidth(), getHeight());
    }

    protected void drawPlaceBorder(Graphics g) {
        g.setColor(color);
        g.drawOval(getStart().x, getStart().y, getWidth() - 1, getHeight() - 1);
    }

    protected void drawTokens(Graphics g, Marking marking) {
        g.setColor(color);
        int x = getCenter().x;
        int y = getCenter().y;
        int tokenSpacing = getWidth() / 5;
        if (marking.getTokens(this) == 1) {
            drawTokenAsDot(g, x, y);
        } else if (marking.getTokens(this) == 2) {
            drawTokenAsDot(g, x - tokenSpacing, y + tokenSpacing);
            drawTokenAsDot(g, x + tokenSpacing, y - tokenSpacing);
        } else if (marking.getTokens(this) == 3) {
            drawTokenAsDot(g, x - tokenSpacing, y + tokenSpacing);
            drawTokenAsDot(g, x, y);
            drawTokenAsDot(g, x + tokenSpacing, y - tokenSpacing);
        } else if (marking.getTokens(this) == 4) {
            drawTokenAsDot(g, x - tokenSpacing, y - tokenSpacing);
            drawTokenAsDot(g, x - tokenSpacing, y + tokenSpacing);
            drawTokenAsDot(g, x + tokenSpacing, y - tokenSpacing);
            drawTokenAsDot(g, x + tokenSpacing, y + tokenSpacing);
        } else if (marking.getTokens(this) == 5) {
            drawTokenAsDot(g, x - tokenSpacing, y - tokenSpacing);
            drawTokenAsDot(g, x - tokenSpacing, y + tokenSpacing);
            drawTokenAsDot(g, x, y);
            drawTokenAsDot(g, x + tokenSpacing, y - tokenSpacing);
            drawTokenAsDot(g, x + tokenSpacing, y + tokenSpacing);
        } else if (marking.getTokens(this) == 6) {
            drawTokenAsDot(g, x - tokenSpacing, y - tokenSpacing);
            drawTokenAsDot(g, x - tokenSpacing, y);
            drawTokenAsDot(g, x - tokenSpacing, y + tokenSpacing);
            drawTokenAsDot(g, x + tokenSpacing, y - tokenSpacing);
            drawTokenAsDot(g, x + tokenSpacing, y);
            drawTokenAsDot(g, x + tokenSpacing, y + tokenSpacing);
        } else if (marking.getTokens(this) == 7) {
            drawTokenAsDot(g, x - tokenSpacing, y - tokenSpacing);
            drawTokenAsDot(g, x - tokenSpacing, y);
            drawTokenAsDot(g, x - tokenSpacing, y + tokenSpacing);
            drawTokenAsDot(g, x, y);
            drawTokenAsDot(g, x + tokenSpacing, y - tokenSpacing);
            drawTokenAsDot(g, x + tokenSpacing, y);
            drawTokenAsDot(g, x + tokenSpacing, y + tokenSpacing);
        } else if (marking.getTokens(this) == 8) {
            drawTokenAsDot(g, x - tokenSpacing, y - tokenSpacing);
            drawTokenAsDot(g, x - tokenSpacing, y);
            drawTokenAsDot(g, x - tokenSpacing, y + tokenSpacing);
            drawTokenAsDot(g, x, y - tokenSpacing);
            drawTokenAsDot(g, x, y + tokenSpacing);
            drawTokenAsDot(g, x + tokenSpacing, y - tokenSpacing);
            drawTokenAsDot(g, x + tokenSpacing, y);
            drawTokenAsDot(g, x + tokenSpacing, y + tokenSpacing);
        } else if (marking.getTokens(this) == 9) {
            drawTokenAsDot(g, x - tokenSpacing, y - tokenSpacing);
            drawTokenAsDot(g, x - tokenSpacing, y);
            drawTokenAsDot(g, x - tokenSpacing, y + tokenSpacing);
            drawTokenAsDot(g, x, y - tokenSpacing);
            drawTokenAsDot(g, x, y);
            drawTokenAsDot(g, x, y + tokenSpacing);
            drawTokenAsDot(g, x + tokenSpacing, y - tokenSpacing);
            drawTokenAsDot(g, x + tokenSpacing, y);
            drawTokenAsDot(g, x + tokenSpacing, y + tokenSpacing);
        } else if (marking.getTokens(this) > 9) {
            GraphicsTools.drawString(g, Integer.toString(marking.getTokens(this)), x, y, HorizontalAlignment.center, VerticalAlignment.center);
        }
    }

    private void drawTokenAsDot(Graphics g, int x, int y) {
        final int tokenSize = getWidth() / 6;
        g.fillOval(x - tokenSize / 2, y - tokenSize / 2, tokenSize, tokenSize);
    }

    @Override
    public boolean containsPoint(int x, int y) {
        // Check whether (x,y) is inside this oval, using the
        // mathematical equation of an ellipse.
        double rx = getWidth() / 2.0;   // horizontal radius of ellipse
        double ry = getHeight() / 2.0;  // vertical radius of ellipse 
        double cx = getStart().x + rx;   // x-coord of center of ellipse
        double cy = getStart().y + ry;    // y-coord of center of ellipse
        if ((ry * (x - cx)) * (ry * (x - cx)) + (rx * (y - cy)) * (rx * (y - cy)) <= rx * rx * ry * ry) {
            return true;
        } else {
            return false;
        }
    }

    public Set<TransitionNode> getConnectedTransitionNodes() {
        Set<TransitionNode> connectedTransitionNodes = new HashSet<TransitionNode>();
        for (ArcEdge arcEdge : getConnectedArcEdges()) {
            connectedTransitionNodes.add(arcEdge.getTransitionNode());
        }
        return connectedTransitionNodes;
    }

    /**
     * if referencePlace.getConnectedPlace() == null then it returns null too.
     */
    public Place getPlace() {
        Place place;
        if (this instanceof ReferencePlace) {
            ReferencePlace referencePlace = (ReferencePlace) this;
            place = referencePlace.getConnectedPlace();
        } else if (this instanceof Place) {
            place = (Place) this;
        } else {
            throw new RuntimeException("PlaceNode which is not ReferencePlace neither Place");
        }
        return place;
    }

    public Set<Transition> getConnectedTransitionsRecursively() {
        Set<Transition> connectedTransitions = new HashSet<Transition>();

        for (Arc arc : getConnectedArcs()) {
            connectedTransitions.add(arc.getTransition());
        }
        for (ReferenceArc referenceArc : getConnectedReferenceArcs()) {
//          Subnet subnet = referenceArc.getSubnet();
//          subnet.getre
//          for (Arc arc : subnet.get)
//          connectedTransitions.add();

            //TODO: !!! unfinished (!)
        }

        return connectedTransitions;
    }
}
