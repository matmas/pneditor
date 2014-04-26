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

import java.util.Set;
import org.pneditor.petrinet.Element;
import org.pneditor.util.Command;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class CutCommand implements Command {

    private Command deleteElements;

    public CutCommand(Set<Element> elementsToDelete) {
        deleteElements = new DeleteElementsCommand(elementsToDelete);
    }

    public void execute() {
        deleteElements.execute();
    }

    public void undo() {
        deleteElements.undo();
    }

    public void redo() {
        deleteElements.redo();
    }

    @Override
    public String toString() {
        return "Cut";
    }

}
