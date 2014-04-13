/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pneditor.editor.commands;

import org.pneditor.petrinet.Arc;
import org.pneditor.util.Command;

/**
 *
 * @author Amodez
 */
public class SetArcInhibitCommand implements Command{
	
	private Arc arc;
	private boolean isInhib;
	
	public SetArcInhibitCommand(Arc arc, boolean isInhib) {
		this.arc = arc;
		this.isInhib = isInhib;
	}
	
	public void execute() {
		//oldMultiplicity = arc.getMultiplicity();
		arc.setInhibitory(isInhib);
	}

	public void undo() {
                //isInhib = !isInhib;
		arc.setInhibitory(!isInhib);
	}

	public void redo() {
		execute();
	}

	@Override
	public String toString() {
		return "Set arc inhibitory";
	}
	
}
