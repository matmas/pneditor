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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class CollectionTools {

    public static boolean containsAtLeastOne(Collection c1, Collection c2) {
        for (Object c2Object : c2) {
            if (c1.contains(c2Object)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsOnlyInstancesOf(Collection collection, Class clazz) {
        for (Object object : collection) {
            if (object.getClass() != clazz) {
                return false;
            }
        }
        return true;
    }

    public static <E> E getFirstElement(Collection<E> elements) {
        E firstElement = null;
        for (E element : elements) {
            firstElement = element;
            break;
        }
        return firstElement;
    }

    public static <E> E getFirstElementNotIn(Collection<E> elements, Collection<E> restricted) {
        E firstElement = null;
        for (E element : elements) {
            if (!restricted.contains(element)) {
                firstElement = element;
                break;
            }
        }
        return firstElement;
    }

    public static <E> Set<E> getElementsNotIn(Collection<E> elements, Collection<E> restricted) {
        Set<E> filtered = new HashSet<E>();
        for (E element : elements) {
            if (!restricted.contains(element)) {
                filtered.add(element);
            }
        }
        return filtered;
    }

    public static <E> E getLastElement(Collection<E> elements) {
        E lastElement = null;
        for (E element : elements) {
            lastElement = element;
        }
        return lastElement;
    }

    private final static Random random = new Random();

    public static <E> E getRandomElement(List<E> elements) {
        int randomIndex = random.nextInt(elements.size());
        return elements.get(randomIndex);
    }

    @SuppressWarnings(value = "unchecked")
    public static <E> Set<E> getFilteredByClass(Collection<?> elements, Class<E> clazz) {
        Set<E> result = new HashSet<E>();
        for (Object element : elements) {
            if (element != null) {
                if (element.getClass() == clazz) {
                    result.add((E) element);
                }
            }
        }
        return result;
    }
}
