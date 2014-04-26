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
import javax.swing.KeyStroke;
import org.pneditor.editor.Root;
import org.pneditor.editor.commands.SetLabelCommand;
import org.pneditor.petrinet.Node;
import org.pneditor.util.GraphicsTools;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class SetLabelAction extends AbstractAction {

    private Root root;

    public SetLabelAction(Root root) {
        this.root = root;
        String name = "Set label";
        putValue(NAME, name);
        putValue(SMALL_ICON, GraphicsTools.getIcon("pneditor/label.gif"));
        putValue(SHORT_DESCRIPTION, name);
//		putValue(MNEMONIC_KEY, KeyEvent.VK_R);
//		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("R"));
        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        if (root.getClickedElement() != null
                && root.getClickedElement() instanceof Node) {
            Node clickedNode = (Node) root.getClickedElement();
            String newLabel = JOptionPane.showInputDialog(root.getParentFrame(), "New label:", clickedNode.getLabel());

            if (newLabel != null && !newLabel.equals(clickedNode.getLabel())) {
                root.getUndoManager().executeCommand(new SetLabelCommand(clickedNode, newLabel));
            }

        }
    }
}
