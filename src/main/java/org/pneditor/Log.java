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

import org.pneditor.petrinet.Transition;
import org.pneditor.petrinet.FiringSequence;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Set;
import org.pneditor.petrinet.PetriNet;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class Log extends LinkedList<FiringSequence> {

    private Set<Transition> transitions;

    public Log() {
    }

    public Log(File file, PetriNet petriNet) throws FileNotFoundException {
        transitions = readFromFile(file, petriNet);
    }

    public Set<Transition> getTransitions() {
        return transitions;
    }

    public void writeToFile(File file) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        OutputStreamWriter out = new OutputStreamWriter(fileOutputStream, "UTF-8");

        for (int i = 0; i < this.size(); i++) {
            FiringSequence firingSequence = this.get(i);
            for (Transition transition : firingSequence) {
                out.write(i + " " + transition.getFullLabel() + '\n');
            }
        }
        out.close();
    }

    private Set<Transition> readFromFile(File file, PetriNet petriNet) throws FileNotFoundException {
        Set<Transition> transitionSet = new HashSet<Transition>();
        LabelToTransition labelToTransition = new LabelToTransition(petriNet);
        int previousCaseId = -1;
        FiringSequence firingSequence = null;
        Scanner fileScanner = new Scanner(file);
        while (fileScanner.hasNextLine()) {
            Scanner lineScanner = new Scanner(fileScanner.nextLine());
            lineScanner.useDelimiter(" ");
            if (lineScanner.hasNext()) {
                int caseId = lineScanner.nextInt();
                String transitionFullName = "";
                while (lineScanner.hasNext()) {
                    transitionFullName = transitionFullName + lineScanner.next() + " ";
                }
                transitionFullName = transitionFullName.trim();
                Transition transition = labelToTransition.getTransition(transitionFullName);
                transitionSet.add(transition);
                if (caseId != previousCaseId || firingSequence == null) {
                    if (firingSequence != null) {
                        this.add(firingSequence);
                    }
                    firingSequence = new FiringSequence();
                    previousCaseId = caseId;
                }
                firingSequence.add(transition);
            }
            lineScanner.close();
        }
        this.add(firingSequence);
        fileScanner.close();
        return transitionSet;
    }
}
