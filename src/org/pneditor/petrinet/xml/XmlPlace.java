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

import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class XmlPlace extends XmlNode {

    @XmlElement(name = "label")
    public String label;

    @XmlElement(name = "tokens")
    public int tokens;

    @XmlElement(name = "isStatic")
    public boolean isStatic;

    @XmlElement(name = "tokenLimit")
    public int tokenLimit;
}
