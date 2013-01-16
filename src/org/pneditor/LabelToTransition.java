package org.pneditor;

import org.pneditor.petrinet.Transition;
import java.util.HashMap;
import java.util.Map;
import org.pneditor.petrinet.PetriNet;

/**
 *
 * @author matmas
 */
public class LabelToTransition {
	
	private Map<String, Transition> map = new HashMap<String, Transition>();
	private PetriNet petriNet;

	public LabelToTransition(PetriNet petriNet) {
		this.petriNet = petriNet;
	}
	
	public Transition getTransition(String label) {
		if (label.equals(null)) {
			return null;
		}
		if (map.containsKey(label)) {
			return map.get(label);
		}
		Transition transition = new Transition();
		transition.setLabel(label);
		petriNet.getNodeSimpleIdGenerator().setUniqueId(transition);
		map.put(label, transition);
		return transition;
	}
}
