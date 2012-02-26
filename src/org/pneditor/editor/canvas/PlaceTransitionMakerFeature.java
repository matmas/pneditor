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
import org.pneditor.editor.Root;
import org.pneditor.editor.commands.AddPlaceCommand;
import org.pneditor.editor.commands.AddTransitionCommand;
import org.pneditor.util.CollectionTools;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class PlaceTransitionMakerFeature implements Feature {

	private Root root;
	private Canvas canvas;
	
	public PlaceTransitionMakerFeature(Root root, Canvas canvas) {
		this.root = root;
		this.canvas = canvas;
	}
	
	public void mousePressed(MouseEvent event) {
		int x = event.getX();
		int y = event.getY();
		int mouseButton = event.getButton();
		
		if (mouseButton == MouseEvent.BUTTON1) {
			if (root.getClickedElement() == null) {
				if (root.isSelectedTool_Place()) {
					root.getSelection().clear();
					root.getUndoManager().executeCommand(new AddPlaceCommand(root.getDocument().petriNet.getCurrentSubnet(), x, y, root.getDocument().petriNet));
					root.setClickedElement(CollectionTools.getLastElement(root.getDocument().petriNet.getCurrentSubnet().getElements()));
				}
				else if (root.isSelectedTool_Transition()) {
					root.getSelection().clear();
					root.getUndoManager().executeCommand(new AddTransitionCommand(root.getDocument().petriNet.getCurrentSubnet(), x, y, root.getDocument().petriNet));
					root.setClickedElement(CollectionTools.getLastElement(root.getDocument().petriNet.getCurrentSubnet().getElements()));
				}
			}
		}
	
	}
	
	public void drawForeground(Graphics g) {}
	public void drawBackground(Graphics g) {}
	public void mouseDragged(int x, int y) {}
	public void mouseReleased(int x, int y) {}
	public void setHoverEffects(int x, int y) {}
	public void setCursor(int x, int y) {}
	public void drawMainLayer(Graphics g) {}
	public void mouseMoved(int x, int y) {}
}
