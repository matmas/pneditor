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
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class DeleteArcCommand implements Command {

    private Arc arc;
    private boolean isAlreadyDeleted;

    public DeleteArcCommand(Arc arc) {
        this.arc = arc;
    }

    public void execute() {
        isAlreadyDeleted = !arc.getParentSubnet().getElements().contains(arc);
        if (!isAlreadyDeleted) {
            arc.getParentSubnet().removeElement(arc);
        }
    }

    public void undo() {
        if (!isAlreadyDeleted) {
            arc.getParentSubnet().addElement(arc);
        }
    }

    public void redo() {
        isAlreadyDeleted = !arc.getParentSubnet().getElements().contains(arc);
        if (!isAlreadyDeleted) {
            arc.getParentSubnet().removeElement(arc);
        }
    }

    @Override
    public String toString() {
        return "Delete arc";
    }

}
