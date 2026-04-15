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
package org.pneditor.petrinet.xml;

import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class XmlSubnet extends XmlNode {

    @XmlElement(name = "label")
    public String label;

    @XmlElement(name = "place")
    public List<XmlPlace> places = new LinkedList<XmlPlace>();

    @XmlElement(name = "transition")
    public List<XmlTransition> transitions = new LinkedList<XmlTransition>();

    @XmlElement(name = "arc")
    public List<XmlArc> arcs = new LinkedList<XmlArc>();

    @XmlElement(name = "subnet")
    public List<XmlSubnet> subnets = new LinkedList<XmlSubnet>();

    @XmlElement(name = "referencePlace")
    public List<XmlReferencePlace> referencePlaces = new LinkedList<XmlReferencePlace>();

    @XmlElement(name = "referenceArc")
    public List<XmlReferenceArc> referenceArcs = new LinkedList<XmlReferenceArc>();

}
