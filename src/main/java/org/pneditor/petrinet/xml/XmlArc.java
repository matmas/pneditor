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
public class XmlArc {

    @XmlElement(name = "sourceId")
    public String sourceId;

    @XmlElement(name = "destinationId")
    public String destinationId;

    @XmlElement(name = "multiplicity")
    public int multiplicity;

    @XmlElement(name = "type")
    public String type;

    @XmlElement(name = "breakPoint")
    public List<XmlPoint> breakPoints = new LinkedList<XmlPoint>();

    @XmlElement(name = "realSourceId")
    public String realSourceId;

    @XmlElement(name = "realDestinationId")
    public String realDestinationId;
}
