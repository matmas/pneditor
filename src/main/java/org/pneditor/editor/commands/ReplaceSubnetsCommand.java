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
import java.util.Set;
import org.pneditor.petrinet.PetriNet;
import org.pneditor.petrinet.Subnet;
import org.pneditor.util.CollectionTools;
import org.pneditor.util.Command;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class ReplaceSubnetsCommand implements Command {

    private Set<Command> replaceSubnets = new HashSet<Command>();

    public ReplaceSubnetsCommand(Set<Subnet> subnets, Subnet storedSubnet, PetriNet petriNet) {
        for (Subnet subnet : subnets) {
            replaceSubnets.add(new ReplaceSubnetCommand(subnet, storedSubnet, petriNet));
        }
    }

    public void execute() {
        for (Command command : replaceSubnets) {
            command.execute();
        }
    }

    public void undo() {
        for (Command command : replaceSubnets) {
            command.undo();
        }
    }

    public void redo() {
        for (Command command : replaceSubnets) {
            command.redo();
        }
    }

    @Override
    public String toString() {
        if (replaceSubnets.size() == 1) {
            return CollectionTools.getFirstElement(replaceSubnets).toString();
        }
        return "Replace subnets";
    }

}
