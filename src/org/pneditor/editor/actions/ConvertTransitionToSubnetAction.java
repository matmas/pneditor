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
import java.util.Set;
import javax.swing.AbstractAction;
import org.pneditor.editor.Root;
import org.pneditor.petrinet.Element;
import org.pneditor.petrinet.Subnet;
import org.pneditor.petrinet.Transition;
import org.pneditor.editor.commands.ConvertTransitionsToSubnetsCommand;
import org.pneditor.util.GraphicsTools;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class ConvertTransitionToSubnetAction extends AbstractAction {

    private Root root;

    public ConvertTransitionToSubnetAction(Root root) {
        this.root = root;
        String name = "Convert transition(s) to subnet(s)";
        putValue(NAME, name);
        putValue(SMALL_ICON, GraphicsTools.getIcon("pneditor/converttransitiontosubnet.gif"));
        putValue(SHORT_DESCRIPTION, name);
        putValue(MNEMONIC_KEY, KeyEvent.VK_C);
        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        Set<Transition> selectedTransitions = new HashSet<Transition>();
        for (Element element : root.getSelectedElementsWithClickedElement()) {
            if (element instanceof Transition) {
                Transition transition = (Transition) element;
                selectedTransitions.add(transition);
            } else if (element instanceof Subnet) {
                Subnet subnet = (Subnet) element;
                selectedTransitions.addAll(subnet.getTransitionsRecursively());
            }
        }

        Set<Subnet> previousSubnets = root.getDocument().petriNet.getCurrentSubnet().getSubnets();

        root.getUndoManager().executeCommand(
                new ConvertTransitionsToSubnetsCommand(selectedTransitions, root.getDocument().petriNet)
        );

        Set<Subnet> createdElements = root.getDocument().petriNet.getCurrentSubnet().getSubnets();
        createdElements.removeAll(previousSubnets);

        root.getSelection().clear();
        root.getSelection().getElements().addAll(createdElements);
        root.getSelection().selectionChanged();

//		root.refreshAll(); TODO: root.refreshAll() does not help
    }
}
