package org.pneditor;

import org.pneditor.petrinet.FiringSequence;
import java.util.Set;
import org.pneditor.petrinet.Marking;
import org.pneditor.petrinet.PetriNet;
import org.pneditor.petrinet.PetriNetException;
import org.pneditor.petrinet.Transition;

/**
 *
 * @author matmas
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
