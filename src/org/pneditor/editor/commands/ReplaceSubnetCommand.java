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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.pneditor.petrinet.Arc;
import org.pneditor.petrinet.ArcEdge;
import org.pneditor.petrinet.Element;
import org.pneditor.petrinet.ElementCloner;
import org.pneditor.petrinet.PetriNet;
import org.pneditor.petrinet.Place;
import org.pneditor.petrinet.PlaceNode;
import org.pneditor.petrinet.ReferencePlace;
import org.pneditor.petrinet.Subnet;
import org.pneditor.petrinet.Transition;
import org.pneditor.util.CollectionTools;
import org.pneditor.util.Command;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class ReplaceSubnetCommand implements Command {

    private Subnet subnet;
    private Subnet storedSubnet;
    private List<Element> previousElements;
    private List<Element> newElements = new LinkedList<Element>();
    private Set<Command> deleteReferenceArcCommands = new HashSet<Command>();
    private PetriNet petriNet;

    public ReplaceSubnetCommand(Subnet subnet, Subnet storedSubnet, PetriNet petriNet) {
        this.petriNet = petriNet;
        this.subnet = subnet;
        Subnet clonedSubnet = ElementCloner.getClone(storedSubnet, petriNet);
        petriNet.getNodeLabelGenerator().setLabelsToReplacedSubnet(clonedSubnet);
        this.storedSubnet = clonedSubnet;

    }

    private Set<ReferencePlace> getReferencePlaces(Subnet subnet) {
        Set<ReferencePlace> referencePlaces = new HashSet<ReferencePlace>();
        for (Element element : subnet.getElements()) {
            if (element instanceof ReferencePlace) {
                ReferencePlace referencePlace = (ReferencePlace) element;
                referencePlaces.add(referencePlace);
            }
        }
        return referencePlaces;
    }

    private Set<ReferencePlace> getWeakEquivalents(ReferencePlace referencePlace, Set<ReferencePlace> referencePlaces) {
        Set<ReferencePlace> weakEquivalents = new HashSet<ReferencePlace>();
        for (ReferencePlace anotherReferencePlace : referencePlaces) {
            if (referencePlace != anotherReferencePlace) {
                if (weakEquivalentReferencePlaces(referencePlace, anotherReferencePlace)) {
                    weakEquivalents.add(anotherReferencePlace);
                }
            }
        }
        return weakEquivalents;
    }

    private boolean isWeakEquivalentWithAnotherFrom(ReferencePlace referencePlace, Set<ReferencePlace> referencePlaces) {
        return !getWeakEquivalents(referencePlace, referencePlaces).isEmpty();
    }

    private boolean isStrongEquivalentWithAllWeakEquivalents(ReferencePlace referencePlace, Set<ReferencePlace> referencePlaces) {
        Set<ReferencePlace> weakEquivalents = getWeakEquivalents(referencePlace, referencePlaces);
        for (ReferencePlace weakEquivalent : weakEquivalents) {
            if (!strongEquivalentReferencePlaces(referencePlace, weakEquivalent)) {
                return false;
            }
        }
        return true;
    }

    private boolean areAllStrongEquivalent(Set<ReferencePlace> referencePlaces) {
        ReferencePlace someEquivalent = CollectionTools.getFirstElement(referencePlaces);
        for (ReferencePlace referencePlace : referencePlaces) {
            if (!strongEquivalentReferencePlaces(referencePlace, someEquivalent)) {
                return false;
            }
        }
        return true;
    }

    private void connect(ReferencePlace storedReferencePlace, ReferencePlace previousReferencePlace, Set<ReferencePlace> resolved) {
        storedReferencePlace.setConnectedPlace(previousReferencePlace.getConnectedPlaceNode());
        resolved.add(storedReferencePlace);
        resolved.add(previousReferencePlace);
    }

    private ReferencePlace getNewReferencePlace(PlaceNode referencedPlaceNode, ReferencePlace model) {
        ReferencePlace newReferencePlace = new ReferencePlace(referencedPlaceNode);
        petriNet.getNodeSimpleIdGenerator().setUniqueId(newReferencePlace);
        newReferencePlace.setCenter(referencedPlaceNode.getCenter().x - subnet.getCenter().x,
                referencedPlaceNode.getCenter().y - subnet.getCenter().y);
        for (Arc arc : model.getConnectedArcs()) { //TODO: !!! what if there are ReferenceArcs?
            Arc newArc = new Arc(newReferencePlace, arc.getTransition(), arc.isPlaceToTransition());
            newElements.add(newArc);
        }
        newElements.add(newReferencePlace);
        return newReferencePlace;
    }

    public void execute() {
        Set<ReferencePlace> previousReferencePlaces = getReferencePlaces(subnet);
        Set<ReferencePlace> storedReferencePlaces = getReferencePlaces(storedSubnet);
        Set<ReferencePlace> resolved = new HashSet<ReferencePlace>();

        for (ReferencePlace storedReferencePlace : storedReferencePlaces) {
            if (!isWeakEquivalentWithAnotherFrom(storedReferencePlace, storedReferencePlaces)) {
                Set<ReferencePlace> weakEquivalentsFromPrevious = getWeakEquivalents(storedReferencePlace, previousReferencePlaces);
                if (weakEquivalentsFromPrevious.size() == 1) {
                    ReferencePlace previousWeakEquivalent = CollectionTools.getFirstElement(getWeakEquivalents(storedReferencePlace, previousReferencePlaces));
                    connect(storedReferencePlace, previousWeakEquivalent, resolved);
                }
            }
        }

        for (ReferencePlace storedReferencePlace : storedReferencePlaces) {
            if (!resolved.contains(storedReferencePlace)) {
                Set<ReferencePlace> storedEquivalents = getWeakEquivalents(storedReferencePlace, storedReferencePlaces);
                storedEquivalents.add(storedReferencePlace);

                Set<ReferencePlace> previousEquivalents = getWeakEquivalents(storedReferencePlace, previousReferencePlaces);

                if (areAllStrongEquivalent(storedEquivalents) && areAllStrongEquivalent(previousEquivalents)) {

                    for (ReferencePlace previousEquivalent : previousEquivalents) {
                        if (!resolved.contains(previousEquivalent)) {
                            ReferencePlace storedEquivalent = CollectionTools.getFirstElementNotIn(storedEquivalents, resolved);

                            if (storedEquivalent == null) {
                                ReferencePlace someStoredEquivalent = CollectionTools.getFirstElement(storedEquivalents);
                                storedEquivalent = getNewReferencePlace(previousEquivalent.getConnectedPlaceNode(), someStoredEquivalent);
                            }
                            connect(storedEquivalent, previousEquivalent, resolved);
                        }
                    }
                }
            }
        }

        Set<ReferencePlace> unresolvedStored = CollectionTools.getElementsNotIn(storedReferencePlaces, resolved);
        Set<ReferencePlace> unresolvedPrevious = CollectionTools.getElementsNotIn(previousReferencePlaces, resolved);
        if (areAllStrongEquivalent(unresolvedStored) && areAllStrongEquivalent(unresolvedPrevious)
                && unresolvedStored.size() >= 1 && unresolvedPrevious.size() >= 1) {
            for (ReferencePlace unresolvedOnePrevious : unresolvedPrevious) {
                ReferencePlace unresolvedOneStored = CollectionTools.getFirstElementNotIn(unresolvedStored, resolved);

                if (unresolvedOneStored == null) {
                    ReferencePlace someUnresolvedStored = CollectionTools.getFirstElement(unresolvedStored);
                    unresolvedOneStored = getNewReferencePlace(unresolvedOnePrevious.getConnectedPlaceNode(), someUnresolvedStored);
                }
                connect(unresolvedOneStored, unresolvedOnePrevious, resolved);
            }
        }

        for (ReferencePlace previousReferencePlace : previousReferencePlaces) {
            if (!resolved.contains(previousReferencePlace)) {
                deleteReferenceArcCommands.add(new DeleteReferenceArcCommand(previousReferencePlace.getReferenceArc()));
            }
        }
        for (Element element : storedSubnet.getElements()) {
            if (element instanceof ReferencePlace) {
                ReferencePlace referencePlace = (ReferencePlace) element;
                if (resolved.contains(referencePlace)) {
                    newElements.add(referencePlace);
                }
            } else {
                newElements.add(element);
            }
        }
        for (ReferencePlace storedReferencePlace : storedReferencePlaces) {
            if (!resolved.contains(storedReferencePlace)) {
                Place place = new Place();
                petriNet.getNodeSimpleIdGenerator().setUniqueId(place);
                place.setCenter(storedReferencePlace.getCenter().x, storedReferencePlace.getCenter().y);
                for (ArcEdge arc : storedReferencePlace.getConnectedArcEdges()) {
                    arc.setPlaceNode(place);
                }
                place.setLabel("?!");
                newElements.add(place);
            }
        }
        for (Element element : storedSubnet.getElements()) { //TODO: clone
            element.setParentSubnet(subnet);
        }
        previousElements = subnet.getElementsCopy();
        subnet.setElements(newElements);
        for (Command deleteReferenceArcCommand : deleteReferenceArcCommands) {
            deleteReferenceArcCommand.execute();
        }
    }

    public void undo() {
        subnet.setElements(previousElements);
        for (Command deleteReferenceArcCommand : deleteReferenceArcCommands) {
            deleteReferenceArcCommand.undo();
        }
    }

    public void redo() {
        subnet.setElements(newElements);
        for (Command deleteReferenceArcCommand : deleteReferenceArcCommands) {
            deleteReferenceArcCommand.redo();
        }
    }

    private boolean weakEquivalentReferencePlaces(ReferencePlace referencePlace1, ReferencePlace referencePlace2) {
        Set<Transition> resolved = new HashSet<Transition>();
        for (Transition transition1 : referencePlace1.getConnectedTransitionsRecursively()) {
            for (Transition transition2 : referencePlace2.getConnectedTransitionsRecursively()) {
                if (!resolved.contains(transition1) && !resolved.contains(transition2)) {
                    if (equivalentPlaceTransitionRelation(referencePlace1, transition1, referencePlace2, transition2)) {
                        resolved.add(transition1);
                        resolved.add(transition2);
                    }
                }
            }
        }
        if (resolved.containsAll(referencePlace1.getConnectedTransitionsRecursively()) && resolved.containsAll(referencePlace2.getConnectedTransitionsRecursively())) {
            return true;
        }
        return false;
    }

    private boolean strongEquivalentReferencePlaces(ReferencePlace referencePlace1, ReferencePlace referencePlace2) {
        return weakEquivalentReferencePlaces(referencePlace1, referencePlace2)
                && referencePlace1.getConnectedTransitionNodes().containsAll(referencePlace2.getConnectedTransitionNodes());
    }

    private boolean equivalentPlaceTransitionRelation(PlaceNode placeNode1, Transition transition1, PlaceNode placeNode2, Transition transition2) {
        Arc arc1pTt = placeNode1.getConnectedArc(transition1, true);
        Arc arc1tTp = placeNode1.getConnectedArc(transition1, false);
        Arc arc2pTt = placeNode2.getConnectedArc(transition2, true);
        Arc arc2tTp = placeNode2.getConnectedArc(transition2, false);
        return equivalentArcs(arc1pTt, arc2pTt) && equivalentArcs(arc1tTp, arc2tTp);
    }

    private boolean equivalentArcs(Arc arc1, Arc arc2) {
        if (arc1 == null && arc2 == null) {
            return true;
        } else if (arc1 == null || arc2 == null) {
            return false;
        } else if (arc1.getMultiplicity() == arc2.getMultiplicity()) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Replace subnet";
    }

}
