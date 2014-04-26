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
package org.pneditor.editor.canvas;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.pneditor.petrinet.Node;
import org.pneditor.petrinet.PlaceNode;
import org.pneditor.petrinet.TransitionNode;
import org.pneditor.petrinet.Element;
import org.pneditor.petrinet.Subnet;
import org.pneditor.petrinet.Transition;
import org.pneditor.util.CollectionTools;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class Selection implements Iterable<Element> {

    Set<Element> selectedElements = new HashSet<Element>();
    SelectionChangedListener selectionChangedListener = null;

    public void setSelectionChangedListener(SelectionChangedListener selectionChangedListener) {
        this.selectionChangedListener = selectionChangedListener;
    }

    public void clear() {
        selectedElements.clear();
        selectionChanged();
    }

    public void add(Element element) {
        selectedElements.add(element);
        selectionChanged();
    }

    public void addAll(Collection<? extends Element> elements) {
        selectedElements.addAll(elements);
        selectionChanged();
    }

    public boolean isEmpty() {
        return selectedElements.isEmpty();
    }

    public boolean contains(Element element) {
        return selectedElements.contains(element);
    }

    public void selectionChanged() {
        if (selectionChangedListener != null) {
            selectionChangedListener.selectionChanged();
        }
    }

    public Iterator<Element> iterator() {
        return selectedElements.iterator();
    }

    public Set<Element> getElements() {
        return selectedElements;
    }

    public Set<Node> getNodes() {
        return CollectionTools.getFilteredByClass(selectedElements, Node.class);
    }

    public Set<Transition> getTransitions() {
        return CollectionTools.getFilteredByClass(selectedElements, Transition.class);
    }

    public Set<Subnet> getSubnets() {
        return CollectionTools.getFilteredByClass(selectedElements, Subnet.class);
    }

    public Set<TransitionNode> getTransitionNodes() {
        return CollectionTools.getFilteredByClass(selectedElements, TransitionNode.class);
    }

    public Set<Transition> getTransitionsRecursively() {
        Set<Transition> selectedTransitions = new HashSet<Transition>();
        for (Element element : selectedElements) {
            if (element instanceof Subnet) {
                Subnet subnet = (Subnet) element;
                selectedTransitions.addAll(subnet.getTransitionsRecursively());
            } else if (element instanceof Transition) {
                selectedTransitions.add((Transition) element);
            }
        }
        return selectedTransitions;
    }

    public Set<PlaceNode> getPlaceNodes() {
        return CollectionTools.getFilteredByClass(selectedElements, PlaceNode.class);
    }

}
