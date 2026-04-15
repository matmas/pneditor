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
import org.pneditor.editor.Root;
import org.pneditor.petrinet.Element;
import org.pneditor.petrinet.Subnet;
import org.pneditor.util.GraphicsTools;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class OpenSubnetAction extends AbstractAction {

    private Root root;

    public OpenSubnetAction(Root root) {
        this.root = root;
        String name = "Open subnet";
        putValue(NAME, name);
        putValue(SMALL_ICON, GraphicsTools.getIcon("pneditor/opensubnet.gif"));
        putValue(SHORT_DESCRIPTION, name);
        putValue(MNEMONIC_KEY, KeyEvent.VK_O);
        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        Element clickedElement = root.getClickedElement();
        if (clickedElement != null) {
            if (clickedElement instanceof Subnet) {
                root.getDocument().petriNet.openSubnet((Subnet) clickedElement);
                root.setClickedElement(null);
                root.refreshAll();
            }
        }
    }
}
