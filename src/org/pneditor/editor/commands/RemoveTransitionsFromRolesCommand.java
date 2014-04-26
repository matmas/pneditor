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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.pneditor.petrinet.Transition;
import org.pneditor.petrinet.Role;
import org.pneditor.util.Command;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class RemoveTransitionsFromRolesCommand implements Command {

    private Set<Transition> transitions;
    private List<Role> roles;
    private Map<Role, Set<Transition>> oldRoleTransitions;

    public RemoveTransitionsFromRolesCommand(Set<Transition> transitions, List<Role> roles) {
        this.transitions = transitions;
        this.roles = roles;
    }

    public void execute() {
        oldRoleTransitions = new HashMap<Role, Set<Transition>>();
        for (Role role : roles) {
            oldRoleTransitions.put(role, new HashSet<Transition>(role.transitions));
        }
        redo();
    }

    public void undo() {
        for (Role role : roles) {
            role.transitions.clear();
            role.transitions.addAll(oldRoleTransitions.get(role));
        }
    }

    public void redo() {
        for (Role role : roles) {
            role.transitions.removeAll(transitions);
        }
    }

    @Override
    public String toString() {
        return "Remove transition from role(s)";
    }

}
