/*
 * Copyright (C) 2012 milka
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
package org.pneditor.editor.actions.algorithms;

import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import org.pneditor.editor.Root;
import org.pneditor.petrinet.Element;
import org.pneditor.petrinet.PetriNet;

/**
 *
 * @author milka
 */
public class LimitationsAction extends AbstractAction {
	
	private Root root;
	
	public LimitationsAction(Root root) {
		this.root = root;
		String name = "Limitations";
		putValue(NAME, name);
		putValue(SHORT_DESCRIPTION, name);
		setEnabled(true);
	}
	
	public void actionPerformed(ActionEvent e) {
		
		JOptionPane.showMessageDialog(root.getParentFrame(), "Limitations Action performed", "Algorithm output", JOptionPane.INFORMATION_MESSAGE);
		
		PetriNet petriNet = root.getDocument().petriNet;
		List<Element> elements = petriNet.getRootSubnet().getElements();
		
	}
	
}
