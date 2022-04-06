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
package org.pneditor.petrinet;

import org.pneditor.editor.PNEditor;

/**
 * Represents place in Petri net
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class Place extends PlaceNode implements Cloneable {

    private boolean isStatic = false;
    
    private int tokenLimit = 0;
    
    @Override
    public boolean isStatic() {
        return isStatic;
    }

    @Override
    public void setStatic(boolean isStatic) {
    	if (this.getTokenLimit()==0) {
    		this.isStatic = isStatic;
    	}
    }
    
    @Override
    public int getTokenLimit() {
    	return tokenLimit;
    }
    
    @Override
    public void setTokenLimit (int tokenLimit) {
    	//if there are already some tokens on the place, and they are in greater amount 
    	// than the wanted invariant, the number of tokens will be set to the invariant
    	if (isStatic == false) {
	    	if (tokenLimit != 0) {
		    	int currentTokens = PNEditor.getRoot().getCurrentMarking().getTokens(this);
		    	if (currentTokens> tokenLimit) {
		    		PNEditor.getRoot().getCurrentMarking().setTokens(this,  tokenLimit);
		    	}
		    	this.tokenLimit = tokenLimit;
	    	}
	    	else if (tokenLimit == 0) {
	    		this.tokenLimit = tokenLimit;
	    	}
	    	
    	}
    }
    
    
}
