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

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class ReferenceArc extends ArcEdge {

    public ReferenceArc(PlaceNode placeNode, Subnet subnet) {
        super(placeNode, subnet, true); //true or false - it is the same. TODO: update it is not the same because of breakpoints order from source to destination node
    }

    public Subnet getSubnet() {
        return (Subnet) getTransitionNode();
    }

    public ReferencePlace getReferencePlace() {
        for (Element element : getSubnet().getElements()) {
            if (element instanceof ReferencePlace) {
                ReferencePlace referencePlace = (ReferencePlace) element;
                if (referencePlace.getConnectedPlaceNode() == getPlaceNode()) {
                    return referencePlace;
                }
            }
        }
        throw new RuntimeException("ReferenceArc: missing ReferencePlace");
    }

    @Override
    public void draw(Graphics g, DrawingOptions drawingOptions) {
//		Arc subnetArc = null;
//		for (Arc arc : referencePlace.getConnectedArcs()) {
//			subnetArc = arc;
//		}

//		if (referencePlace.getConnectedNodes().size() == 1 &&
//			referencePlace.getConnectedArcs().size() == 1 &&
//			subnetArc instanceof ReferenceArc) {
//			for (Arc arc : referencePlace.getConnectedArcs()) {
//				this.multiplicity = arc.multiplicity;
//				setDirection(arc.placeToTransition);
//				super.draw(g);
//			}
//		}
        ReferencePlace referencePlace = getReferencePlace();
        if (referencePlace.getConnectedTransitionNodes().size() == 1) {
            g.setColor(color);
            GraphicsTools.setDashedStroke(g);
            drawSegmentedLine(g);

            for (Arc arc : referencePlace.getConnectedArcs()) { //TODO: also referenceArcs
                setPlaceToTransition(arc.isPlaceToTransition());
                Point arrowTip = computeArrowTipPoint();
                drawArrow(g, arrowTip);

                if (referencePlace.getConnectedArcEdges().size() > 1 || arc.getMultiplicity() > 1) {
                    drawMultiplicityLabel(g, arrowTip, arc.getMultiplicity());
                }
            }
            GraphicsTools.setDefaultStroke(g);
        } else if (referencePlace.getConnectedTransitionNodes().isEmpty()) {
            GraphicsTools.setDottedStroke(g);
            drawSegmentedLine(g);
            GraphicsTools.setDefaultStroke(g);
        } else {
            GraphicsTools.setDashedStroke(g);
            drawSegmentedLine(g);
            GraphicsTools.setDefaultStroke(g);
        }
    }

}
