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

import org.pneditor.petrinet.Node;
import org.pneditor.util.Command;

/**
 * Set label to clicked element
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class SetLabelCommand implements Command {

    private Node node;
    private String newLabel;
    private String oldLabel;

    public SetLabelCommand(Node node, String newLabel) {
        this.node = node;
        this.newLabel = newLabel;
    }

    public void execute() {
        this.oldLabel = node.getLabel();
        node.setLabel(newLabel);
    }

    public void undo() {
        node.setLabel(oldLabel);
    }

    public void redo() {
        execute();
    }

    @Override
    public String toString() {
        return "Set label to " + newLabel;
    }

}
