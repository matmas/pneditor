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
    
	public static int initialNumberOftokens;  /* initialNumberOftokens is a static variable that allows us to store a number of tokens upstream instead of downstream*/
    public static int vaft;  /*vaft is a static variable that allows us to store a number of tokens downstream instead of upstream */
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
            this.map.put(place, tokens);
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
        lock.readLock().lock();
        try {
            for (Arc arc : transition.getConnectedArcs()) {
                if (arc.isPlaceToTransition()) {
                    if (arc.getType().equals(Arc.RESET)) {//reset arc is always fireable
                        continue;      //but can be blocked by other arcs 
                    } else {
                        if (!arc.getType().equals(Arc.INHIBITOR)) {
                            if (getTokens(arc.getPlaceNode()) < arc.getMultiplicity()) {  //normal arc
                                isEnabled = false;
                                break;
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
        } finally {
            lock.readLock().unlock();
        }
        return isEnabled;
    }
//Overload method  isenabled .
    
    public boolean isEnabled(Transition transition,int i) {
        boolean isEnabled = true;
        lock.readLock().lock();
        try {
            for (Arc arc : transition.getConnectedArcs()) {
                if (arc.isPlaceToTransition()) {
                    if (arc.getType().equals(Arc.RESET)) {//reset arc is always fireable
                        continue;      //but can be blocked by other arcs 
                    } else {
                        if (!arc.getType().equals(Arc.INHIBITOR)) {
                            if (getTokens(arc.getPlaceNode()) < i*arc.getMultiplicity()) {  //normal arc 
                            	                                                            //The i value contributes to the prohibition to send 
                            	                                                            //Token(s) located upstream place(s)
                                isEnabled = false;
                                break;
                            }
                        } else {
                            if (getTokens(arc.getPlaceNode()) >= arc.getMultiplicity()) {//inhibitory arc and allow tokens not to be sent 
                                                                          //when the value of arc multiplied by the number of toKens are numerous
                            	                                          // the number of tokens on the place
                            	isEnabled = false;
                                break;
                            }
                        }
                    }
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        return isEnabled;
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
    
    /**
     * Return the number of tokens located at the place(s) that are positioned before the transition to activate
     * @param t : transition 
     * @return
     * 
     */
    public  int returnTokens(Transition t) {
    	int n = 0; // Initializing the number of tokens , this variable n is used(in Line 481) to calculate of tokens of places or a place placed before a transition.
    	if(isEnabled(t)){
    	
    	 for (Arc arc : t.getConnectedArcs()) {
    		 if(arc.isPlaceToTransition()) {
    			 int tokens = getTokens(arc.getPlaceNode());
    			 n= n + tokens;
    		 }
    		
    	 }
    	
		
    	}
		return n;
    	
    }
    
    /**
     * Return the number of tokens located at the place(s) that are positioned before the transition to activate
     * @param t
     * @return
     */
    public int returnTokensAfter(Transition t) {
  
    	int n =0;    // Initializing the number of tokens , this variable n is used(in Line 501 and 506) to calculate of tokens of places or a place placed before a transition.
    	if(isEnabled(t)) {
    		
    	  	 for (Arc arc : t.getConnectedArcs()) {
        		 if(!arc.isPlaceToTransition()) {
        			 int tokens = getTokens(arc.getPlaceNode());
        			 n= n + tokens;
        		 }	  		
    	
    	      }
    	}  	 
		return n;
    	
    }

    
    
    /**
     * Overload of the method fire allowing to send N token(s) from place(s) to the following place(s)
     * @param transition
     * @param i
     * @return
     */
	public boolean fire(Transition transition, int i) {
		// TODO Auto-generated method stub
        boolean success;
        lock.writeLock().lock();
        try {
            if (isEnabled(transition)) {
                for (Arc arc1 : transition.getConnectedArcs()) {
                    if (!arc1.isPlaceToTransition()) {
                    
                    		int tokens = getTokens(arc1.getPlaceNode()); 
                    		initialNumberOftokens = returnTokens(transition);                 		
                    		if(i<initialNumberOftokens)
                                   setTokens(arc1.getPlaceNode(), tokens + i*arc1.getMultiplicity());
                    		else if(i>initialNumberOftokens || i==initialNumberOftokens){
                    			 setTokens(arc1.getPlaceNode(), tokens + initialNumberOftokens*arc1.getMultiplicity());
                    			 vaft= tokens + initialNumberOftokens*arc1.getMultiplicity(); // Stock the value in variable defined in line 38.
                            }
                            

                    }
                }
                
              for (Arc arc : transition.getConnectedArcs()) {
              if (arc.isPlaceToTransition()) {
                 int tokens = getTokens(arc.getPlaceNode());
                 if(i<tokens || i==tokens) {
                  if (!arc.getType().equals(Arc.INHIBITOR)) {                 //inhibitor arc doesnt consume tokens
                      if (arc.getType().equals(Arc.RESET)) {                      //reset arc consumes them all
                          setTokens(arc.getPlaceNode(), 0);
                      } else {
                              setTokens(arc.getPlaceNode(), tokens - i*arc.getMultiplicity());
                      }
                  }
                 }

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

/**
 * Management of the backtrack taking into account the initial state of the network and the final 
 and the final state. Calculations have been made with precision for a good management of the Marking
 * @param transition
 * @param i
 */
	public void undoFire(Transition transition, int i) {
		// TODO Auto-generated method stub
        lock.writeLock().lock();
        try {
            if (canBeUnfired(transition)) {
                for (Arc arc1 : transition.getConnectedArcs()) {
               	 if(arc1.isPlaceToTransition()) {
            		 int tokens = getTokens(arc1.getPlaceNode());         	
              		if(i<initialNumberOftokens || i==initialNumberOftokens ) {
                     setTokens(arc1.getPlaceNode(), tokens + i*arc1.getMultiplicity());}
              		else if(i>initialNumberOftokens) {
              			setTokens(arc1.getPlaceNode(), tokens + initialNumberOftokens*arc1.getMultiplicity());
              		}
            		 
            	 }
                }
                for (Arc arc1 : transition.getConnectedArcs()) {
               	 
               	 if (!arc1.isPlaceToTransition()) {
                        
                		int tokens = getTokens(arc1.getPlaceNode());
                		if(i<initialNumberOftokens || i==initialNumberOftokens) {
                               setTokens(arc1.getPlaceNode(), tokens - i*arc1.getMultiplicity());}
                		else if(i>initialNumberOftokens){
                			 setTokens(arc1.getPlaceNode(), Math.abs(tokens - initialNumberOftokens*arc1.getMultiplicity()));
                        }               	 
                  
               }

            }
        }
                
        }      
           finally {
            lock.writeLock().unlock();
        }
		
	}

}
