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
import org.pneditor.editor.actions.RecordMacroAction;
import org.pneditor.editor.actions.PlayMacroAction;
import org.pneditor.util.Command;
import org.pneditor.util.GraphicsTools;
import javax.swing.AbstractAction;

import org.pneditor.util.RecordableCommand;


/**
 * MacroManager manages macro recording and playing
 *
 * @author Ladislas Ducerf <ladislas.ducerf at gmail.com>
 */
public class MacroManager {


	//the list of commands, once recording is done
    private List<RecordableCommand> recordedCommands = new ArrayList<RecordableCommand>();
    // list of commands being recorded
    private List<RecordableCommand> buffer = new ArrayList<RecordableCommand>();
    /*
     * The separation of the two list allows for the saved macro to be played during
     * recording, so the new one can be composed of the old one
     */
    
    private Root root;
    private PlayMacroAction playMacroAction;
    private boolean recording;

    /**
     * Constructs a new MacroManager
     *
     * @param root Root object
     * @param playMacroAction action for play macro button
     */
    public MacroManager(Root root, PlayMacroAction playMacroAction) {
        this.root = root;
        this.playMacroAction = playMacroAction;
        this.recording = false;
    }
    
    /**
     * Records a command in the buffer if it implements the RecordableCommand
     * interface
     * 
     * @param command the command to be recorded
     */
    public void recordCommand(Command command) {
    	//Do we want macro to be sensitive to undo/redo during recording ?
    	// Currently they are not
    	if(isRecordableCommand(command) ) {
	        buffer.add((RecordableCommand) command);
	        //currentCommandIndex = buffer.size() - 1;
    	}
    }

    /**
     * Returns true if a commands implements the RecordableCommand interface
     * 
     * @param command the command to be tested
     * @return
     */
    public boolean isRecordableCommand(Command command) {
    	return (command instanceof RecordableCommand);
    }
    
    /**
     * Puts the macroManager in recording mode and prepares everything
     */
    public void beginRecording() {
    	this.recording = true;
    	eraseBuffer();
    	refresh();	
    }
    
    /**
     * Puts the macroManager out of recording mode and saves the buffer
     */
    public void endRecording() {
    	this.recording = false;
    	copyBufferToRecordedCommands();
    	refresh();
    }
    
    
    /**
     * Identifies if all elements of the macro (places/nodes) are still existing
     * 
     * @return false if at least one element is missing, true otherwise
     */
    public boolean macroUnaffected() {
    	boolean unaffected = true;
    	for (RecordableCommand command : recordedCommands) {
    		if(! root.getDocument().petriNet.getCurrentSubnet().getElements().contains(command.getRecordedElement())) {
    			unaffected = false;
    			break;
    		}
    	}
    	return unaffected;
    }
    
    /**
     * Undo a played macro. Called by the undo method of the PlayMacro command 
     */
    public void undoMacro() {
    	System.out.println(recordedCommands.size());
    	for (int i = recordedCommands.size() - 1 ; i >= 0 ; i --) {
    		Command command  = recordedCommands.get(i);
    		System.out.println(command);
    		command.undo();
    		refresh();
    	}
    }
    

    /**
     * Execute a macro. Called by the execute method of the PlayMacro command
     */
    public void playMacro() {
    	for (Command command : recordedCommands) {
    		command.execute();
    		refresh();
    	}
    	
    }

    /**
     * Erases all commands from the buffer.
     */
    public void eraseBuffer() {
        buffer = new ArrayList<RecordableCommand>();
    }
    
    /**
     * Copy the buffer to the recorded commands
     */
    public void copyBufferToRecordedCommands() {
    	recordedCommands = new ArrayList<RecordableCommand>(buffer);
    }
    
    /**
     * Return the number of commands saved
     * 
     * @return the size of recordedCommands
     */
    public int getRecordedCommandsNumber() {
    	return recordedCommands.size();
    }
    
    
    public boolean getRecording() {
    	return recording;
    }
    

    private void refresh() {
        root.refreshAll();
    }
    
    public void refreshPlayIcon() {
        if (macroUnaffected()) {
        	playMacroAction.putValue(AbstractAction.SMALL_ICON, GraphicsTools.getIcon("pneditor/macroPlay.gif"));
        } else {
        	playMacroAction.putValue(AbstractAction.SMALL_ICON, GraphicsTools.getIcon("pneditor/macroPlayUncertain.gif"));
        }
    }

}
