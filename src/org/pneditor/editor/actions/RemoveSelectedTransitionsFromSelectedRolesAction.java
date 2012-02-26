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

package org.pneditor.editor.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.AbstractAction;
import org.pneditor.editor.Root;
import org.pneditor.editor.commands.RemoveTransitionsFromRolesCommand;
import org.pneditor.petrinet.Subnet;
import org.pneditor.petrinet.Transition;
import org.pneditor.petrinet.Role;
import org.pneditor.util.CollectionTools;
import org.pneditor.util.GraphicsTools;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class RemoveSelectedTransitionsFromSelectedRolesAction extends AbstractAction {
	
	private Root root;
	
	public RemoveSelectedTransitionsFromSelectedRolesAction(Root root) {
		this.root = root;
		String name = "Remove transition(s) from role(s)";
		putValue(NAME, name);
		putValue(SMALL_ICON, GraphicsTools.getIcon("pneditor/removefromrole16.gif"));
		putValue(SHORT_DESCRIPTION, name);
		putValue(MNEMONIC_KEY, KeyEvent.VK_E);
		setEnabled(false);
	}

	public void actionPerformed(ActionEvent e) {
		List<Role> selectedRoles = root.getRoleEditor().getSelectedElements();
		Set<Transition> selectedTransitions = new HashSet<Transition>();
		selectedTransitions.addAll(root.getSelection().getTransitionsRecursively()); //TODO: cleanup - selection - included clickedElement
		if (root.getClickedElement() instanceof Subnet) {
			Subnet subnet = (Subnet)root.getClickedElement();
			selectedTransitions.addAll(subnet.getTransitionsRecursively());
		}
		else if (root.getClickedElement() instanceof Transition) {
			selectedTransitions.add((Transition)root.getClickedElement());
		}

		boolean change = false;
		for (Role role : selectedRoles) {
			if (CollectionTools.containsAtLeastOne(role.transitions, selectedTransitions)) {
				change = true;
				break;
			}
		}

		if ( !selectedRoles.isEmpty() && !selectedTransitions.isEmpty() && change) {
			root.getUndoManager().executeCommand(new RemoveTransitionsFromRolesCommand(selectedTransitions, selectedRoles));
		}
	}
}
