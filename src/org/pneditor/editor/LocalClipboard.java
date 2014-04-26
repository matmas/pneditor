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
package org.pneditor.editor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import org.pneditor.petrinet.Arc;
import org.pneditor.petrinet.ArcEdge;
import org.pneditor.petrinet.Element;
import org.pneditor.petrinet.ElementCloner;
import org.pneditor.petrinet.Marking;
import org.pneditor.petrinet.Node;
import org.pneditor.petrinet.PetriNet;
import org.pneditor.petrinet.Place;
import org.pneditor.petrinet.PlaceNode;
import org.pneditor.petrinet.Subnet;
import org.pneditor.petrinet.Transition;
import org.pneditor.petrinet.TransitionNode;
import org.pneditor.util.CollectionTools;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class LocalClipboard {

    private Subnet subnet = new Subnet();

    public LocalClipboard() {
    }

    public void setContents(Set<Element> elements, PetriNet petriNet) {
        subnet.removeElements();
        elements = filterOutDisconnectedArcs(elements);
        elements = ElementCloner.getClones(elements, petriNet);
        subnet.addAll(elements);
    }

    public Set<Element> getContents(PetriNet petriNet) {
        return ElementCloner.getClones(subnet.getElements(), petriNet);
    }

    public boolean isEmpty() {
        return subnet.getElements().isEmpty();
    }

    private static Set<Element> filterOutDisconnectedArcs(Set<Element> elements) {
        Set<Element> filteredElements = new HashSet<Element>();
        Set<Node> nodes = getNodes(elements);
        for (Node node : nodes) {
            Set<ArcEdge> connectedArcEdges = node.getConnectedArcEdges();
            for (ArcEdge connectedArcEdge : connectedArcEdges) {
                if (nodes.contains(connectedArcEdge.getPlaceNode()) && nodes.contains(connectedArcEdge.getTransitionNode())) {
                    filteredElements.add(connectedArcEdge);
                }
            }
        }
        filteredElements.addAll(nodes);
        return filteredElements;
    }

    public static Set<Node> getNodes(Set<Element> elements) {
        Set<Node> nodes = new HashSet<Node>();
        for (Element element : elements) {
            if (element instanceof Node) {
                nodes.add((Node) element);
            }
        }
        return nodes;
    }

    public Set<Node> getNodes() {
        Set<Node> nodes = new HashSet<Node>();
        for (Element element : subnet.getElements()) {
            if (element instanceof Node) {
                nodes.add((Node) element);
            }
        }
        return nodes;
    }

//	private PetriNet clipboardNet = new PetriNet();
//
//	public void setContents(Set<Element> elements, PetriNet petriNet) {
//		clipboardNet.clear();
//		Map<Element, Element> originalToCloneMap = makeOriginalToCloneMap(elements);
//		copyMarking(originalToCloneMap, petriNet.getInitialMarking(), clipboardNet.getInitialMarking());
//		clipboardNet.getRootSubnet().addAll(new HashSet<Element>(originalToCloneMap.values()));
//	}
//
//	public Set<Element> getContents(PetriNet petriNet) {
//		Map<Element, Element> originalToCloneMap = makeOriginalToCloneMap(clipboardNet.getRootSubnet().getElements());
//		copyMarking(originalToCloneMap, clipboardNet.getInitialMarking(), petriNet.getInitialMarking());
//		return new HashSet<Element>(originalToCloneMap.values());
//	}
//
//	public boolean isEmpty() {
//		return clipboardNet.getRootSubnet().getElements().isEmpty();
//	}
//
//	// -------------------------------------------------------------------------
//
//	private void copyMarking(Map<Element, Element> originalToCloneMap, Marking sourceMarking, Marking destinationMarking) {
//		for (Place place : CollectionTools.getFilteredByClass(originalToCloneMap.keySet(), Place.class)) {
//			int tokens = sourceMarking.getTokens(place);
//			destinationMarking.setTokens((Place)originalToCloneMap.get(place), tokens);
//		}
//	}
//	
//	private static Map<Element, Element> makeOriginalToCloneMap(Collection<Element> elements) {
//		Map<Element, Element> clones = new Hashtable<Element, Element>();
//		Set<ArcEdge> connectedArcs = new HashSet<ArcEdge>();
//
//		for (Element element : elements) {
//			if (element instanceof Node) {
//				Node node = (Node)element;
//				clones.put(node, node.getClone());
//				connectedArcs.addAll(node.getConnectedArcEdges());
//			}
//		}
//
//		for (ArcEdge arcEdge : connectedArcs) {
//			if (elements.contains(arcEdge.getPlaceNode()) || elements.contains(arcEdge.getTransitionNode())) {
//				ArcEdge clonedArc = arcEdge.getClone();
//
//				if (elements.contains(arcEdge.getPlaceNode())) {
//					clonedArc.setPlaceNode((PlaceNode)clones.get(arcEdge.getPlaceNode()));
//                }
//				if (elements.contains(arcEdge.getTransitionNode())) {
//					clonedArc.setTransitionNode((TransitionNode)clones.get(arcEdge.getTransitionNode()));
//                }
//				clones.put(arcEdge, clonedArc);
//            }
//        }
//		return clones;
//	}
}
