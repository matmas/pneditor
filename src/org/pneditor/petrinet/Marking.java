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
package org.pneditor.petrinet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.pneditor.util.CollectionTools;

/**
 * Marking stores and manages information about tokens.
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class Marking {

    protected Map<Place, Integer> map = new ConcurrentHashMap<Place, Integer>();
    private PetriNet petriNet;
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true); //fair

    /**
     * Copy constructor.
     *
     * @param marking the marking to be copied.
     */
    public Marking(Marking marking) {
        marking.getLock().readLock().lock();
        try {
            this.map = new ConcurrentHashMap<Place, Integer>(marking.map);
        } finally {
            marking.getLock().readLock().unlock();
        }
        this.petriNet = marking.petriNet;
    }

    /**
     * Creates EMPTY marking of the specified Petri net.
     *
     * @param petriNet Petri net to create marking from.
     */
    public Marking(PetriNet petriNet) {
        this.petriNet = petriNet;
    }

    public ReadWriteLock getLock() {
        return lock;
    }

    private Set<Transition> getTransitions() {
        return petriNet.getRootSubnet().getTransitionsRecursively();
    }

    public PetriNet getPetriNet() {
        return petriNet;
    }

    /**
     * Returns the number of tokens based on the specified PlaceNode (Place or
     * ReferencePlace). If specified PlaceNode is ReferencePlace, it will return
     * number of tokens of its connected Place. If the specified ReferencePlace
     * is not connected to any Place, it will return zero. If the resulting
     * Place is static, number of tokens will be given from initial marking
     * instead.
     */
    public int getTokens(PlaceNode placeNode) {
        Place place = placeNode.getPlace();
        if (place == null) { // In case of disconnected ReferencePlace, we want it to appear with zero tokens. Disconnected ReferencePlaces can be found in stored subnets.
            return 0;
        }

        Marking marking;
        if (place.isStatic()) {
            marking = petriNet.getInitialMarking();
        } else {
            marking = this;
        }

        if (marking.map.get(place) == null) { // Place has zero tokens in the beginning. Not every place is in map. Only those previously edited.
            return 0;
        }

        return marking.map.get(place);
    }

    /**
     * Sets the number of tokens to the specified PlaceNode (Place or
     * ReferencePlace). If specified PlaceNode is ReferencePlace, it will set
     * number of tokens to its connected Place. If the specified ReferencePlace
     * is not connected to any Place, it will throw RuntimeException. If the
     * specified number of tokens is negative, it will throw RuntimeException.
     * If the resulting Place is static, number of tokens will be set to initial
     * marking instead.
     */
    public void setTokens(PlaceNode placeNode, int tokens) {
        if (tokens < 0) {
            //throw new RuntimeException("Number of tokens must be non-negative");
            throw new IllegalStateException("Number of tokens must be non-negative");
        }

        Place place = placeNode.getPlace();

        if (place == null) {
            //throw new RuntimeException("setTokens() to disconnected ReferencePlace");
            throw new IllegalStateException("setTokens() to disconnected ReferencePlace");
        }

        if (place.isStatic()) {
            petriNet.getInitialMarking().map.put(place, tokens);
        } else {
        	if (tokenLimitCompliant(placeNode, tokens)) {
        		this.map.put(place, tokens);
        	}
        }
    }

    /**
     * Determines if a transition is enabled in this marking
     *
     * @param transition - transition to be checked
     * @return true if transition is enabled in the marking, otherwise false
     */
    public boolean isEnabled(Transition transition) {
        boolean isEnabled = true;
        ArrayList<PlaceNode> originPlaces = new ArrayList<PlaceNode>();
        ArrayList<Integer> originPlacesArcMultiplicity = new ArrayList<Integer>();
        int tokens  = 0;
        int index = -6;
        PlaceNode place = null;
        int arcMul = 0;
        lock.readLock().lock();
        try {
            for (Arc arc : transition.getConnectedArcs()) {// first it iterates over incoming arcs
                if (arc.isPlaceToTransition()) {
                    if (arc.getType().equals(Arc.RESET)) {//reset arc is always fireable
                        continue;      //but can be blocked by other arcs 
                    } else {
                        if (!arc.getType().equals(Arc.INHIBITOR)) {
                            if (getTokens(arc.getPlaceNode()) < arc.getMultiplicity()) {  //normal arc
                                isEnabled = false;
                                break;
                            } else {
                            	originPlaces.add(arc.getPlaceNode());
                            	originPlacesArcMultiplicity.add(arc.getMultiplicity());
                            	//this construct a list of the places that will lose tokens and how many
                            }
                        } else {
                            if (getTokens(arc.getPlaceNode()) >= arc.getMultiplicity()) {//inhibitory arc
                                isEnabled = false;
                                break;
                            }
                        }
                    }
                } 
            }
            for(Arc arc : transition.getConnectedArcs()) { // then it iterates over outcoming arcs
                if(!arc.isPlaceToTransition()) { // arc is from transition to place
                	place = arc.getPlaceNode(); 
                	//we can now use the list to see if the node will consume token with the firing
                	tokens = getTokens(place);
                	index = originPlaces.indexOf(place);
                	if(index>=0) {
                		arcMul = originPlacesArcMultiplicity.get(index);
                		tokens = tokens - arcMul; 
                		//thus, is this transition will be fired, it will consume arcMul tokens so we update that number
                		// it is always a non-negative integer ; otherwise, this would have detected the transition as
                		// not enabled in the previous for loop
                	}
                	if (!tokenLimitCompliant(arc.getPlaceNode(), tokens + arc.getMultiplicity())) {
                		isEnabled = false;
                		break;
                	}
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        return isEnabled;
    }
    
    /**
     * Check that a given token number is valid on a given place
     *
     * @param placeNode placeNode to do the check on
     * @param requestedTokens number of tokens wanted to be checked
     * @return false if the given numbers of token is invalid, otherwise true
     */
    public boolean tokenLimitCompliant(PlaceNode placeNode, int requestedTokens) {
    	boolean compliant = true;
    	if (placeNode.getTokenLimit()> 0) {
    		if (requestedTokens > placeNode.getTokenLimit()) {
    			compliant = false;
    		}
    	}
    	return compliant;
    }

    /**
     * Fires a transition in this marking. Changes this marking.
     *
     * @param transition transition to be fired in the marking
     * @return false if the specified transition was not enabled, otherwise true
     */
    public boolean fire(Transition transition) {
        boolean success;
        lock.writeLock().lock();
        try {
            if (isEnabled(transition)) {
                for (Arc arc : transition.getConnectedArcs()) {
                    if (arc.isPlaceToTransition()) {
                        int tokens = getTokens(arc.getPlaceNode());
                        if (!arc.getType().equals(Arc.INHIBITOR)) {                 //inhibitor arc doesnt consume tokens
                            if (arc.getType().equals(Arc.RESET)) {                      //reset arc consumes them all
                                setTokens(arc.getPlaceNode(), 0);
                            } else {
                                setTokens(arc.getPlaceNode(), tokens - arc.getMultiplicity());
                            }
                        }
                    }
                }
                for (Arc arc : transition.getConnectedArcs()) {
                    if (!arc.isPlaceToTransition()) {
                        int tokens = getTokens(arc.getPlaceNode());
                        setTokens(arc.getPlaceNode(), tokens + arc.getMultiplicity());
                    }
                }
                success = true;
            } else {
                success = false;
            }
        } finally {
            lock.writeLock().unlock();
        }
        return success;
    }
    
    public boolean canBeUnfired(Transition transition) {
        boolean canBeUnfired = true;
        lock.readLock().lock();
        try {
            for (Arc arc : transition.getConnectedArcs()) {
                if (!arc.isPlaceToTransition()) {
                    if (getTokens(arc.getPlaceNode()) < arc.getMultiplicity()) {
                        canBeUnfired = false;
                        break;
                    }
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        return canBeUnfired;
    }

    public void undoFire(Transition transition) {
        lock.writeLock().lock();
        try {
            if (canBeUnfired(transition)) {
                for (Arc arc : transition.getConnectedArcs()) {
                    if (!arc.isPlaceToTransition()) {
                        int tokens = getTokens(arc.getPlaceNode());
                        setTokens(arc.getPlaceNode(), tokens - arc.getMultiplicity());
                    } else {
                        int tokens = getTokens(arc.getPlaceNode());
                        setTokens(arc.getPlaceNode(), tokens + arc.getMultiplicity());
                    }
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Returns a new marking after firing a transition. Original marking is not
     * changed.
     *
     * @param transition transition to be fired
     * @return new marking with fired transition
     */
    public Marking getMarkingAfterFiring(Transition transition) {
        if (!this.isEnabled(transition)) {
            return null;
        }
        Marking newMarking = new Marking(this);
        newMarking.fire(transition);
        return newMarking;
    }

    public Set<Transition> getEnabledTransitions(Set<Transition> transitions) {
        Set<Transition> enabledTransitions = new HashSet<Transition>();
        for (Transition transition : transitions) {
            if (isEnabled(transition)) {
                enabledTransitions.add(transition);
            }
        }
        return enabledTransitions;
    }

    /**
     * Returns a set of all enabled transitions
     *
     * @return set of all enabled transitions
     */
    public Set<Transition> getAllEnabledTransitions() {
        Set<Transition> enabledTransitions = new HashSet<Transition>();
        lock.readLock().lock();
        try {
            for (Transition transition : getTransitions()) {
                if (isEnabled(transition)) {
                    enabledTransitions.add(transition);
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        return enabledTransitions;
    }

    private List<Transition> getAllEnabledTransitionsByList() {
        List<Transition> fireableTransitions = new ArrayList<Transition>();
        lock.readLock().lock();
        try {
            for (Transition transition : getTransitions()) {
                if (isEnabled(transition)) {
                    fireableTransitions.add(transition);
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        return fireableTransitions;
    }

    /**
     * Fires random chosen transition
     *
     * @throws RuntimeException if no transition is enabled.
     * @return transition, which was fired
     */
    public Transition fireRandomTransition() {
        List<Transition> fireableTransitions = getAllEnabledTransitionsByList();
        if (fireableTransitions.size() == 0) {
            throw new RuntimeException("fireRandomTransition() -> no transition is enabled");
        }
        Transition randomTransition = CollectionTools.getRandomElement(fireableTransitions);
        fire(randomTransition);
        return randomTransition;
    }

    /**
     * Determines if this marking can be fired by any transition.
     *
     * @return true if there is a transition which can be fired in the marking.
     */
    public boolean isEnabledByAnyTransition() {
        return !getAllEnabledTransitions().isEmpty();
    }

    /**
     * Returns true if specified firingSequence leads to valid marking i.e.
     * getMarkingAfterFiring(firingSequence) != null
     */
    public boolean isCorrectContinuation(FiringSequence firingSequence) {
        return getMarkingAfterFiring(firingSequence) != null;
    }

    /**
     * Returns true if specified firingSequence leads to invalid marking i.e.
     * !isCorrectContinuation(firingSequence)
     */
    public boolean isWrongContinuation(FiringSequence firingSequence) {
        return !isCorrectContinuation(firingSequence);
    }

    /**
     * Returns a marking after firing a sequence of transitions. The original
     * marking is not changed.
     *
     * @param firingSequence sequence of transitions to be fired one after the
     * other
     * @return a new marking after firing a sequence of transitions
     */
    public Marking getMarkingAfterFiring(FiringSequence firingSequence) {
        Marking newMarking = new Marking(this);
        for (Transition transition : firingSequence) {
            if (!newMarking.isEnabled(transition)) {
                return null;
            }
            newMarking.fire(transition);
        }
        return newMarking;
    }

    /**
     * Returns a set of all transition firing sequences, which can be fired in
     * this marking.
     *
     * @throws PetriNetException if there the same marking is visited more than
     * once.
     */
    public Set<FiringSequence> getAllFiringSequencesRecursively() throws PetriNetException {
        Set<Marking> visitedMarkings = new HashSet<Marking>();
        visitedMarkings.add(this);
        return getAllFiringSequencesRecursively(this, visitedMarkings);
    }

    private Set<FiringSequence> getAllFiringSequencesRecursively(Marking marking, Set<Marking> visitedMarkings) throws PetriNetException {
        Set<FiringSequence> firingSequences = new HashSet<FiringSequence>();

        Set<Transition> enabledTransitions = marking.getAllEnabledTransitions();
        for (Transition transition : enabledTransitions) {
            Marking newMarking = marking.getMarkingAfterFiring(transition);

            if (visitedMarkings.contains(newMarking)) {
                throw new PetriNetException("There is a loop.");
            }
            visitedMarkings.add(newMarking);

            if (!newMarking.isEnabledByAnyTransition()) { // leaf marking
                FiringSequence firingSequence = new FiringSequence();
                firingSequence.add(transition);
                firingSequences.add(firingSequence);
            }

            for (FiringSequence nextFiringSequence : getAllFiringSequencesRecursively(newMarking, visitedMarkings)) {
                FiringSequence firingSequence = new FiringSequence();
                firingSequence.add(transition);
                firingSequence.addAll(nextFiringSequence);
                firingSequences.add(firingSequence);
            }

            visitedMarkings.remove(newMarking);
        }
        return firingSequences;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Fireable transitions: ");

        lock.readLock().lock();
        try {
            for (Transition transition : this.getAllEnabledTransitions()) {
                result.append(transition.getFullLabel() + " ");
            }
            if (this.getAllEnabledTransitions().isEmpty()) {
                result.append("-NONE-");
            }
            result.append("\nPlaces: ");
            for (Place place : petriNet.getRootSubnet().getPlacesRecursively()) {
                result.append(place.getLabel() + ":" + getTokens(place) + " ");
            }
            if (petriNet.getRootSubnet().getPlacesRecursively().isEmpty()) {
                result.append("-NONE-");
            }
        } finally {
            lock.readLock().unlock();
        }

        return result.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Marking other = (Marking) obj;
        if (this.petriNet != other.petriNet && (this.petriNet == null || !this.petriNet.equals(other.petriNet))) {
            return false;
        }
        if (this.map == other.map) {
            return true;
        }
        Set<Place> places = new HashSet<Place>(); // because map is sparse
        places.addAll(this.map.keySet());
        places.addAll(other.map.keySet());
        for (Place place : places) {
            if (this.getTokens(place) != other.getTokens(place)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + (this.petriNet != null ? this.petriNet.hashCode() : 0);
        for (Place place : this.map.keySet()) { // because map is sparse
            hash = 73 * hash + this.getTokens(place);
        }
        return hash;
    }

}
