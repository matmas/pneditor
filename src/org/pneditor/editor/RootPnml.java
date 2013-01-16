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

package org.pneditor.editor;

import java.awt.BorderLayout;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.*;
import org.pneditor.editor.actions.*;
import org.pneditor.editor.canvas.*;
import org.pneditor.editor.filechooser.EpsFileType;
import org.pneditor.editor.filechooser.FileType;
import org.pneditor.editor.filechooser.PngFileType;
import org.pneditor.editor.filechooser.ViptoolPnmlFileType;

/**
 * This class is the main point of the application.
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class RootPnml extends RootPflow implements Root, WindowListener, ListSelectionListener, SelectionChangedListener {

	public RootPnml() {
		super();
	}
	
	@Override
	protected void setupMainFrame() {
		List<FileType> openSaveFiletypes = new LinkedList<FileType>();
		openSaveFiletypes.add(new ViptoolPnmlFileType());
		
		List<FileType> exportFiletypes = new LinkedList<FileType>();
		exportFiletypes.add(new EpsFileType());
		exportFiletypes.add(new PngFileType());

		Action newFile = new NewFileAction(this);
		Action openFile = new OpenFileAction(this, openSaveFiletypes);
		Action saveFile = new SaveAction(this, openSaveFiletypes);
		Action saveFileAs = new SaveFileAsAction(this, openSaveFiletypes);
		Action exportFile = new ExportAction(this, exportFiletypes);
		Action quit = new QuitAction(this);
		
		setLabel = new SetLabelAction(this);
		setTokens = new SetTokensAction(this);
		setPlaceStatic = new SetPlaceStaticAction(this);
		setArcMultiplicity = new SetArcMultiplicityAction(this);
		addSelectedTransitionsToSelectedRoles = new AddSelectedTransitionsToSelectedRolesAction(this);
		removeSelectedTransitionsFromSelectedRoles = new RemoveSelectedTransitionsFromSelectedRolesAction(this);
		convertTransitionToSubnet = new ConvertTransitionToSubnetAction(this);
		openSubnet = new OpenSubnetAction(this);
		closeSubnet = new CloseSubnetAction(this);
		delete = new DeleteAction(this);
		
		cutAction = new CutAction(this);
		copyAction = new CopyAction(this);
		pasteAction = new PasteAction(this);

		Action selectTool_SelectionAction = new SelectionSelectToolAction(this);
		Action selectTool_PlaceAction = new PlaceSelectToolAction(this);
		Action selectTool_TransitionAction = new TransitionSelectToolAction(this);
		Action selectTool_ArcAction = new ArcSelectToolAction(this);
		Action selectTool_TokenAction = new TokenSelectToolAction(this);
		
		saveSubnetAs = new SaveSubnetAsAction(this);
		replaceSubnet = new ReplaceSubnetAction(this);
		
		select = new JToggleButton(selectTool_SelectionAction);
		select.setSelected(true);
		place = new JToggleButton(selectTool_PlaceAction);
		transition = new JToggleButton(selectTool_TransitionAction);
		arc = new JToggleButton(selectTool_ArcAction);
		token = new JToggleButton(selectTool_TokenAction);
		
		select.setText("");
		place.setText("");
		transition.setText("");
		arc.setText("");
		token.setText("");
		
		ButtonGroup drawGroup = new ButtonGroup();
		drawGroup.add(select);
		drawGroup.add(place);
		drawGroup.add(transition);
		drawGroup.add(arc);
		drawGroup.add(token);
		
		toolBar.setFloatable(false);
		
		toolBar.add(newFile);
		toolBar.add(openFile);
		toolBar.add(saveFile);
		toolBar.add(exportFile);
		toolBar.addSeparator();

		toolBar.add(cutAction);
		toolBar.add(copyAction);
		toolBar.add(pasteAction);
		toolBar.addSeparator();


		toolBar.add(undo);
		toolBar.add(redo);
		toolBar.add(delete);
		toolBar.addSeparator();
		
		toolBar.add(select);
		toolBar.add(place);
		toolBar.add(transition);
		toolBar.add(arc);
		toolBar.add(token);
//		toolBar.addSeparator();
//		toolBar.add(addSelectedTransitionsToSelectedRoles);
//		toolBar.add(removeSelectedTransitionsFromSelectedRoles);
		
		JMenuBar menuBar = new JMenuBar();
		mainFrame.setJMenuBar(menuBar);
		
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic('F');
		menuBar.add(fileMenu);
		
		JMenu editMenu = new JMenu("Edit");
		editMenu.setMnemonic('E');
		menuBar.add(editMenu);
		
		JMenu drawMenu = new JMenu("Draw");
		drawMenu.setMnemonic('D');
		menuBar.add(drawMenu);
		
		JMenu elementMenu = new JMenu("Element");
		elementMenu.setMnemonic('l');
		menuBar.add(elementMenu);
		
//		JMenu rolesMenu = new JMenu("Roles");
//		rolesMenu.setMnemonic('R');
//		menuBar.add(rolesMenu);
		
//		JMenu subnetMenu = new JMenu("Subnet");
//		subnetMenu.setMnemonic('S');
//		menuBar.add(subnetMenu);

		JMenu helpMenu = new JMenu("Help");
		helpMenu.add(new AboutAction(this));
		menuBar.add(helpMenu);

		fileMenu.add(newFile);
		fileMenu.add(openFile);
		fileMenu.add(saveFile);
		fileMenu.add(saveFileAs);
		fileMenu.add(exportFile);
		fileMenu.addSeparator();
		fileMenu.add(quit);
		
		editMenu.add(undo);
		editMenu.add(redo);
		editMenu.addSeparator();
		editMenu.add(cutAction);
		editMenu.add(copyAction);
		editMenu.add(pasteAction);
		editMenu.add(delete);

		elementMenu.add(setLabel);
		elementMenu.addSeparator();
		elementMenu.add(setTokens);
//		elementMenu.add(setPlaceStatic);
		elementMenu.addSeparator();
		elementMenu.add(setArcMultiplicity);
		
//		rolesMenu.add(addSelectedTransitionsToSelectedRoles);
//		rolesMenu.add(removeSelectedTransitionsFromSelectedRoles);
		
		drawMenu.add(selectTool_SelectionAction);
		drawMenu.addSeparator();
		drawMenu.add(selectTool_PlaceAction);
		drawMenu.add(selectTool_TransitionAction);
		drawMenu.add(selectTool_ArcAction);
		drawMenu.add(selectTool_TokenAction);
		
//		subnetMenu.add(openSubnet);
//		subnetMenu.add(closeSubnet);
//		subnetMenu.add(convertTransitionToSubnet);
//		subnetMenu.add(replaceSubnet);
//		subnetMenu.add(saveSubnetAs);
		
		placePopup = new JPopupMenu();
		placePopup.add(setLabel);
		placePopup.add(setTokens);
//		placePopup.add(setPlaceStatic);
		placePopup.addSeparator();
		placePopup.add(cutAction);
		placePopup.add(copyAction);
		placePopup.add(delete);
		
		transitionPopup = new JPopupMenu();
		transitionPopup.add(setLabel);
//		transitionPopup.add(convertTransitionToSubnet);
//		transitionPopup.add(addSelectedTransitionsToSelectedRoles);
//		transitionPopup.add(removeSelectedTransitionsFromSelectedRoles);
		transitionPopup.addSeparator();
		transitionPopup.add(cutAction);
		transitionPopup.add(copyAction);
		transitionPopup.add(delete);
		
//		Font boldFont = new Font(Font.SANS_SERIF, Font.BOLD, 12);
		
		canvasPopup = new JPopupMenu();
//		canvasPopup.add(closeSubnet).setFont(boldFont);
		canvasPopup.add(pasteAction);
		
		subnetPopup = new JPopupMenu();
//		subnetPopup.add(openSubnet).setFont(boldFont);
//		subnetPopup.add(setLabel);
//		subnetPopup.add(replaceSubnet);
//		subnetPopup.add(saveSubnetAs);
//		subnetPopup.add(addSelectedTransitionsToSelectedRoles);
//		subnetPopup.add(removeSelectedTransitionsFromSelectedRoles);
//		subnetPopup.add(delete);
		
		arcEdgePopup = new JPopupMenu();
		arcEdgePopup.add(setArcMultiplicity);
		arcEdgePopup.add(delete);
		
//		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
//		splitPane.setDividerSize(6);
//		splitPane.setOneTouchExpandable(true);
//		splitPane.setLeftComponent(getRoleEditor());
//		splitPane.setRightComponent(canvas);
//		splitPane.setDividerLocation(120);
		
//		mainFrame.add(splitPane, BorderLayout.CENTER);
		mainFrame.add(canvas, BorderLayout.CENTER);
		mainFrame.add(toolBar, BorderLayout.NORTH);
		
		mainFrame.addWindowListener(this);
		mainFrame.setLocation(50, 50);
		mainFrame.setSize(630,450);
		mainFrame.setVisible(true);
	}

}
