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

import java.awt.Point;
import java.util.List;
import org.pneditor.petrinet.Edge;
import org.pneditor.util.Command;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class SetEdgeZigzagPointCommand implements Command {

    private Edge edge;
    private Point startingLocation;
    private Point targetLocation;
    private List<Point> oldBreakPoints;

    public SetEdgeZigzagPointCommand(Edge edge, Point startingLocation, Point targetLocation) {
        this.edge = edge;
        this.startingLocation = new Point(startingLocation);
        this.targetLocation = new Point(targetLocation);
    }

    public void execute() {
        oldBreakPoints = edge.getBreakPointsCopy();
        redo();
    }

    public void undo() {
        edge.setBreakPoints(oldBreakPoints);
    }

    public void redo() {
        edge.addOrGetBreakPoint(new Point(startingLocation)).setLocation(targetLocation);
        edge.cleanupUnecessaryBreakPoints();
    }

    @Override
    public String toString() {
        return "Set edge break point";
    }

}
