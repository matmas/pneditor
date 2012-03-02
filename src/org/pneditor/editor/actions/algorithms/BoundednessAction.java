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
import java.util.*;
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
	
	private Stack<Marking> markingsStack;
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
		markingsStack = new Stack<Marking>();
		markingsStack.push(m0);
		
		Set<Transition> executableTransitions = m0.getAllEnabledTransitions();
		for(Transition t : executableTransitions) {
			int branchLength = checkBranchBoundedness(m0, t);
			for (int i=0; i<branchLength; i++) {
				markingsStack.pop();
			}
		}
		
		if (isUnboundedness)
			JOptionPane.showMessageDialog(root.getParentFrame(), "UnBoundedness PetriNet", "Algorithm output", JOptionPane.INFORMATION_MESSAGE);
		else
			JOptionPane.showMessageDialog(root.getParentFrame(), "Boundedness PetriNet", "Algorithm output", JOptionPane.INFORMATION_MESSAGE);
	}
	
	private int checkBranchBoundedness(Marking marking, Transition transition) {
		
		if (isUnboundedness)
			return 0;
		
		Marking newMarking = new Marking(marking);
		newMarking.fire(transition);
		
		for (Marking oldMarking : markingsStack) {
			if (isOmega(newMarking, oldMarking)) {
				isUnboundedness = true;
				return 0;
			}
		}
		
		if (!markingsStack.contains(newMarking)) {
			markingsStack.push(newMarking);
			Set<Transition> executableTransitions = newMarking.getAllEnabledTransitions();
			for (Transition t : executableTransitions) {
				int branchLength = checkBranchBoundedness(newMarking, t);
				for (int i = 0; i < branchLength; i++) {
					markingsStack.pop();
				}
			}
			return 1;
		}
		
		return 0;
		
	}
	
	private boolean isOmega(Marking newMarking, Marking oldMarking) {
		
		Set<Place> newMarkingPlaces = newMarking.getPetriNet().getRootSubnet().getPlaces();
		Set<Place> oldMarkingPlaces = oldMarking.getPetriNet().getRootSubnet().getPlaces();
		
		boolean isOneSharplyHigher = false;
		
		for (Place newMarkingPlace : newMarkingPlaces) {
			
			int newTokens = newMarking.getTokens(newMarkingPlace);
			
			Place oldMarkingPlace = null;
			for (Place place : oldMarkingPlaces) {
				if (place.equals(newMarkingPlace)) {
					oldMarkingPlace = place;
					break;
				}
			}
			
			int oldTokens = oldMarking.getTokens(oldMarkingPlace);
			
			if (! (newTokens >= oldTokens) )
				return false;
			else if (newTokens > oldTokens)
				isOneSharplyHigher = true;
			
		}
		
		if (isOneSharplyHigher)
			return true;
		
		
		return false;
		
		
	}
	
}
