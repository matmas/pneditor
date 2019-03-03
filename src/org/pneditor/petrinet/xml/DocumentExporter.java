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

import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.TransformerException;
import org.pneditor.petrinet.Arc;
import org.pneditor.petrinet.Document;
import org.pneditor.petrinet.Element;
import org.pneditor.petrinet.Marking;
import org.pneditor.petrinet.Place;
import org.pneditor.petrinet.ReferenceArc;
import org.pneditor.petrinet.ReferencePlace;
import org.pneditor.petrinet.Subnet;
import org.pneditor.petrinet.Transition;
import org.pneditor.petrinet.Role;
import org.pneditor.util.Xslt;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class DocumentExporter {

    private XmlDocument xmlDocument = new XmlDocument();

    public DocumentExporter(Document document, Marking marking) {

        marking.getLock().readLock().lock();
        try {
//          xmlDocument.rootSubnet = getXmlSubnet(document.petriNet.getRootSubnet(), marking);
            xmlDocument.rootSubnet = getXmlSubnet(marking.getPetriNet().getRootSubnet(), marking);
        } finally {
            marking.getLock().readLock().unlock();
        }

        for (Role role : document.roles) {
            xmlDocument.roles.add(getXmlRole(role));
        }
        //Rectangle bounds = document.petriNet.getRootSubnet().getBoundsRecursively();
        Rectangle bounds = marking.getPetriNet().getRootSubnet().getBoundsRecursively();
        xmlDocument.left = bounds.x;
        xmlDocument.top = bounds.y;
    }

    public void writeToFile(File file) throws FileNotFoundException, JAXBException {
        JAXBContext ctx = JAXBContext.newInstance(XmlDocument.class);
        Marshaller m = ctx.createMarshaller();
        m.setProperty("jaxb.formatted.output", true);
        m.marshal(xmlDocument, new FileOutputStream(file));
    }

    public void writeToFileWithXslt(File file, InputStream xslt) throws FileNotFoundException, JAXBException, IOException, TransformerException {
        if (xslt == null) {
            writeToFile(file);
            return;
        }
        JAXBContext ctx = JAXBContext.newInstance(XmlDocument.class);
        Marshaller m = ctx.createMarshaller();
        m.setProperty("jaxb.formatted.output", true);
        File tempFile = File.createTempFile("pneditor-export", null);
        m.marshal(xmlDocument, new FileOutputStream(tempFile));
        Xslt.transformXml(tempFile, xslt, file);
        tempFile.delete(); // delete temp file
    }

    private XmlRole getXmlRole(Role role) {
        XmlRole xmlRole = new XmlRole();
        xmlRole.id = role.id;
        xmlRole.name = role.name;
        for (Transition transition : role.transitions) {
            xmlRole.transitionIds.add(transition.getId());
        }
        xmlRole.createCase = role.createCase;
        xmlRole.destroyCase = role.destroyCase;
        return xmlRole;
    }

    private XmlSubnet getXmlSubnet(Subnet subnet, Marking initialMarking) {
        XmlSubnet xmlSubnet = new XmlSubnet();
        xmlSubnet.id = subnet.getId();
        xmlSubnet.label = subnet.getLabel();
        xmlSubnet.x = subnet.getCenter().x;
        xmlSubnet.y = subnet.getCenter().y;
        for (Element element : subnet.getElements()) {
            if (element instanceof Subnet) {
                xmlSubnet.subnets.add(getXmlSubnet((Subnet) element, initialMarking));
            } else if (element instanceof Transition) {
                xmlSubnet.transitions.add(getXmlTransition((Transition) element));
            } else if (element instanceof ReferencePlace) {
                xmlSubnet.referencePlaces.add(getXmlReferencePlace((ReferencePlace) element));
            } else if (element instanceof Place) {
                xmlSubnet.places.add(getXmlPlace((Place) element, initialMarking));
            } else if (element instanceof ReferenceArc) {
                xmlSubnet.referenceArcs.add(getXmlReferenceArc((ReferenceArc) element));
            } else if (element instanceof Arc) {
                xmlSubnet.arcs.add(getXmlArc((Arc) element));
            }
        }
        return xmlSubnet;
    }

    private XmlPlace getXmlPlace(Place place, Marking initialMarking) {
        XmlPlace xmlPlace = new XmlPlace();
        xmlPlace.id = place.getId();
        xmlPlace.x = place.getCenter().x;
        xmlPlace.y = place.getCenter().y;
        xmlPlace.isStatic = place.isStatic();
        xmlPlace.tokenLimit = place.getTokenLimit();
        xmlPlace.label = place.getLabel();
        xmlPlace.tokens = initialMarking.getTokens(place);
        return xmlPlace;
    }

    private XmlTransition getXmlTransition(Transition transition) {
        XmlTransition xmlTransition = new XmlTransition();
        xmlTransition.id = transition.getId();
        xmlTransition.x = transition.getCenter().x;
        xmlTransition.y = transition.getCenter().y;
        xmlTransition.label = transition.getLabel();
        return xmlTransition;
    }

    private XmlArc getXmlArc(Arc arc) {
        XmlArc xmlArc = new XmlArc();
        xmlArc.multiplicity = arc.getMultiplicity();
        xmlArc.type = arc.getType();
        xmlArc.sourceId = arc.getSource().getId();
        xmlArc.destinationId = arc.getDestination().getId();

        if (arc.getSource() instanceof ReferencePlace) {
            ReferencePlace referencePlace = (ReferencePlace) arc.getSource();
            xmlArc.realSourceId = referencePlace.getConnectedPlace().getId();
        } else {
            xmlArc.realSourceId = xmlArc.sourceId;
        }
        if (arc.getDestination() instanceof ReferencePlace) {
            ReferencePlace referencePlace = (ReferencePlace) arc.getDestination();
            xmlArc.realDestinationId = referencePlace.getConnectedPlace().getId();
        } else {
            xmlArc.realDestinationId = xmlArc.destinationId;
        }

        List<Point> breakPoints = arc.getBreakPoints();
        for (Point point : breakPoints) {
            XmlPoint xmlPoint = new XmlPoint();
            xmlPoint.x = point.x;
            xmlPoint.y = point.y;
            xmlArc.breakPoints.add(xmlPoint);
        }
        return xmlArc;
    }

    private XmlReferencePlace getXmlReferencePlace(ReferencePlace referencePlace) {
        XmlReferencePlace xmlReferencePlace = new XmlReferencePlace();
        xmlReferencePlace.id = referencePlace.getId();
        xmlReferencePlace.x = referencePlace.getCenter().x;
        xmlReferencePlace.y = referencePlace.getCenter().y;
        xmlReferencePlace.connectedPlaceId = referencePlace.getConnectedPlaceNode().getId();
        return xmlReferencePlace;
    }

    private XmlReferenceArc getXmlReferenceArc(ReferenceArc referenceArc) {
        XmlReferenceArc xmlReferenceArc = new XmlReferenceArc();
        xmlReferenceArc.placeId = referenceArc.getPlaceNode().getId();
        xmlReferenceArc.subnetId = referenceArc.getSubnet().getId();

        List<Point> breakPoints = referenceArc.getBreakPointsCopy();
        if (!referenceArc.isPlaceToTransition()) {
            Collections.reverse(breakPoints);
        }
        for (Point point : breakPoints) {
            XmlPoint xmlPoint = new XmlPoint();
            xmlPoint.x = point.x;
            xmlPoint.y = point.y;
            xmlReferenceArc.breakPoints.add(xmlPoint);
        }
        return xmlReferenceArc;
    }
}
