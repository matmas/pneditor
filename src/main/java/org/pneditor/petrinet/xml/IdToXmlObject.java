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

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class IdToXmlObject {

    private XmlDocument xmlDocument;

    public IdToXmlObject(XmlDocument xmlDocument) {
        this.xmlDocument = xmlDocument;
    }

    private Map<String, Object> map = new HashMap<String, Object>();

    public Object getXmlObject(String id) {
        if (id.equals(null)) {
            return null;
        }
        if (id.equals("")) {
            return null;
        }
        if (map.containsKey(id)) {
            return map.get(id);
        }
        Object xmlObject = getXmlObjectFromXmlSubnet(id, xmlDocument.rootSubnet);
        if (xmlObject != null) {
            map.put(id, xmlObject);
        }
        return xmlObject;
    }

    private Object getXmlObjectFromXmlSubnet(String id, XmlSubnet xmlSubnet) {
        for (XmlPlace xmlPlace : xmlSubnet.places) {
            if (xmlPlace.id.equals(id)) {
                return xmlPlace;
            }
        }
        for (XmlTransition xmlTransition : xmlSubnet.transitions) {
            if (xmlTransition.id.equals(id)) {
                return xmlTransition;
            }
        }
        for (XmlReferencePlace xmlReference : xmlSubnet.referencePlaces) {
            if (xmlReference.id.equals(id)) {
                return xmlReference;
            }
        }
        for (XmlSubnet xmlSubSubnet : xmlSubnet.subnets) {
            if (xmlSubSubnet.id.equals(id)) {
                return xmlSubSubnet;
            }
            Object xmlObject = getXmlObjectFromXmlSubnet(id, xmlSubSubnet);
            if (xmlObject != null) {
                return xmlObject;
            }
        }
        return null;
    }

}
