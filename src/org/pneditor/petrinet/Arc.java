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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class Arc extends ArcEdge implements Cloneable{

	private int multiplicity = 1;
        private boolean inhibitory = false;
        private boolean reset = false;
        
        
	public Arc(Node sourceNode) {
		setSource(sourceNode);
		setStart(sourceNode.getCenter().x, sourceNode.getCenter().y);
		setEnd(sourceNode.getCenter().x, sourceNode.getCenter().y);
	}

	public Arc(Node source, Node destination) {
		setSource(source);
		setDestination(destination);
	}

	public Arc(PlaceNode placeNode, Transition transition, boolean placeToTransition) {
		super(placeNode, transition, placeToTransition);
	}

	public int getMultiplicity() {
		return multiplicity;
	}

	public void setMultiplicity(int multiplicity) {
		this.multiplicity = multiplicity;
	}
        
        public void setInhibitory(boolean value) {
                this.inhibitory = value;            
        }
        
        public boolean getInhibitory() {
                return this.inhibitory;            
        }
        
    	public boolean setReset(boolean value) {
    		return this.reset = value;
    	}

    	public boolean getReset() {
    		 return this.reset;
    	}
	
	@Override
	public void draw(Graphics g, DrawingOptions drawingOptions) {
                if(this.inhibitory) 
                        this.color = Color.MAGENTA;
                else if(this.reset)
                        this.color = Color.RED;
                	else 
                		this.color = Color.BLACK;
                
		g.setColor(color);
		drawSegmentedLine(g);
                Point arrowTip = computeArrowTipPoint();
                if(this.inhibitory){                    
                	 drawCircle(g, arrowTip);        	
                }
                else{
                	if(this.reset){
                    	drawArrowDouble(g, arrowTip);
                    }else{
                    	drawArrow(g, arrowTip);
                    } 
                }
                
		if (multiplicity >= 2) {
			drawMultiplicityLabel(g, arrowTip, multiplicity);
		}
	}
	
	public Transition getTransition() {
		return (Transition)getTransitionNode();
	}
	
	@Override
	public Arc getClone(){
		Arc arc = (Arc)super.getClone();
		arc.multiplicity = this.multiplicity;
		return arc;
    }
}
