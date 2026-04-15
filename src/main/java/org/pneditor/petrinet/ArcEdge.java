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

import java.awt.Graphics;
import java.awt.Point;
import org.pneditor.util.GraphicsTools;
import org.pneditor.util.GraphicsTools.HorizontalAlignment;
import org.pneditor.util.GraphicsTools.VerticalAlignment;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public abstract class ArcEdge extends Edge implements Cloneable {

    public ArcEdge() {
    }

    public ArcEdge(PlaceNode placeNode, TransitionNode transitionNode, boolean placeToTransition) {
        if (placeToTransition) {
            setSource(placeNode);
            setDestination(transitionNode);
        } else {
            setSource(transitionNode);
            setDestination(placeNode);
        }
    }

    public PlaceNode getPlaceNode() {
        return isPlaceToTransition() ? (PlaceNode) getSource() : (PlaceNode) getDestination();
    }

    public void setPlaceNode(PlaceNode placeNode) {
        if (isPlaceToTransition()) {
            setSource(placeNode);
        } else {
            setDestination(placeNode);
        }
    }

    public TransitionNode getTransitionNode() {
        return isPlaceToTransition() ? (TransitionNode) getDestination() : (TransitionNode) getSource();
    }

    public void setTransitionNode(TransitionNode transition) {
        if (isPlaceToTransition()) {
            setDestination(transition);
        } else {
            setSource(transition);
        }
    }

    public boolean isPlaceToTransition() {
        return (getSource() instanceof PlaceNode);
    }

    public void setPlaceToTransition(boolean placeToTransition) {
        if (isPlaceToTransition() != placeToTransition) {
            reverseBreakPoints();
        }
        if (placeToTransition && getSource() instanceof TransitionNode && getDestination() instanceof PlaceNode) {
            TransitionNode transitionNode = (TransitionNode) getSource();
            PlaceNode placeNode = (PlaceNode) getDestination();
            setSource(placeNode);
            setDestination(transitionNode);
        }
        if (!placeToTransition && getSource() instanceof PlaceNode && getDestination() instanceof TransitionNode) {
            PlaceNode placeNode = (PlaceNode) getSource();
            TransitionNode transitionNode = (TransitionNode) getDestination();
            setSource(transitionNode);
            setDestination(placeNode);
        }
    }

    protected void drawArrow(Graphics g, Point arrowTip) {
        Point lastBreakPoint = getLastBreakPoint();
        GraphicsTools.drawArrow(g, lastBreakPoint.x, lastBreakPoint.y, arrowTip.x, arrowTip.y);
    }

    protected void drawArrowDouble(Graphics g, Point arrowTip) {
        Point lastBreakPoint = getLastBreakPoint();
        /*int dx =lastBreakPoint.x - arrowTip.x;
         int dy =lastBreakPoint.y - arrowTip.y;
         int px = 8;
         int py = (int) ((dy/dx) * px);
         GraphicsTools.drawArrow(g, lastBreakPoint.x, lastBreakPoint.y, arrowTip.x, arrowTip.y);*/
        GraphicsTools.drawArrowDouble(g, lastBreakPoint.x, lastBreakPoint.y, arrowTip.x, arrowTip.y);
    }

    protected void drawCircle(Graphics g, Point arrowTip) {
        Point lastBreakPoint = getLastBreakPoint();
        GraphicsTools.drawCircle(g, lastBreakPoint.x, lastBreakPoint.y, arrowTip.x, arrowTip.y);
    }

    protected void drawMultiplicityLabel(Graphics g, Point arrowTip, int multiplicity) {
        Point labelPoint = getLabelPoint(arrowTip);
        GraphicsTools.drawString(g, Integer.toString(multiplicity), labelPoint.x, labelPoint.y, HorizontalAlignment.center, VerticalAlignment.bottom);
    }

    @Override
    public ArcEdge getClone() {
        ArcEdge arcEdge = (ArcEdge) super.getClone();
        arcEdge.setSource(this.getSource());
        arcEdge.setDestination(this.getDestination());
        arcEdge.setBreakPoints(this.getBreakPointsCopy());
        return arcEdge;
    }
}
