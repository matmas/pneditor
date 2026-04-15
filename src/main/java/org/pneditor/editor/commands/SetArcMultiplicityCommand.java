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

import org.pneditor.petrinet.Arc;
import org.pneditor.util.Command;

/**
 * Set multiplicity to clicked arc
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class SetArcMultiplicityCommand implements Command {

    private Arc arc;
    private int newMultiplicity;
    private int oldMultiplicity;

    public SetArcMultiplicityCommand(Arc arc, int newMultiplicity) {
        this.arc = arc;
        this.newMultiplicity = newMultiplicity;
    }

    public void execute() {
        oldMultiplicity = arc.getMultiplicity();
        arc.setMultiplicity(newMultiplicity);
    }

    public void undo() {
        arc.setMultiplicity(oldMultiplicity);
    }

    public void redo() {
        execute();
    }

    @Override
    public String toString() {
        return "Set arc multiplicity";
    }

}
