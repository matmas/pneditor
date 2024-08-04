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
package org.pneditor.editor;

import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import org.pneditor.editor.actions.RedoAction;
import org.pneditor.editor.actions.UndoAction;
import org.pneditor.util.Command;

/**
 * UndoManager provides the basic undo-redo capability.
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class UndoManager {

    private List<Command> executedCommands = new ArrayList<Command>();
    private int currentCommandIndex = -1;
    private Root root;
    private UndoAction undoAction;
    private RedoAction redoAction;

    /**
     * Constructs a new UndoManager
     *
     * @param root Root object
     * @param undoAction action for undo button
     * @param redoAction action for redo button
     */
    public UndoManager(Root root, UndoAction undoAction, RedoAction redoAction) {
        this.root = root;
        this.undoAction = undoAction;
        this.redoAction = redoAction;
    }

    /**
     * Execute command in UndoManager.
     *
     * @param command command to be executed
     */
    public void executeCommand(Command command) {
        List<Command> nonRedoedCommands = new ArrayList<Command>(executedCommands.subList(currentCommandIndex + 1, executedCommands.size()));
        executedCommands.removeAll(nonRedoedCommands);
        executedCommands.add(command);
        currentCommandIndex = executedCommands.size() - 1;
        command.execute();
        if (root.getMacroManager().getRecording()) {
        	root.getMacroManager().recordCommand(command);
        }
        refresh();
        root.setModified(true);
    }

    /**
     * Performs the undo action.
     */
    public void undoCommand() {
        if (isUndoable()) {
            Command command = executedCommands.get(currentCommandIndex);
            command.undo();
            currentCommandIndex--;
            refresh();
        }
        root.setModified(true);
    }

    /**
     * Performs the redo action.
     */
    public void redoNextCommand() {
        if (isRedoable()) {
            Command command = executedCommands.get(currentCommandIndex + 1);
            command.redo();
            currentCommandIndex++;
            refresh();
        }
        root.setModified(true);
    }

    /**
     * Determines if undo is possible.
     *
     * @return true if undo action is possible otherwise false
     */
    public boolean isUndoable() {
        return currentCommandIndex != -1;
    }

    /**
     * Determines if redo is possible.
     *
     * @return true if redo action is possible otherwise false
     */
    public boolean isRedoable() {
        return currentCommandIndex < executedCommands.size() - 1;
    }

    /**
     * Erases all commands from the undo manager.
     */
    public void eraseAll() {
        executedCommands = new ArrayList<Command>();
        currentCommandIndex = -1;
        refresh();
    }

    private void refresh() {
        root.refreshAll();
        if (isUndoable()) {
            undoAction.putValue(AbstractAction.SHORT_DESCRIPTION, "Undo: " + executedCommands.get(currentCommandIndex).toString());
        } else {
            undoAction.putValue(AbstractAction.SHORT_DESCRIPTION, "Undo");
        }
        if (isRedoable()) {
            redoAction.putValue(AbstractAction.SHORT_DESCRIPTION, "Redo: " + executedCommands.get(currentCommandIndex + 1).toString());
        } else {
            redoAction.putValue(AbstractAction.SHORT_DESCRIPTION, "Redo");
        }
    }

}
