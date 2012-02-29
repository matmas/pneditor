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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import org.pneditor.editor.Root;
import org.pneditor.petrinet.*;

/**
 *
 * @author milka
 */
public class BoundednessAction extends AbstractAction {
	
	private Root root;
	
	private HashSet<Marking> checkedMarkings;
	private boolean isUnboundedness;
	
	public BoundednessAction(Root root) {
		this.root = root;
		String name = "Boundedness";
		putValue(NAME, name);
		putValue(SHORT_DESCRIPTION, name);
		setEnabled(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {

		isUnboundedness = false;
		PetriNet petriNet = root.getDocument().petriNet;
		
		Marking m0 = petriNet.getInitialMarking();
		checkedMarkings = new HashSet<Marking>();
		checkedMarkings.add(m0);
		
		Set<Transition> executableTransitions = m0.getAllEnabledTransitions();
		for(Transition t : executableTransitions) {
			checkBranchBoundedness(m0, t);
		}
		
		if (isUnboundedness)
			JOptionPane.showMessageDialog(root.getParentFrame(), "UnBoundedness PetriNet", "Algorithm output", JOptionPane.INFORMATION_MESSAGE);
		else
			JOptionPane.showMessageDialog(root.getParentFrame(), "Boundedness PetriNet", "Algorithm output", JOptionPane.INFORMATION_MESSAGE);
	}
	
	private void checkBranchBoundedness(Marking marking, Transition transition) {
		
		if (isUnboundedness)
			return;
		
		Marking newMarking = new Marking(marking);
		newMarking.fire(transition);
		
		for (Marking oldMarking : checkedMarkings) {
			if (isOmega(newMarking, oldMarking)) {
				isUnboundedness = true;
				return;
			}
		}
		
		if (!checkedMarkings.contains(newMarking)) {
			checkedMarkings.add(newMarking);
			Set<Transition> executableTransitions = newMarking.getAllEnabledTransitions();
			for (Transition t : executableTransitions) {
				checkBranchBoundedness(newMarking, t);
			}
		}
		
	}
	
	private boolean isOmega(Marking newMarking, Marking oldMarking) {
		
		Set<Transition> newMarkingTransitins = newMarking.getAllEnabledTransitions();
		Set<Transition> oldMarkingTransitins = oldMarking.getAllEnabledTransitions();
		
		if (Arrays.equals(newMarkingTransitins.toArray(), oldMarkingTransitins.toArray()) 
				&& newMarkingTransitins.size() != 0 
				&& oldMarkingTransitins.size() != 0) {
			int newMarkCount = 0;
			Collection<Integer> values = newMarking.map.values();
			for (Integer val : values) {
				newMarkCount += val;
			}
			int oldMarkCount = 0;
			values = oldMarking.map.values();
			for (Integer val : values) {
				oldMarkCount += val;
			}
			if (newMarkCount > oldMarkCount)
				return true;
		}
		
		return false;
		
	}
	
}
