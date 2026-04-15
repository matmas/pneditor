/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pneditor.editor.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.pneditor.editor.Root;
import org.pneditor.editor.commands.DeleteElementCommand;
import org.pneditor.editor.commands.SetArcInhibitCommand;
import org.pneditor.editor.commands.SetArcMultiplicityCommand;
import org.pneditor.editor.commands.SetArcResetCommand;
import org.pneditor.petrinet.Arc;
import org.pneditor.util.GraphicsTools;

/**
 *
 * @author Amodez
 */
public class SetArcInhibitoryAction extends AbstractAction {

    private Root root;

    public SetArcInhibitoryAction(Root root) {
        this.root = root;
        String name = "Set/unset inhibitor arc type";
        putValue(NAME, name);
        putValue(SMALL_ICON, GraphicsTools.getIcon("pneditor/converttransitiontosubnet.gif"));
        putValue(SHORT_DESCRIPTION, name);
        putValue(MNEMONIC_KEY, KeyEvent.VK_I);
        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        if (root.getClickedElement() != null) {
            if (root.getClickedElement() instanceof Arc) {
                Arc arc = (Arc) root.getClickedElement();
                boolean isInhib = arc.getType().equals(Arc.INHIBITOR);
                root.getUndoManager().executeCommand(new SetArcInhibitCommand(arc, !isInhib));
            }
        }
    }
}
