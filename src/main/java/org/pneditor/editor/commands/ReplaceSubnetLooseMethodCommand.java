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

import java.util.HashMap;
import java.util.Map;
import org.pneditor.petrinet.Arc;
import org.pneditor.petrinet.PetriNet;
import org.pneditor.petrinet.Place;
import org.pneditor.petrinet.ReferencePlace;
import org.pneditor.petrinet.Subnet;
import org.pneditor.util.CollectionTools;
import org.pneditor.util.Command;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class ReplaceSubnetLooseMethodCommand implements Command {

    private Subnet subnet;
    private Subnet storedSubnet;
    private PetriNet petriNet;
    private Command replaceSubnet;

    public ReplaceSubnetLooseMethodCommand(Subnet subnet, Subnet storedSubnet, PetriNet petriNet) {
        this.subnet = subnet;
        this.storedSubnet = storedSubnet;
        this.petriNet = petriNet;
        replaceSubnet = new ReplaceSubnetCommand(subnet, storedSubnet, petriNet);
    }

    public void execute() {
        setArcMultiplicitiesToOne(subnet);
        replaceSubnet.execute();
        restoreArcMultiplicities(subnet);
    }

    public void undo() {
        setArcMultiplicitiesToOne(subnet);
        replaceSubnet.undo();
        restoreArcMultiplicities(subnet);
    }

    public void redo() {
        setArcMultiplicitiesToOne(subnet);
        replaceSubnet.redo();
        restoreArcMultiplicities(subnet);
    }

    private Map<Place, Integer> outFlows = new HashMap<Place, Integer>();
    private Map<Place, Integer> inFlows = new HashMap<Place, Integer>();

    private void setArcMultiplicitiesToOne(Subnet subnet) {
        outFlows.clear();
        inFlows.clear();
        for (ReferencePlace referencePlace : subnet.getReferencePlaces()) {
            Arc outArc = CollectionTools.getFirstElement(referencePlace.getConnectedArcs(true));
            if (outArc != null) {
                int outFlow = outArc.getMultiplicity();
                outFlows.put(referencePlace.getConnectedPlace(), outFlow);
            }
            for (Arc arc : referencePlace.getConnectedArcs(true)) {
                arc.setMultiplicity(1);
            }
            Arc inArc = CollectionTools.getFirstElement(referencePlace.getConnectedArcs(false));
            if (inArc != null) {
                int inFlow = inArc.getMultiplicity();
                inFlows.put(referencePlace.getConnectedPlace(), inFlow);
            }
            for (Arc arc : referencePlace.getConnectedArcs(false)) {
                arc.setMultiplicity(1);
            }
        }
    }

    private void restoreArcMultiplicities(Subnet subnet) {
        for (ReferencePlace referencePlace : subnet.getReferencePlaces()) {
            Place place = referencePlace.getConnectedPlace();
            if (place != null) {
                for (Arc arc : referencePlace.getConnectedArcs(true)) {
                    Integer multiplicity = outFlows.get(place);
                    if (multiplicity == null) {
                        multiplicity = inFlows.get(place);
                    }
                    arc.setMultiplicity(multiplicity);
                }
                for (Arc arc : referencePlace.getConnectedArcs(false)) {
                    Integer multiplicity = inFlows.get(place);
                    if (multiplicity == null) {
                        multiplicity = outFlows.get(place);
                    }
                    arc.setMultiplicity(multiplicity);
                }
            }
        }
    }
}
