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

import org.pneditor.petrinet.PetriNet;
import org.pneditor.petrinet.PlaceNode;
import org.pneditor.petrinet.ReferencePlace;
import org.pneditor.petrinet.ReferenceArc;
import org.pneditor.petrinet.Subnet;
import org.pneditor.util.Command;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class AddReferenceArcCommand implements Command {

    private Subnet parentSubnet;
    private PlaceNode placeNode;
    private Subnet nestedSubnet;
    private ReferenceArc createdReferenceArc;
    private ReferencePlace referencePlace;
    private PetriNet petriNet;

    public AddReferenceArcCommand(PlaceNode placeNode, Subnet nestedSubnet, PetriNet petriNet) {
        this.parentSubnet = placeNode.getParentSubnet();
        this.placeNode = placeNode;
        this.nestedSubnet = nestedSubnet;
        this.petriNet = petriNet;
    }

    public void execute() {
        referencePlace = new ReferencePlace(placeNode);
        referencePlace.setCenter(
                placeNode.getCenter().x - nestedSubnet.getCenter().x,
                placeNode.getCenter().y - nestedSubnet.getCenter().y
        );
        petriNet.getNodeSimpleIdGenerator().setUniqueId(referencePlace);
        createdReferenceArc = new ReferenceArc(placeNode, nestedSubnet);
        redo();
    }

    public void undo() {
        new DeleteElementCommand(createdReferenceArc).execute();
    }

    public void redo() {
        nestedSubnet.addElement(referencePlace);
        parentSubnet.addElement(createdReferenceArc);
    }

    @Override
    public String toString() {
        return "Add reference arc";
    }

}
