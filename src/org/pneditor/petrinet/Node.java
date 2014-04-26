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
import java.util.HashSet;
import java.util.Set;
import org.pneditor.editor.PNEditor;
import org.pneditor.util.GraphicsTools;
import org.pneditor.util.GraphicsTools.HorizontalAlignment;
import org.pneditor.util.GraphicsTools.VerticalAlignment;

/**
 * Transition, Place, ReferencePlace, Subnet are subclasses of Node
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public abstract class Node extends Element implements Comparable<Node> {

    private String id;
    private String label;

    public Node() {
        setSize(32, 32);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Set<ArcEdge> getConnectedArcEdges() {
        Set<ArcEdge> connectedArcEdges = new HashSet<ArcEdge>();
        for (ArcEdge arcEdge : getParentSubnet().getArcEdges()) {
            if (arcEdge.getSource() == this || arcEdge.getDestination() == this) {
                connectedArcEdges.add(arcEdge);
            }
        }
        return connectedArcEdges;
    }

    public Set<Arc> getConnectedArcs() {
        Set<Arc> connectedArcs = new HashSet<Arc>();
        for (Arc arc : getParentSubnet().getArcs()) {
            if (arc.getSource() == this || arc.getDestination() == this) {
                connectedArcs.add(arc);
            }
        }
        return connectedArcs;
    }

    public Set<Arc> getConnectedArcs(boolean placeToTransition) {
        Set<Arc> connectedArcs = new HashSet<Arc>();
        for (Arc arc : getParentSubnet().getArcs()) {
            if ((arc.getSource() == this || arc.getDestination() == this) && arc.isPlaceToTransition() == placeToTransition) {
                connectedArcs.add(arc);
            }
        }
        return connectedArcs;
    }

    public Arc getConnectedArcToNode(Node node) {
        for (Arc arc : getParentSubnet().getArcs()) {
            if (arc.getSource() == this && arc.getDestination() == node) {
                return arc;
            }
        }
        return null;
    }

    public Set<Arc> getConnectedArcsToAndFromNode(Node node) {
        Set<Arc> connectedArcs = new HashSet<Arc>();
        for (Arc arc : getParentSubnet().getArcs()) {
            if (arc.getSource() == this && arc.getDestination() == node
                    || arc.getSource() == node && arc.getDestination() == this) {
                connectedArcs.add(arc);
            }
        }
        return connectedArcs;
    }

    public Set<ReferenceArc> getConnectedReferenceArcs() {
        Set<ReferenceArc> connectedReferenceArcs = new HashSet<ReferenceArc>();
        for (ReferenceArc referenceArc : getParentSubnet().getReferenceArcs()) {
            if (referenceArc.getSource() == this || referenceArc.getDestination() == this) {
                connectedReferenceArcs.add(referenceArc);
            }
        }
        return connectedReferenceArcs;
    }

    /**
     * Returns the label. Labels are not required to be unique.
     *
     * @return the label.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets a new label. Labels are not required to be unique.
     *
     * @param label - label to set.
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Returns full path with it's subnet labels prepended. Example:
     * subnetLabel1.subnetLabel2. ... .subnetLabelN.getLabel() between each
     * sublabel is dot (".") inserted. Full labels are not required to be
     * unique.
     *
     * @return full label with subnet labels prepended.
     */
    public String getFullLabel() {
        StringBuilder fullLabel = new StringBuilder();
        fullLabel.insert(0, getLabel());
        Subnet subnet = getParentSubnet();
        while (subnet != null && subnet.getParentSubnet() != null) {
            fullLabel.insert(0, subnet.getLabel() + ".");
            subnet = subnet.getParentSubnet();
        }
        return fullLabel.toString();
    }

    public Set<Node> getInputNodes() {
        Set<Node> inputNodes = new HashSet<Node>();
        for (Arc arc : this.getConnectedArcs()) {
            if (arc.getDestination() == this) {
                inputNodes.add(arc.getSource());
            }
        }
        return inputNodes;
    }

    public Set<Node> getOutputNodes() {
        Set<Node> outputNodes = new HashSet<Node>();
        for (Arc arc : this.getConnectedArcs()) {
            if (arc.getSource() == this) {
                outputNodes.add(arc.getDestination());
            }
        }
        return outputNodes;
    }

    public int compareTo(Node node) {
        if (this.getLabel() != null && node.getLabel() != null && !(this.getLabel().equals("") && node.getLabel().equals(""))) {
            return this.getLabel().compareTo(node.getLabel());
        } else {
            return this.getId().compareTo(node.getId());
        }
    }

    protected void drawLabel(Graphics g) {
        if (getLabel() != null && !getLabel().equals("")) {
            GraphicsTools.drawString(g, getLabel(), getCenter().x, getEnd().y, HorizontalAlignment.center, VerticalAlignment.top);
        }
//		GraphicsTools.drawString(g, getId(), getCenter().x, getStart().y, HorizontalAlignment.center, VerticalAlignment.bottom);
    }

    @Override
    public Node getClone() {
        Node node = (Node) super.getClone();
        node.label = this.label;
        PNEditor.getRoot().getDocument().getPetriNet().getNodeSimpleIdGenerator().setUniqueId(node);
        return node;
    }
}
