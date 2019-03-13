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
import org.pneditor.util.GraphicsTools;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class ReferencePlace extends PlaceNode {

    private PlaceNode connectedPlaceNode;

    public ReferencePlace(PlaceNode connectedPlaceNode) {
        this.connectedPlaceNode = connectedPlaceNode;
    }

    public ReferenceArc getReferenceArc() {
        for (Element element : getParentSubnet().getParentSubnet().getElements()) {
            if (element instanceof ReferenceArc) {
                ReferenceArc referenceArc = (ReferenceArc) element;
                if (referenceArc.getPlaceNode() == connectedPlaceNode
                        && referenceArc.getTransitionNode() == getParentSubnet()) {
                    return referenceArc;
                }
            }
        }
        throw new RuntimeException("ReferencePlace: missing ReferencePlace");
    }

    public PlaceNode getConnectedPlaceNode() {
        return connectedPlaceNode;
    }

    public Place getConnectedPlace() {
        PlaceNode connPlaceNode = connectedPlaceNode;
        if (connPlaceNode == null) {
            return null;
        }
        while (connPlaceNode instanceof ReferencePlace && !(connPlaceNode instanceof Place)) {
            ReferencePlace connectedReferencePlace = (ReferencePlace) connPlaceNode;
            connPlaceNode = connectedReferencePlace.getConnectedPlaceNode();
        }
        return (Place) connPlaceNode;
    }

    public void setConnectedPlace(PlaceNode placeNode) {
        connectedPlaceNode = placeNode;
    }

    /**
     * This call is redirected to the connected PlaceNode. Returns "" if
     * connected PlaceNode is null.
     */
    @Override
    public String getLabel() {
        if (connectedPlaceNode == null) {
            return "";
        }
        return connectedPlaceNode.getLabel();
    }

    /**
     * This call is redirected to the connected PlaceNode.
     */
    @Override
    public void setLabel(String label) {
        connectedPlaceNode.setLabel(label);
    }

    /**
     * This call is redirected to the connected PlaceNode. Returns false if
     * connected PlaceNode is null.
     */
    @Override
    public boolean isStatic() {
        if (connectedPlaceNode == null) {
            return false;
        }
        return connectedPlaceNode.isStatic();
    }

    /**
     * This call is redirected to the connected PlaceNode.
     */
    @Override
    public void setStatic(boolean isStatic) {
        connectedPlaceNode.setStatic(isStatic);
    }

    @Override
    public int getTokenLimit() {
    	if (connectedPlaceNode == null) {
    		return 0;
    	}
    	return connectedPlaceNode.getTokenLimit();
    }
    
    @Override
    public void setTokenLimit(int tokenLimit) {
    	connectedPlaceNode.setTokenLimit(tokenLimit);
    }
    
    @Override
    protected void drawPlaceBorder(Graphics g) {
        GraphicsTools.setDashedStroke(g);
        super.drawPlaceBorder(g);
        GraphicsTools.setDefaultStroke(g);
    }

}
