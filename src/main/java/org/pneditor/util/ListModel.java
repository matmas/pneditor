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
package org.pneditor.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.AbstractListModel;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public abstract class ListModel<E> extends AbstractListModel implements Iterable<E> {

    protected List<E> elements = new ArrayList<E>();

    abstract public void addNew();

    public int getSize() {
        return elements.size();
    }

    public E getElementAt(int i) {
        return elements.get(i);
    }

    public void delete(int[] selectedIndices) {
        List<E> elementsToDelete = new LinkedList<E>();
        for (int i : selectedIndices) {
            elementsToDelete.add(elements.get(i));
        }
        for (E e : elementsToDelete) {
            int i = elements.indexOf(e);
            elements.remove(i);
            fireIntervalRemoved(this, i, i);
        }
    }

    public Iterator<E> iterator() {
        return elements.iterator();
    }

    public void clear() {
        int lastIndex = elements.size() - 1;
        elements.clear();
        if (lastIndex >= 0) {
            fireIntervalRemoved(this, 0, lastIndex);
        }
    }

    public void add(E element) {
        elements.add(element);
    }
}
