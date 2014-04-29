/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pneditor.editor.commands;

import org.pneditor.petrinet.Arc;
import org.pneditor.util.Command;

/**
 *
 * @author Amodez
 */
public class SetArcInhibitCommand implements Command {

    private Arc arc;
    private boolean isInhib;
    private String oldType;
    
    public SetArcInhibitCommand(Arc arc, boolean isInhib) {
        this.arc = arc;
        this.isInhib = isInhib;
    }

    public void execute() {
        oldType = arc.getType();
        if (isInhib) {
            arc.setType(Arc.INHIBITOR);
        }
        else {
            arc.setType(Arc.REGULAR);
        }
    }

    public void undo() {
        arc.setType(oldType);
    }

    public void redo() {
        execute();
    }

    @Override
    public String toString() {
        return "Set arc type to inhibitor arc";
    }

}
