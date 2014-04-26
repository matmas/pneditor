package org.pneditor.editor.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import org.pneditor.editor.PNEditor;
import org.pneditor.editor.canvas.Selection;
import org.pneditor.petrinet.Element;
import org.pneditor.petrinet.PetriNet;

/**
 *
 * @author matmas
 */
public class SelectAllAction extends AbstractAction {

    public SelectAllAction() {
        String name = "Select All";
        putValue(NAME, name);
        putValue(SHORT_DESCRIPTION, name);
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl A"));
        setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PetriNet petriNet = PNEditor.getRoot().getDocument().getPetriNet();

        Selection selection = PNEditor.getRoot().getSelection();
        selection.clear();
        selection.addAll(petriNet.getCurrentSubnet().getElements());

        PNEditor.getRoot().refreshAll();
    }

//	@Override
//	public boolean shouldBeEnabled() {
//		PetriNet petriNet = PNEditor.getRoot().getDocument().getPetriNet();
//		return !petriNet.isEmpty();
//	}
}
