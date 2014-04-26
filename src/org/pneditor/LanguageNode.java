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
import java.util.HashSet;
import java.util.Set;
import org.pneditor.petrinet.Transition;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class LanguageNode {

    private Transition transition;
    private Set<LanguageNode> nextNodes = new HashSet<LanguageNode>();
    private Set<Transition> disallowedNextTransitions = new HashSet<Transition>();

    public LanguageNode(Transition transition) {
        this.transition = transition;
    }

    public Transition getTransition() {
        return transition;
    }

    public Set<LanguageNode> getNextNodes() {
        return nextNodes;
    }

    public Set<Transition> getNextTransitions() {
        Set<Transition> nextTransitions = new HashSet<Transition>();
        for (LanguageNode node : nextNodes) {
            nextTransitions.add(node.getTransition());
        }
        return nextTransitions;
    }

    public LanguageNode getNextNode(Transition transition) {
        for (LanguageNode node : nextNodes) {
            if (node.getTransition() == transition) {
                return node;
            }
        }
        return null;
    }

    public LanguageNode addNextNode(Transition transition) {
        LanguageNode newNode = new LanguageNode(transition);
        nextNodes.add(newNode);
        return newNode;
    }

    public Set<Transition> getDisallowedNextTransitions() {
        return disallowedNextTransitions;
    }

    public Set<FiringSequence> getCorrectContinuationsRecursively() {
        Set<FiringSequence> correctContinuations = new HashSet<FiringSequence>();

        if (transition != null) { // transition == null if it is rootNode
            FiringSequence firingSequence = new FiringSequence();
            firingSequence.add(transition);
            correctContinuations.add(firingSequence);
        }

        for (LanguageNode nextNode : getNextNodes()) {
            for (FiringSequence nextFiringSequence : nextNode.getCorrectContinuationsRecursively()) {
                FiringSequence firingSequence = new FiringSequence();
                if (transition != null) { //not root node
                    firingSequence.add(transition);
                }
                firingSequence.addAll(nextFiringSequence);
                correctContinuations.add(firingSequence);
            }
        }
        return correctContinuations;
    }

    public Set<FiringSequence> getWrongContinuationsRecursively() {
        Set<FiringSequence> wrongContinuations = new HashSet<FiringSequence>();

        for (Transition disallowedNextTransition : getDisallowedNextTransitions()) {
            FiringSequence firingSequence = new FiringSequence();
            firingSequence.add(disallowedNextTransition);
            wrongContinuations.add(firingSequence);
        }

        for (LanguageNode nextNode : getNextNodes()) {
            for (FiringSequence nextFiringSequence : nextNode.getWrongContinuationsRecursively()) {
                FiringSequence firingSequence = new FiringSequence();
                firingSequence.add(nextNode.transition);
                firingSequence.addAll(nextFiringSequence);
                wrongContinuations.add(firingSequence);
            }
        }

        return wrongContinuations;
    }

}
