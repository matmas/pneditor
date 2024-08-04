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
package org.pneditor.editor.commands;

import org.pneditor.petrinet.Element;
import org.pneditor.petrinet.Marking;
import org.pneditor.petrinet.Transition;
import org.pneditor.util.RecordableCommand;


/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class FireTransitionCommand implements RecordableCommand {

    private Transition transition;
    private Marking marking;

    public FireTransitionCommand(Transition transition, Marking marking) {
        this.transition = transition;
        this.marking = marking;
    }

    public void execute() {
        if (marking.isEnabled(transition)) {
            marking.fire(transition);
        }
    }

    public void undo() {
        if (marking.canBeUnfired(transition)) {
            marking.undoFire(transition);
        }
    }

    public void redo() {
        execute();
    }

	public Element getRecordedElement() {
		return transition;
	}
    
    @Override
    public String toString() {
        return "Fire transition";
    }


}
