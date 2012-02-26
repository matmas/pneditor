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
import javax.swing.JPopupMenu;
import org.pneditor.editor.Root;
import org.pneditor.petrinet.ArcEdge;
import org.pneditor.petrinet.PlaceNode;
import org.pneditor.petrinet.Subnet;
import org.pneditor.petrinet.Transition;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class PopupMenuFeature implements Feature {

	private Root root;
	private Canvas canvas;

	public PopupMenuFeature(Root root, Canvas canvas) {
		this.root = root;
		this.canvas = canvas;
	}
	
	public void mousePressed(MouseEvent event) {
		int x = event.getX();
		int y = event.getY();
		int mouseButton = event.getButton();
		
		int realX = x + canvas.getTranslationX();
		int realY = y + canvas.getTranslationY();
		
		if (mouseButton == MouseEvent.BUTTON3) {
			if (root.getClickedElement() != null &&
				(
					root.isSelectedTool_Select() ||
					root.isSelectedTool_Place() ||
					root.isSelectedTool_Transition() ||
					root.isSelectedTool_Arc() ||
					root.isSelectedTool_Token() && !(root.getClickedElement() instanceof PlaceNode)
				)
			) {
				if (root.getClickedElement() instanceof PlaceNode) {
					showPopup(root.getPlacePopup(), realX, realY);
					if (!root.getSelection().contains(root.getClickedElement())) {
						root.getSelection().clear();
					}
				}
				else if (root.getClickedElement() instanceof Subnet) {
					showPopup(root.getSubnetPopup(), realX, realY);
					if (!root.getSelection().contains(root.getClickedElement())) {
						root.getSelection().clear();
					}
				}
				else if (root.getClickedElement() instanceof Transition) {
					showPopup(root.getTransitionPopup(), realX, realY);
					if (!root.getSelection().contains(root.getClickedElement())) {
						root.getSelection().clear();
					}
				}
				else if (root.getClickedElement() instanceof ArcEdge) {
					showPopup(root.getArcEdgePopup(), realX, realY);
					if (!root.getSelection().contains(root.getClickedElement())) {
						root.getSelection().clear();
					}
				}
			}
			
			if (root.getClickedElement() == null &&
				root.isSelectedTool_Select()
			) {
				showPopup(root.getCanvasPopup(), realX, realY);
			}
		}
	}
	
	private void showPopup(JPopupMenu popupMenu, int clickedX, int clickedY) {
		popupMenu.show(canvas, clickedX - 10, clickedY - 2);
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
