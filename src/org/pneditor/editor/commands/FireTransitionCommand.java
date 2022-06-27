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

import org.pneditor.petrinet.Marking;
import org.pneditor.petrinet.Transition;
import org.pneditor.util.Command;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class FireTransitionCommand implements Command {

    private Transition transition;
    private Marking marking;
    private int _i= 1;
    /**
     * Overload the fireTransitionCommand 
     * @param transition : the transition activated
     * @param marking 
     * @param i  : the number of tokens put by the user
     */
    public FireTransitionCommand(Transition transition, Marking marking, int i) {
        this.transition = transition;
        this.marking = marking;
        this._i=i;
    }

    public FireTransitionCommand(Transition transition, Marking marking) {
        this.transition = transition;
        this.marking = marking;
    }

    public void execute() {
        if (marking.isEnabled(transition,_i)) {
            marking.fire(transition,_i);
        }
    }
/**
 * 
 * Marking.java receiver Call and UNDO management .
 */
    public void undo() {
        if (marking.canBeUnfired(transition)) {
            marking.undoFire(transition,_i);
        }
    }

    public void redo() {
        execute();
    }

    @Override
    public String toString() {
        return "Fire transition";
    }

}
