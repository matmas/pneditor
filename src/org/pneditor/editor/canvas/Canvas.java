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

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import org.pneditor.petrinet.Element;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import org.pneditor.editor.Root;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class Canvas extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {
	
	List<Element> highlightedElements = new ArrayList<Element>();
	Cursor alternativeCursor;
	public Cursor activeCursor;
	private List<Feature> features = new ArrayList<Feature>();
	private Root root;
	
	public Canvas(Root root) {
		this.root = root;
		
		setBackground(Color.white);
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		
		features.add(new ClickFeature(root, this));
		features.add(new PanningFeature(root, this));
		features.add(new DraggingFeature(root, this));
		features.add(new SelectionDrawingFeature(root, this));
		features.add(new TokenFeature(root, this));
		features.add(new EdgeZigzagFeature(root, this));
		features.add(new RoleFeature(root, this));
		features.add(new PlaceTransitionMakerFeature(root, this));
		features.add(new PopupMenuFeature(root, this));
		features.add(new SubnetFeature(root, this));
		features.add(new ArcFeature(root, this));
		features.add(new PetriNetFeature(root, this));
	}
	
	public int getTranslationX() {
		return root.getDocument().petriNet.getCurrentSubnet().getViewTranslation().x + getWidth() / 2;
	}
	
	public int getTranslationY() {
		return root.getDocument().petriNet.getCurrentSubnet().getViewTranslation().y + getHeight() / 2;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		super.paintComponent(g);
		g.translate(getTranslationX(), getTranslationY());
		
		for (Feature f : features) {
			f.drawBackground(g);
		}
		for (Feature f : features) {
			f.drawMainLayer(g);
		}
		for (Feature f : features) {
			f.drawForeground(g);
		}
	}
	
	
	
	
	
	
	
	public void mouseWheelMoved(MouseWheelEvent e)
    {
		if (e.getWheelRotation() == 1) {
			if (root.isSelectedTool_Place())
				root.selectTool_Transition();
			else if (root.isSelectedTool_Transition())
				root.selectTool_Arc();
			else if (root.isSelectedTool_Arc())
				root.selectTool_Token();
			else if (root.isSelectedTool_Token())
				root.selectTool_Place();
			else
				root.selectTool_Place();
		}
		else if (e.getWheelRotation() == -1) {
			if (root.isSelectedTool_Place())
				root.selectTool_Token();
			else if (root.isSelectedTool_Transition())
				root.selectTool_Place();
			else if (root.isSelectedTool_Arc())
				root.selectTool_Transition();
			else if (root.isSelectedTool_Token())
				root.selectTool_Arc();
			else
				root.selectTool_Token();
		}
		repaint();
		setHoverEffects(e.getX(), e.getY());
    }
	
	public void mousePressed(MouseEvent event) {
		int x = event.getX() - getTranslationX();
		int y = event.getY() - getTranslationY();
		event = new MouseEvent(
			(Component)event.getSource(),
			event.getID(),
			event.getWhen(),
			event.getModifiers(),
			x,
			y,
			event.getXOnScreen(),
			event.getYOnScreen(),
			event.getClickCount(),
			event.isPopupTrigger(),
			event.getButton());
		
		root.setClickedElement(root.getDocument().petriNet.getCurrentSubnet().getElementByXY(x, y));
		
		for (Feature f : features) {
			f.mousePressed(event);
		}
		
		if (event.getButton() == MouseEvent.BUTTON3) {
			if (root.getClickedElement() == null) { // The user did not click on a shape.
				root.selectTool_Select();
			}
		}
		
		setCursor(x, y);
		setHoverEffects(x, y);
	}
	
	public void mouseDragged(MouseEvent event) {
		int x = event.getX() - getTranslationX();
		int y = event.getY() - getTranslationY();
		
		for (Feature f : features) {
			f.mouseDragged(x, y);
		}
		
		setHoverEffects(x, y);
	}

	public void mouseReleased(MouseEvent evt) {
		int x = evt.getX() - getTranslationX();
		int y = evt.getY() - getTranslationY();
		
		for (Feature f : features) {
			f.mouseReleased(x, y);
		}
		
		setHoverEffects(x, y);
		setCursor(x, y);
	}

	public void mouseMoved(MouseEvent evt) {
		int x = evt.getX() - getTranslationX();
		int y = evt.getY() - getTranslationY();
		
		for (Feature f : features) {
			f.mouseMoved(x, y);
		}
		
		setHoverEffects(x, y);
		setCursor(x, y);
	}
	
	void setHoverEffects(int x, int y) {
		if (!highlightedElements.isEmpty()) {
			for (Element element : highlightedElements) {
				element.highlightColor = null;
			}
			highlightedElements.clear();
			repaint();
		}
		for (Feature f : features) {
			f.setHoverEffects(x, y);
		}
	}
	
	void setCursor(int x, int y) {
		alternativeCursor = null;
		
		for (Feature f : features) {
			f.setCursor(x, y);
		}
		
		Cursor cursor;
		if (alternativeCursor != null)
			cursor = alternativeCursor;
		else
			cursor = activeCursor;
		
		if (getCursor() != cursor) {
			setCursor(cursor);
		}
	}
	
	public void mouseEntered(MouseEvent evt) { }
	public void mouseExited(MouseEvent evt) { }
	public void mouseClicked(MouseEvent evt) { }

}
