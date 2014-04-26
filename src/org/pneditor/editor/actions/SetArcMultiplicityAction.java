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
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import org.pneditor.petrinet.Arc;
import org.pneditor.editor.Root;
import org.pneditor.editor.commands.DeleteElementCommand;
import org.pneditor.editor.commands.SetArcMultiplicityCommand;
import org.pneditor.util.GraphicsTools;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class SetArcMultiplicityAction extends AbstractAction {

    private Root root;

    public SetArcMultiplicityAction(Root root) {
        this.root = root;
        String name = "Set arc multiplicity";
        putValue(NAME, name);
        putValue(SMALL_ICON, GraphicsTools.getIcon("pneditor/multiplicity.gif"));
        putValue(SHORT_DESCRIPTION, name);
        putValue(MNEMONIC_KEY, KeyEvent.VK_M);
//		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("M"));
        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        if (root.getClickedElement() != null) {
            if (root.getClickedElement() instanceof Arc) {
                Arc arc = (Arc) root.getClickedElement();
                int multiplicity = arc.getMultiplicity();
                String response = JOptionPane.showInputDialog(root.getParentFrame(), "Multiplicity:", multiplicity);
                if (response != null) {
                    try {
                        multiplicity = Integer.parseInt(response);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(root.getParentFrame(), ex.getMessage() + " is not a number");
                    }
                }

                if (arc.getMultiplicity() != multiplicity) {
                    if (multiplicity < 1) {
                        root.getUndoManager().executeCommand(new DeleteElementCommand(arc));
                    } else {
                        root.getUndoManager().executeCommand(new SetArcMultiplicityCommand(arc, multiplicity));
                    }
                }
            }
        }
    }
}
