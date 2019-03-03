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

import org.pneditor.petrinet.Marking;
import org.pneditor.petrinet.PlaceNode;
import org.pneditor.util.Command;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class SetTokenLimitCommand implements Command {

    private PlaceNode placeNode;
    private int newLimitValue;
    private Marking marking;
    

    public SetTokenLimitCommand(PlaceNode placeNode, int limit, Marking marking) {
        this.placeNode = placeNode;
        this.newLimitValue = limit;
        this.marking = marking;
    }

    private int oldLimitValue;
    private int oldTokenValue;
    
    
    public void execute() {
        this.oldTokenValue = marking.getTokens(placeNode);
        this.oldLimitValue = placeNode.getTokenLimit();
        
        placeNode.setTokenLimit(newLimitValue);
    }

    public void undo() {
        this.placeNode.setTokenLimit(oldLimitValue);
        this.marking.setTokens(this.placeNode, this.oldTokenValue);
    }

    public void redo() {
        execute();
    }

    @Override
    public String toString() {
        return "Set/unset place node token limit";
    }
}
