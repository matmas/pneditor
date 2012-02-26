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
import org.pneditor.petrinet.Element;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import org.pneditor.editor.Root;
import org.pneditor.editor.commands.MoveElementCommand;
import org.pneditor.editor.commands.MoveElementsCommand;
import org.pneditor.petrinet.Node;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
class DraggingFeature implements Feature {
	
	private Root root;
	private Canvas canvas;
	
	DraggingFeature(Root root, Canvas canvas) {
		this.root = root;
		this.canvas = canvas;
	}
	
	private Element draggedElement = null;
	private Point deltaPosition;
	private int prevDragX;  // During dragging, these record the x and y coordinates of the
	private int prevDragY;  //    previous position of the mouse.
	
	
	public void mousePressed(MouseEvent event) {
		int x = event.getX();
		int y = event.getY();
		int mouseButton = event.getButton();
		boolean doubleclick = event.getClickCount() == 2;
		
		if (!doubleclick) {
			if (mouseButton == MouseEvent.BUTTON1 &&
				root.getClickedElement() != null &&
				(
					root.isSelectedTool_Select() ||
					root.isSelectedTool_Place() ||
					root.isSelectedTool_Transition()
				) &&
				root.getClickedElement() instanceof Node
			) {
				if (!root.getSelection().contains(root.getClickedElement())) {
					root.getSelection().clear();
				}

				draggedElement = root.getDocument().petriNet.getCurrentSubnet().getElementByXY(x, y);
				deltaPosition = new Point();
				prevDragX = x;
				prevDragY = y;
			}
		}
	}

	public void mouseDragged(int x, int y) {
		if (draggedElement != null) {
			doTheMoving(x, y);
			canvas.repaint();  // redraw canvas to show shape in new position
			deltaPosition.translate(x - prevDragX, y - prevDragY);
			prevDragX = x;
			prevDragY = y;
		}
	}

	public void mouseReleased(int x, int y) {
		if (draggedElement != null) {
			doTheMoving(x, y);
			deltaPosition.translate(x - prevDragX, y - prevDragY);
			saveTheMoving();
			canvas.repaint();
			draggedElement = null;  // Dragging is finished.
		}
	}

	private void doTheMoving(int mouseX, int mouseY) {
		if (!root.getSelection().isEmpty()) {
			for (Element selectedElement : root.getSelection()) {
				selectedElement.moveBy(mouseX - prevDragX, mouseY - prevDragY);
			}
		}
		else {
			draggedElement.moveBy(mouseX - prevDragX, mouseY - prevDragY);
		}
	}
	
	private void saveTheMoving() {
		if (!deltaPosition.equals(new Point(0, 0))) {
			if (!root.getSelection().isEmpty()) {
				for (Element selectedElement : root.getSelection()) {
					selectedElement.moveBy(-deltaPosition.x, -deltaPosition.y); //move back to original positions
				}
				root.getUndoManager().executeCommand(new MoveElementsCommand(root.getSelection().getElements(), deltaPosition));
			}
			else {
				draggedElement.moveBy(-deltaPosition.x, -deltaPosition.y);  //move back to original position
				root.getUndoManager().executeCommand(new MoveElementCommand(draggedElement, deltaPosition));
			}
		}
	}

	public void setCursor(int x, int y) {
		Element element = root.getDocument().petriNet.getCurrentSubnet().getElementByXY(x, y);

		if (root.isSelectedTool_Select() ||
			root.isSelectedTool_Place() ||
			root.isSelectedTool_Transition()
		) {
			if (element != null && element instanceof Node) {
				canvas.alternativeCursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
			}
		}
	}
	
	public void drawForeground(Graphics g) {}
	public void drawBackground(Graphics g) {}
	public void setHoverEffects(int x, int y) {}
	public void drawMainLayer(Graphics g) {}
	public void mouseMoved(int x, int y) {}
}
