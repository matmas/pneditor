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

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;
import org.pneditor.petrinet.Element;
import org.pneditor.util.Command;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class MoveElementsCommand implements Command {

    private Set<Command> moveElements = new HashSet<Command>();

    public MoveElementsCommand(Set<Element> elements, Point deltaPosition) {
        for (Element element : elements) {
            moveElements.add(new MoveElementCommand(element, deltaPosition));
        }
    }

    public void execute() {
        for (Command moveElement : moveElements) {
            moveElement.execute();
        }
    }

    public void undo() {
        for (Command moveElement : moveElements) {
            moveElement.undo();
        }
    }

    public void redo() {
        for (Command moveElement : moveElements) {
            moveElement.redo();
        }
    }

    @Override
    public String toString() {
        if (moveElements.size() == 1) {
            for (Command moveElement : moveElements) {
                return moveElement.toString();
            }
        }
        return "Move elements";
    }

}
