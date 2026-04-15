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

/**
 * Represents a sequence of transitions.
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class FiringSequence extends ArrayList<Transition> implements Comparable<FiringSequence> {

    public FiringSequence() {
    }

    public FiringSequence(FiringSequence firingSequence) {
        super(firingSequence);
    }

    public int compareTo(FiringSequence firingSequence) {
        return this.toString().compareTo(firingSequence.toString());
    }

    /**
     * One transition can be more then one time in firing sequence. Returns
     * number of occurences of the specified transition.
     *
     * @param transition transition to determine number of occurences
     * @return number of occurences of the specified transition
     */
    public int getNumOfTransition(Transition transition) {
        int num = 0;
        for (Transition t : this) {
            if (t == transition) {
                num++;
            }
        }
        return num;
    }

    /**
     * Returns last transition in this firing sequence.
     *
     * @return last transition in thefiring sequence
     */
    public Transition getLastTransition() {
        return this.get(this.size() - 1);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Transition transition : this) {
            result.append(transition.getFullLabel() + " ");
        }
        return result.toString();
    }
}
