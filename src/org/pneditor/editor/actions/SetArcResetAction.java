/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pneditor.editor.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.pneditor.editor.Root;
import org.pneditor.editor.commands.SetArcInhibitCommand;
import org.pneditor.editor.commands.SetArcResetCommand;
import org.pneditor.petrinet.Arc;
import org.pneditor.util.GraphicsTools;

/**
 *
 * @author jan.tancibok
 */
public class SetArcResetAction extends AbstractAction{

    private Root root;
	
	public SetArcResetAction(Root root) {
		this.root = root;
		String name = "Change arc to reset arc";
		putValue(NAME, name);
		putValue(SMALL_ICON, GraphicsTools.getIcon("pneditor/setarcresetaction.gif"));
		putValue(SHORT_DESCRIPTION, name);
		putValue(MNEMONIC_KEY, KeyEvent.VK_I);
		setEnabled(false);
	}

	public void actionPerformed(ActionEvent e) {
		if (root.getClickedElement() != null) {
			if (root.getClickedElement() instanceof Arc) {
				Arc arc = (Arc) root.getClickedElement();
				boolean isReset = !arc.getReset();
				//String response = JOptionPane.showInputDialog(root.getParentFrame(), "Multiplicity:", multiplicity);
				//arc.setInhibitory(!isInhib);
				root.getUndoManager().executeCommand(new SetArcResetCommand(arc, isReset));
				
				//if arc was inhibit then un-inhibit it
				if(arc.getInhibitory()){root.getUndoManager().executeCommand(new SetArcInhibitCommand(arc, false));}
			}
		}
	}
}