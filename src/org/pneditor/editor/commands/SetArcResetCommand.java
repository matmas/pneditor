/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pneditor.editor.commands;

import org.pneditor.petrinet.Arc;
import org.pneditor.util.Command;

/**
 *
 * @author jan.tancibok
 */
public class SetArcResetCommand implements Command{
	
	private Arc arc;
	private boolean isReset;
	
	public SetArcResetCommand(Arc arc, boolean reset) {
		this.arc = arc;
		this.isReset = reset;
	}
	
	public void execute() {
		arc.setReset(isReset);
	}

	public void undo() {
		arc.setReset(!isReset);
	}

	public void redo() {
		execute();
	}

	@Override
	public String toString() {
		return "Switch arc to reset arc";
	}	
}