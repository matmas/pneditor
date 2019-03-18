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
import javax.swing.KeyStroke;
import org.pneditor.editor.Root;
//import org.pneditor.editor.commands.DeleteElementsCommand;
import org.pneditor.petrinet.Element;
import org.pneditor.util.GraphicsTools;

/**
 *
 * @author Ladislas Ducerf <ladislas.ducerf at gmail.com>
 */
public class RecordMacroAction extends AbstractAction {

    private Root root;

    public RecordMacroAction(Root root) {
        this.root = root;
        String name = "Record macro of token operations";
        putValue(NAME, name);
        putValue(SMALL_ICON, GraphicsTools.getIcon("pneditor/macroRecord.gif"));
        putValue(SHORT_DESCRIPTION, name);
        setEnabled(true);
    }

    public void actionPerformed(ActionEvent e) {
		if(root.getMacroManager().getRecording()) { //currently recording
			root.getMacroManager().endRecording();
	        putValue(SMALL_ICON, GraphicsTools.getIcon("pneditor/macroRecord.gif"));
	        String name = "Record macro of the 'Edit tokens / Fire transitions' tool";
	        putValue(NAME, name);
	        putValue(SHORT_DESCRIPTION, name);

		}else { //currently not recording
			root.getMacroManager().beginRecording();
	        putValue(SMALL_ICON, GraphicsTools.getIcon("pneditor/macroStop.gif"));
	        String name = "Stop macro recording";
	        putValue(NAME, name);
	        putValue(SHORT_DESCRIPTION, name);

		}		
    }
}
