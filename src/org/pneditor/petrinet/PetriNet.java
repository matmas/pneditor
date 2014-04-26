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
import java.util.Collections;
import java.util.Stack;

/**
 * PetriNet class stores reference to the root subnet and manages a view of
 * currently opened subnet in form of a stack. Default view is only the root
 * subnet opened. Opening and closing subnets does not influence anything other
 * and serves only for informational purposes.
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class PetriNet {

    private Stack<Subnet> openedSubnets = new Stack<Subnet>();
    private Subnet rootSubnet;
    private Marking initialMarking = new Marking(this);
    private NodeSimpleIdGenerator nodeSimpleIdGenerator = new NodeSimpleIdGenerator(this);
    private NodeLabelGenerator nodeLabelGenerator = new NodeLabelGenerator(this);

    /**
     * Constructor. Creates a new Petri net with empty root subnet.
     */
    public PetriNet() {
        clear();
    }

    /**
     * Returns the root subnet of the Petri net. It is the only commonly used
     * method.
     *
     * @return the root subnet of the Petri net
     */
    public Subnet getRootSubnet() {
        return rootSubnet;
    }

    /**
     * Replaces the root subnet with a different one and thus destroys old
     * reference. Currently useful only for DocumentImporter.
     *
     * @param rootSubnet a subnet to replace with
     */
    public void setRootSubnet(Subnet rootSubnet) {
        this.rootSubnet = rootSubnet;
    }

    /**
     * Determines whether the are no opened subnets except the root subnet
     *
     * @return true if only root subnet is opened otherwise false
     */
    public boolean isCurrentSubnetRoot() {
        return getCurrentSubnet() == getRootSubnet();
    }

    /**
     * Returns currenly opened subnet.
     *
     * @return currenly opened subnet
     */
    public Subnet getCurrentSubnet() {
        return openedSubnets.peek();
    }

    /**
     * Replaces the root subnet with empty one and resets view so that opened
     * subnet is the new root subnet.
     */
    public void clear() {
        rootSubnet = new Subnet();
        resetView();
    }

    /**
     * Resets view, so that currently opened subnet is root subnet.
     */
    public void resetView() {
        openedSubnets.clear();
        openedSubnets.add(rootSubnet);
    }

    /**
     * Opens a subnet. Changes view, so that specified subnet is currently
     * opened. The specified subnet must be directly nested in currenly opened
     * subnet.
     *
     * @param subnet subnet to be opened
     */
    public void openSubnet(Subnet subnet) {
        openedSubnets.push(subnet);
    }

    /**
     * Closes currenly opened subnet, so that parent subnet becomes next opened.
     */
    public void closeSubnet() {
        if (!isCurrentSubnetRoot()) {
            openedSubnets.pop();
        }
    }

    /**
     * Returns a ordered collection of currently opened subnets, i.e. a path to
     * the currently opened subnet.
     *
     * @return collection of opened subnets
     */
    public Collection<Subnet> getOpenedSubnets() {
        return Collections.unmodifiableCollection(openedSubnets);
    }

    public Marking getInitialMarking() {
        return initialMarking;
    }

    @Deprecated
    public void setInitialMarking(Marking initialMarking) {
        this.initialMarking = initialMarking;
    }

    public NodeSimpleIdGenerator getNodeSimpleIdGenerator() {
        return nodeSimpleIdGenerator;
    }

    public NodeLabelGenerator getNodeLabelGenerator() {
        return nodeLabelGenerator;
    }

    public boolean hasStaticPlace() {
        for (Place place : getRootSubnet().getPlacesRecursively()) {
            if (place.isStatic()) {
                return true;
            }
        }
        return false;
    }
}
