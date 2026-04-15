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
package org.pneditor.editor.commands;

import org.pneditor.petrinet.Arc;
import org.pneditor.petrinet.ArcEdge;
import org.pneditor.petrinet.Element;
import org.pneditor.petrinet.Place;
import org.pneditor.petrinet.ReferencePlace;
import org.pneditor.petrinet.ReferenceArc;
import org.pneditor.petrinet.TransitionNode;
import org.pneditor.util.Command;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class DeleteElementCommand implements Command {

    private Element element;
    private Command deleteElement;

    public DeleteElementCommand(Element elementToDelete) {
        this.element = elementToDelete;
        if (element instanceof ReferencePlace) {
            //do nothing
        } else if (element instanceof Place) {
            Place place = (Place) element;
            deleteElement = new DeletePlaceNodeCommand(place);
        } else if (element instanceof TransitionNode) {
            TransitionNode transition = (TransitionNode) element;
            deleteElement = new DeleteTransitionNodeCommand(transition);
        } else if (element instanceof ReferenceArc) {
            ReferenceArc referenceArc = (ReferenceArc) element;
            deleteElement = new DeleteReferenceArcCommand(referenceArc);
        } else if (element instanceof Arc) {
            Arc arc = (Arc) element;
            deleteElement = new DeleteArcCommand(arc);
        }
    }

    public void execute() {
        if (deleteElement != null) {
            deleteElement.execute();
        }
    }

    public void undo() {
        if (deleteElement != null) {
            deleteElement.undo();
        }
    }

    public void redo() {
        if (deleteElement != null) {
            deleteElement.redo();
        }
    }

    @Override
    public String toString() {
        return deleteElement.toString();
    }
}
