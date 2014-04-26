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
package org.pneditor.editor.commands;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;
import org.pneditor.petrinet.Arc;
import org.pneditor.petrinet.Element;
import org.pneditor.petrinet.PetriNet;
import org.pneditor.petrinet.PlaceNode;
import org.pneditor.petrinet.ReferencePlace;
import org.pneditor.petrinet.ReferenceArc;
import org.pneditor.petrinet.Subnet;
import org.pneditor.petrinet.Transition;
import org.pneditor.util.CollectionTools;
import org.pneditor.util.Command;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class ConvertTransitionToSubnetCommand implements Command {

    private Transition transition;
    private Subnet parentSubnet;
    private Subnet subnet;
    private Set<ReferencePlace> referencePlaces = new HashSet<ReferencePlace>();
    private Set<ReferenceArc> referenceArcs = new HashSet<ReferenceArc>();
    private Set<Arc> realArcs = new HashSet<Arc>();
    private PetriNet petriNet;
    private String transitionOldLabel;

    public ConvertTransitionToSubnetCommand(Transition transition, PetriNet petriNet) {
        this.transition = transition;
        this.parentSubnet = transition.getParentSubnet();
        this.petriNet = petriNet;
    }

    public void execute() {
        subnet = new Subnet();
        subnet.setCenter(transition.getCenter().x, transition.getCenter().y);
        petriNet.getNodeSimpleIdGenerator().setUniqueId(subnet);
        for (PlaceNode parentPlaceNode : transition.getConnectedPlaceNodes()) {

            ReferencePlace referencePlace = new ReferencePlace(parentPlaceNode);
            referencePlace.setCenter(
                    parentPlaceNode.getCenter().x - transition.getCenter().x,
                    parentPlaceNode.getCenter().y - transition.getCenter().y
            );
            petriNet.getNodeSimpleIdGenerator().setUniqueId(referencePlace);
            referencePlaces.add(referencePlace);

            realArcs.addAll(parentPlaceNode.getConnectedArcs(transition));

            Arc realArc = CollectionTools.getFirstElement(parentPlaceNode.getConnectedArcs(transition));
            ReferenceArc referenceArc = new ReferenceArc(parentPlaceNode, subnet);
            referenceArc.setPlaceToTransition(realArc.isPlaceToTransition());
            referenceArc.setBreakPoints(realArc.getBreakPoints());
            referenceArcs.add(referenceArc);

            subnet.addElement(referencePlace);
        }
        transitionOldLabel = transition.getLabel();
        redo();
    }

    public void undo() {
        transition.setLabel(transitionOldLabel);
        parentSubnet.removeElement(subnet);
        migrateElementToSubnet(transition, parentSubnet);
        transition.setCenter(subnet.getCenter().x, subnet.getCenter().y);
        for (Arc realArc : realArcs) {
            for (Point breakPoint : realArc.getBreakPoints()) {
                breakPoint.translate(
                        subnet.getCenter().x, subnet.getCenter().y
                );
            }
            migrateElementToSubnet(realArc, parentSubnet);

            ReferencePlace referencePlace = (ReferencePlace) realArc.getPlaceNode();
            realArc.setPlaceNode(referencePlace.getConnectedPlaceNode());
        }

        for (ReferenceArc referenceArc : referenceArcs) {
            parentSubnet.removeElement(referenceArc);
        }
    }

    public void redo() {
        petriNet.getNodeLabelGenerator().setLabelsOfConversionTransitionToSubnet(transition, subnet);

        parentSubnet.addElement(subnet);
        migrateElementToSubnet(transition, subnet);
        transition.setCenter(0, 0);
        for (Arc realArc : realArcs) {
            for (Point breakPoint : realArc.getBreakPoints()) {
                breakPoint.translate(
                        -subnet.getCenter().x, -subnet.getCenter().y
                );
            }
            migrateElementToSubnet(realArc, subnet);

            ReferencePlace referencePlace = getReferencePlace(realArc.getPlaceNode());
            realArc.setPlaceNode(referencePlace);
        }
        for (ReferenceArc referenceArc : referenceArcs) {
            parentSubnet.addElement(referenceArc);
        }

    }

    private ReferencePlace getReferencePlace(PlaceNode placeNode) {
        for (ReferencePlace referencePlace : referencePlaces) {
            if (referencePlace.getConnectedPlaceNode() == placeNode) {
                return referencePlace;
            }
        }
        throw new RuntimeException("ConvertTransitionToSubnetCommand: PlaceNode: missing ReferencePlace");
    }

    private void migrateElementToSubnet(Element element, Subnet subnet) {
        element.getParentSubnet().removeElement(element);
        subnet.addElement(element);
        element.setParentSubnet(subnet);
    }

    @Override
    public String toString() {
        return "Convert transition to subnet";
    }

}
