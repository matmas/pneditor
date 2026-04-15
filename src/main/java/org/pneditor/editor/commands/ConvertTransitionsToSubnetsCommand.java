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
import org.pneditor.petrinet.Transition;
import org.pneditor.util.CollectionTools;
import org.pneditor.util.Command;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class ConvertTransitionsToSubnetsCommand implements Command {

    private Set<Command> convertTransitionsToSubnets = new HashSet<Command>();

    public ConvertTransitionsToSubnetsCommand(Set<Transition> transitions, PetriNet petriNet) {
        for (Transition transition : transitions) {
            convertTransitionsToSubnets.add(new ConvertTransitionToSubnetCommand(transition, petriNet));
        }
    }

    public void execute() {
        for (Command command : convertTransitionsToSubnets) {
            command.execute();
        }
    }

    public void undo() {
        for (Command command : convertTransitionsToSubnets) {
            command.undo();
        }
    }

    public void redo() {
        for (Command command : convertTransitionsToSubnets) {
            command.redo();
        }
    }

    @Override
    public String toString() {
        if (convertTransitionsToSubnets.size() == 1) {
            return CollectionTools.getFirstElement(convertTransitionsToSubnets).toString();
        }
        return "Convert transitions to subnets";
    }

}
