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
package misc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import org.pneditor.petrinet.Document;
import org.pneditor.petrinet.Marking;
import org.pneditor.petrinet.PetriNet;
import org.pneditor.petrinet.Place;
import org.pneditor.petrinet.xml.DocumentImporter;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class MarkingConcurrentSimulation {

    public static void main(String[] args) throws InterruptedException {
        System.out.print("Testing...");
        try {
            Document document = new DocumentImporter().readFromFile(new File("src/misc/MarkingConcurrentSimulation.pflow"));
            PetriNet petriNet = document.petriNet;
            for (int i = 0; i < 1000; i++) {
                doTest(petriNet);
            }
        } catch (JAXBException ex) {
            Logger.getLogger(MarkingConcurrentSimulation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MarkingConcurrentSimulation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MarkingConcurrentSimulation.class.getName()).log(Level.SEVERE, null, ex);
        }
        while (Thread.activeCount() > 1) {
            Thread.sleep(100);
        }
        System.out.println("FINISHED");
    }

    private static void doTest(final PetriNet petriNet) throws InterruptedException {
        final Marking marking = petriNet.getInitialMarking();
        new Thread(new Runnable() {
            public void run() {
//				System.out.println("running co-thread");
                for (int i = 0; i < 100; i++) {
                    try {
                        if (!marking.isEnabledByAnyTransition()) {
                            System.out.println("all transitions disabled");
                            System.out.println(marking);
                            return;
                        }
                        marking.fireRandomTransition();
                        for (Place place : petriNet.getRootSubnet().getPlacesRecursively()) {
                            if (marking.getTokens(place) < 0) {
                                System.out.println("negative place");
                                System.out.println(marking);
                                return;
                            }
                        }
                        Thread.sleep(50);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MarkingConcurrentSimulation.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
//				System.out.println("finished co-thread");
            }
        }).start();
    }
}
