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
package org.pneditor;

import org.pneditor.petrinet.FiringSequence;
import java.util.Set;
import org.pneditor.petrinet.Marking;
import org.pneditor.petrinet.PetriNet;
import org.pneditor.petrinet.PetriNetException;
import org.pneditor.petrinet.Transition;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class Language {

    private LanguageNode rootNode = new LanguageNode(null);
    private Set<Transition> transitions;

    public Language(Log log) {
        for (FiringSequence firingSequence : log) {
            insertCorrectContinuation(firingSequence);
        }
        this.transitions = log.getTransitions();
        setupDisallowedTransitionsRecursively(rootNode, transitions);
    }

    public Language(PetriNet petriNet) throws PetriNetException {
        Marking initialMarking = petriNet.getInitialMarking();
        for (FiringSequence firingSequence : initialMarking.getAllFiringSequencesRecursively()) {
            insertCorrectContinuation(firingSequence);
        }
        this.transitions = petriNet.getRootSubnet().getTransitionsRecursively();
        setupDisallowedTransitionsRecursively(rootNode, transitions);
    }

    private void insertCorrectContinuation(FiringSequence correctContinuation) {
        LanguageNode currentNode = rootNode;
        for (Transition transition : correctContinuation) {
            LanguageNode nextNode = currentNode.getNextNode(transition);
            if (nextNode == null) {
                nextNode = currentNode.addNextNode(transition);
            }
            currentNode = nextNode;
        }
    }

    private void setupDisallowedTransitionsRecursively(LanguageNode node, Set<Transition> allTransitions) {
        node.getDisallowedNextTransitions().addAll(allTransitions);
        node.getDisallowedNextTransitions().removeAll(node.getNextTransitions());
        for (LanguageNode nextNode : node.getNextNodes()) {
            setupDisallowedTransitionsRecursively(nextNode, allTransitions);
        }
    }

    public LanguageNode getRootNode() {
        return rootNode;
    }

    public Set<LanguageNode> getFirstNodes() {
        return rootNode.getNextNodes();
    }

    public Set<Transition> getTransitions() {
        return transitions;
    }

    public Set<FiringSequence> getCorrectContinuations() {
        return rootNode.getCorrectContinuationsRecursively();
    }

    public Set<FiringSequence> getWrongContinuations() {
        return rootNode.getWrongContinuationsRecursively();
    }
}
