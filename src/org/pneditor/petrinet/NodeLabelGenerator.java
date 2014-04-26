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

import java.util.Collection;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class NodeLabelGenerator {

    private int nextUniquePlaceNumber = 1;
    private int nextUniqueTransitionNumber = 1;
    private int nextUniqueSubnetNumber = 1;
    private PetriNet petriNet;

    public NodeLabelGenerator(PetriNet petriNet) {
        this.petriNet = petriNet;
    }

    private void setUniqueLabel(Node node) {
        String label;
        if (node instanceof Place) {
            label = getPrefix(node) + Integer.toString(nextUniquePlaceNumber++);
        } else if (node instanceof Transition) {
            label = getPrefix(node) + Integer.toString(nextUniqueTransitionNumber++);
        } else if (node instanceof Subnet) {
            label = getPrefix(node) + Integer.toString(nextUniqueSubnetNumber++);
        } else if (node instanceof ReferencePlace) {
            throw new RuntimeException("Why would anyone want to label a ReferencePlace?");
        } else {
            throw new RuntimeException("Node which is neither Place nor Transition nor Subnet nor ReferencePlace.");
        }
        node.setLabel(label);
    }

    private String getPrefix(Node node) {
        String prefix;
        if (node instanceof Place) {
            prefix = "p";
        } else if (node instanceof Transition) {
            prefix = "t";
        } else if (node instanceof Subnet) {
            prefix = "s";
        } else if (node instanceof ReferencePlace) {
            throw new RuntimeException("Why would anyone want to label a ReferencePlace?");
        } else {
            throw new RuntimeException("Node which is neither Place nor Transition nor Subnet nor ReferencePlace.");
        }
        return prefix;
    }

    public void setLabelToNewlyCreatedNode(Node node) {
//		setUniqueLabel(node);
    }

    public void setLabelsToPastedContent(Collection<Element> elements) {
        for (Element element : elements) {
            if (element instanceof Node && !(element instanceof ReferencePlace)) {
//				setUniqueLabel((Node)element);
            }
        }
    }

    public void setLabelsToReplacedSubnet(Subnet subnet) {
        for (Node node : subnet.getNodesRecursively()) {
            if (!(node instanceof ReferencePlace)) {
//				if (isNodeAutolabeled(node)) {
//					setUniqueLabel(node);
//				}
            }
        }
    }

    public void setLabelsOfConversionTransitionToSubnet(Transition transition, Subnet subnet) {
//		if (isNodeAutolabeled(transition)) {
//			setUniqueLabel(subnet);
//		}
//		else {
//			subnet.setLabel(transition.getLabel());
//			setUniqueLabel(transition);
//		}

        subnet.setLabel(transition.getLabel());
        transition.setLabel(null);
    }

    public void cloneLabel(Node newNode, Node oldNode) {
//		if (isNodeAutolabeled(oldNode)) {
//			setUniqueLabel(newNode);
//		}
//		else {
        newNode.setLabel(oldNode.getLabel());
//		}
    }

    private boolean isNodeAutolabeled(Node node) {
        return node.getLabel().matches("^" + getPrefix(node) + "[0-9]+$");
    }

    public void fixFutureUniqueLabels() {
        int maxPlaceNumber = 0;
        int maxTransitionNumber = 0;
        int maxSubnetNumber = 0;

        for (Place place : petriNet.getRootSubnet().getPlacesRecursively()) {
            String placeLabel = place.getLabel();
            if (placeLabel != null && placeLabel.startsWith(getPrefix(place))) {
                try {
                    int placeNumber = Integer.parseInt(placeLabel.substring(1));
                    if (placeNumber > maxPlaceNumber) {
                        maxPlaceNumber = placeNumber;
                    }
                } catch (NumberFormatException ex) {
                    //do nothing
                }
            }
        }
        for (Transition transition : petriNet.getRootSubnet().getTransitionsRecursively()) {
            String transitionLabel = transition.getLabel();
            if (transitionLabel != null && transitionLabel.startsWith(getPrefix(transition))) {
                try {
                    int transitionNumber = Integer.parseInt(transitionLabel.substring(1));
                    if (transitionNumber > maxTransitionNumber) {
                        maxTransitionNumber = transitionNumber;
                    }
                } catch (NumberFormatException ex) {
                    //do nothing
                }
            }
        }
        for (Subnet subnet : petriNet.getRootSubnet().getSubnetsRecursively()) {
            String subnetLabel = subnet.getLabel();
            if (subnetLabel != null && subnetLabel.startsWith(getPrefix(subnet))) {
                try {
                    int subnetNumber = Integer.parseInt(subnetLabel.substring(1));
                    if (subnetNumber > maxSubnetNumber) {
                        maxSubnetNumber = subnetNumber;
                    }
                } catch (NumberFormatException ex) {
                    //do nothing
                }
            }
        }
        nextUniquePlaceNumber = maxPlaceNumber + 1;
        nextUniqueTransitionNumber = maxTransitionNumber + 1;
        nextUniqueSubnetNumber = maxSubnetNumber + 1;
    }
}
