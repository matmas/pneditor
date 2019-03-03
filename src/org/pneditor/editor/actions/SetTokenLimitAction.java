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
import org.pneditor.editor.commands.SetTokenLimitCommand;
import org.pneditor.petrinet.Marking;
import org.pneditor.petrinet.Place;
import org.pneditor.petrinet.PlaceNode;
import org.pneditor.util.GraphicsTools;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */

/*
 * 
 * S'inspirer de setTokenAction ?
 * Notamment pour cas de mauvais input
 */
public class SetTokenLimitAction extends AbstractAction {

    private Root root;

    public SetTokenLimitAction(Root root) {
        this.root = root;
        String name = "Set token limit";
        putValue(NAME, name);
        putValue(SMALL_ICON, GraphicsTools.getIcon("pneditor/tokenLimit.gif"));
        putValue(SHORT_DESCRIPTION, name);
//      putValue(MNEMONIC_KEY, KeyEvent.VK_R);
//      putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("R"));
        setEnabled(false);
    }

    
    public void actionPerformed(ActionEvent e) {
    	Marking initialMarking = root.getDocument().petriNet.getInitialMarking();
        if (root.getClickedElement() != null) {
            if (root.getClickedElement() instanceof PlaceNode) {
                PlaceNode placeNode = (PlaceNode) root.getClickedElement();
                int tokenLimit = placeNode.getTokenLimit();
                String response = JOptionPane.showInputDialog(root.getParentFrame(), "Token limit (0 = no limit) :", tokenLimit);
                if (response != null) {
                    try {
                    	tokenLimit = Integer.parseInt(response);
                    } catch (NumberFormatException exception) {
                        JOptionPane.showMessageDialog(root.getParentFrame(), exception.getMessage() + " is not a number");
                    }

                    if (tokenLimit < 0) {
                    	tokenLimit = placeNode.getTokenLimit(); // restore old value
                        JOptionPane.showMessageDialog(root.getParentFrame(), "Token limit must be non-negative");
                    }
                }

                if (placeNode.getTokenLimit() != tokenLimit) {
                	//placeNode.setTokenLimit(tokenLimit);
                    root.getUndoManager().executeCommand(new SetTokenLimitCommand(placeNode, tokenLimit, initialMarking));
                }
            }
        }
    }
    
}
