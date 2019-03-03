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
import javax.swing.AbstractAction;
import org.pneditor.editor.Root;
import org.pneditor.editor.commands.SetUnsetPlaceStaticCommand;
import org.pneditor.petrinet.PlaceNode;
import org.pneditor.util.GraphicsTools;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class SetPlaceStaticAction extends AbstractAction {

    private Root root;

    public SetPlaceStaticAction(Root root) {
        this.root = root;
        String name = "Set/unset place static";
        putValue(NAME, name);
        putValue(SHORT_DESCRIPTION, name);
        putValue(SMALL_ICON, GraphicsTools.getIcon("pneditor/staticplace.gif"));
        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        if (root.getClickedElement() instanceof PlaceNode) {
            PlaceNode placeNode = (PlaceNode) root.getClickedElement();
            if (placeNode.getTokenLimit()==0)
            {
            	root.getUndoManager().executeCommand(new SetUnsetPlaceStaticCommand(placeNode));
            }
        }
    }

}
