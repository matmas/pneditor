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

package org.pneditor.editor.canvas;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;
import org.pneditor.editor.Root;
import org.pneditor.petrinet.Subnet;
import org.pneditor.petrinet.Transition;
import org.pneditor.petrinet.Role;
import org.pneditor.petrinet.TransitionNode;
import org.pneditor.util.CollectionTools;
import org.pneditor.util.GraphicsTools;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class RoleFeature implements Feature {

	private Root root;
	private Canvas canvas;
	
	private BufferedImage fullRoleImage, partialRoleImage, mixedRoleImage;
	
	public RoleFeature(Root root, Canvas canvas) {
		this.root = root;
		this.canvas = canvas;
		fullRoleImage = GraphicsTools.getBufferedImage("pneditor/canvas/fullrole.gif");
		partialRoleImage = GraphicsTools.getBufferedImage("pneditor/canvas/partialrole.gif");
		mixedRoleImage = GraphicsTools.getBufferedImage("pneditor/canvas/mixedrole.gif");
	}
	
	public void drawForeground(Graphics g) {
		Set<TransitionNode> partiallyIncluded = new HashSet<TransitionNode>();
		Set<TransitionNode> fullyIncluded = null;
		Set<Subnet> mixedIncluded = new HashSet<Subnet>();
		
		for (Role role : root.getRoleEditor().getSelectedElements()) {
			Set<TransitionNode> included = new HashSet<TransitionNode>();
			
			for (Transition transition : root.getDocument().petriNet.getCurrentSubnet().getTransitions()) {
				if (role.transitions.contains(transition)) {
					included.add(transition);
				}
			}
			for (Subnet subnet : root.getDocument().petriNet.getCurrentSubnet().getSubnets()) {
				Set<Transition> transitions = subnet.getTransitionsRecursively();
				if (role.transitions.containsAll(transitions)) {
					included.add(subnet);
				}
				else if (CollectionTools.containsAtLeastOne(role.transitions, transitions)) {
					mixedIncluded.add(subnet);
				}
			}
			
			if (fullyIncluded == null) {
				fullyIncluded = new HashSet<TransitionNode>();
				fullyIncluded.addAll(included);
			}
			partiallyIncluded.addAll(included);
			fullyIncluded.retainAll(included);
		}
		
		if (fullyIncluded != null) {
			partiallyIncluded.removeAll(fullyIncluded);
			for (TransitionNode transition : partiallyIncluded) {
				GraphicsTools.drawImageCentered(g, partialRoleImage, transition.getStart().x + transition.getWidth() / 2, transition.getStart().y + transition.getHeight() / 2);
			}
			for (TransitionNode transition : fullyIncluded) {
				GraphicsTools.drawImageCentered(g, fullRoleImage, transition.getStart().x + transition.getWidth() / 2, transition.getStart().y + transition.getHeight() / 2);
			}
		}
		for (Subnet subnet : mixedIncluded) {
			GraphicsTools.drawImageCentered(g, mixedRoleImage, subnet.getStart().x + subnet.getWidth() / 2, subnet.getStart().y + subnet.getHeight() / 2);
		}
	}
	
	public void mousePressed(MouseEvent event) {}
	public void mouseDragged(int x, int y) {}
	public void mouseReleased(int x, int y) {}
	public void setHoverEffects(int x, int y) {}
	public void drawBackground(Graphics g) {}
	public void setCursor(int x, int y) {}
	public void drawMainLayer(Graphics g) {}
	public void mouseMoved(int x, int y) {}
}
