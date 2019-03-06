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
import org.pneditor.editor.actions.RecordMacroAction;
import org.pneditor.editor.actions.PlayMacroAction;
import org.pneditor.editor.actions.FastPlayMacroAction;
import org.pneditor.util.Command;
import org.pneditor.editor.UndoManager;
import java.util.concurrent.TimeUnit;

/**
 * MacroManager manages macro, and rely on UndoManager
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class MacroManager {


	//the list of commands, once recording is done
    private List<Command> recordedCommands = new ArrayList<Command>();
    // list of commands being recorded
    private List<Command> buffer = new ArrayList<Command>();
    /*
     * The separation of the two list allows for the saved macro to be played during
     * recording, so the new one can be composed of the old one
     */
    
    private int currentCommandIndex = -1;
    private Root root;
    private RecordMacroAction recordMacroAction;
    private PlayMacroAction playMacroAction;
    private FastPlayMacroAction fastPlayMacroAction;
    private boolean recording;
    private boolean playing;

    /**
     * Constructs a new MacroManager
     *
     * @param root Root object
     * @param undoAction action for undo button
     * @param redoAction action for redo button
     */
    public MacroManager(Root root, RecordMacroAction recordMacroAction, PlayMacroAction playMacroAction, FastPlayMacroAction fastPlayMacroAction) {
        this.root = root;
        this.recordMacroAction = recordMacroAction;
        this.playMacroAction = playMacroAction;
        this.fastPlayMacroAction = fastPlayMacroAction;
        this.recording = false;
        this.playing = false;
    }
    
    public void recordCommand(Command command) {
    	/*
        List<Command> nonRedoedCommands = new ArrayList<Command>(buffer.subList(currentCommandIndex + 1, buffer.size()));
        buffer.removeAll(nonRedoedCommands);
        */
    	//Do we want macro to be sensitive to undo/redo during recording ?
        buffer.add(command);
        currentCommandIndex = buffer.size() - 1;
        //command.execute();
        //refresh();
        //root.setModified(true);
    }
    
    public void beginRecording() {
    	this.recording = true;
    	eraseBuffer();
    	refresh();	
    }
    
    public void endRecording() {
    	this.recording = false;
    	copyBufferToRecordedCommands();
    	refresh();
    }
    
    
    
    public void playMacro(boolean fast) {
    	for (Command command : recordedCommands) {
    		command.execute();
    		refresh();
    		if (!fast) {
    			try {
    				TimeUnit.MILLISECONDS.sleep(500);
    			}
    			catch (InterruptedException e) {}
    		}
    	}
    	
    }

    /**
     * Erases all commands from the buffer.
     */
    public void eraseBuffer() {
        buffer = new ArrayList<Command>();
    }
    
    public void copyBufferToRecordedCommands() {
    	recordedCommands = new ArrayList<Command>(buffer);
    }
    
    public int getRecordedCommandsNumber() {
    	return recordedCommands.size();
    }
    
    public boolean getRecording() {
    	return recording;
    }
    
    public boolean getPlaying() {
    	return playing;
    }
    
    public void setPlaying(boolean set) {
    	playing = set;
    }
    
    public void setRecording(boolean set) {
    	recording  = set;
    }
    
    private void refresh() {
        root.refreshAll();
        /*
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
        */
    }

}
