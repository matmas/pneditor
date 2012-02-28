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
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.event.*;
import org.pneditor.editor.actions.*;
import org.pneditor.editor.actions.algorithms.BoundednessAction;
import org.pneditor.editor.canvas.*;
import org.pneditor.petrinet.*;
import org.pneditor.editor.filechooser.EpsFileType;
import org.pneditor.editor.filechooser.FileType;
import org.pneditor.editor.filechooser.PflowFileType;
import org.pneditor.editor.filechooser.PngFileType;
import org.pneditor.editor.filechooser.ViptoolPnmlFileType;
import org.pneditor.util.*;

/**
 * This class is the main point of the application.
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class RootPflow implements Root, WindowListener, ListSelectionListener, SelectionChangedListener {
	
	private static final String APP_NAME = "PNEditor";
	private static final String APP_VERSION = "0.51";
	
	public RootPflow() {
		loadPreferences();
		selection.setSelectionChangedListener(this);
		
		roleEditor = new ListEditor<Role>("Roles", document.roles, getParentFrame());
		roleEditor.addButton.setIcon(GraphicsTools.getIcon("pneditor/addrole.gif"));
		roleEditor.deleteButton.setIcon(GraphicsTools.getIcon("pneditor/deleterole.gif"));
		roleEditor.addButton.setToolTipText("Add role");
		roleEditor.editButton.setToolTipText("Edit role properties");
		roleEditor.deleteButton.setToolTipText("Delete role");
		roleEditor.addListSelectionListener(this);
		
		setupMainFrame();
		mainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setupFrameIcons();
	}

	private static final String CURRENT_DIRECTORY = "current_directory";

	private void loadPreferences() {
		Preferences preferences = Preferences.userNodeForPackage(this.getClass());
		setCurrentDirectory(new File(preferences.get(CURRENT_DIRECTORY, System.getProperty("user.home"))));
	}
	
	private void savePreferences() {
		Preferences preferences = Preferences.userNodeForPackage(this.getClass());
		preferences.put(CURRENT_DIRECTORY, getCurrentDirectory().toString());
	}

	// Undo manager - per tab
	protected UndoAction undo = new UndoAction(this);
	protected RedoAction redo = new RedoAction(this);
	protected UndoManager undoManager = new UndoManager(this, undo, redo);
	public UndoManager getUndoManager() {
		return undoManager;
	}
	
	// Current directory - per application
	protected File currentDirectory;
	public File getCurrentDirectory() {
		return currentDirectory;
	}
	public void setCurrentDirectory(File currentDirectory) {
		this.currentDirectory = currentDirectory;
	}
	
	// Main frame - per application
	protected MainFrame mainFrame = new MainFrame(getNewWindowTitle());
	public Frame getParentFrame() {
		return mainFrame;
	}
	
	// Document - per tab
	protected Document document = new Document();
	public Document getDocument() {
		return document;
	}
	public void setDocument(Document document) {
		this.document = document;
		getDocument().petriNet.resetView();
		getRoleEditor().setModel(getDocument().roles);
		getUndoManager().eraseAll();
		refreshAll();
	}
	
	// Clicked element - per tab
	protected Element clickedElement = null;
	public Element getClickedElement() {
		return clickedElement;
	}
	public void setClickedElement(Element clickedElement) {
		this.clickedElement = clickedElement;
		enableOnlyPossibleActions();
	}
	
	// Selection - per tab
	protected Selection selection = new Selection();
	public Selection getSelection() {
		return selection;
	}
	public void selectionChanged() {
		enableOnlyPossibleActions();
	}
	
	// Selection + clicked element
	public Set<Element> getSelectedElementsWithClickedElement() {
		Set<Element> selectedElements = new HashSet<Element>();
		selectedElements.addAll(getSelection().getElements());
		selectedElements.add(getClickedElement());
		return selectedElements;
	}
	
	// List editor - per tab
	protected ListEditor<Role> roleEditor; //TODO
	public void valueChanged(ListSelectionEvent e) {
		enableOnlyPossibleActions();
		repaintCanvas();
	}
	
	
	//per tab
	public void selectTool_Select() {
		select.setSelected(true);
		canvas.activeCursor = Cursor.getDefaultCursor();
		canvas.setCursor(canvas.activeCursor);
		repaintCanvas();
	}
	public boolean isSelectedTool_Select() {
		return select.isSelected();
	}
	
	public void selectTool_Place() {
		place.setSelected(true);
		canvas.activeCursor = GraphicsTools.getCursor("pneditor/canvas/place.gif", new Point(16, 16));
		canvas.setCursor(canvas.activeCursor);
		repaintCanvas();
	}
	public boolean isSelectedTool_Place() {
		return place.isSelected();
	}
	
	public void selectTool_Transition() {
		transition.setSelected(true);
		canvas.activeCursor = GraphicsTools.getCursor("pneditor/canvas/transition.gif", new Point(16, 16));
		canvas.setCursor(canvas.activeCursor);
		repaintCanvas();
	}
	public boolean isSelectedTool_Transition() {
		return transition.isSelected();
	}
	
	public void selectTool_Arc() {
		arc.setSelected(true);
		canvas.activeCursor = GraphicsTools.getCursor("pneditor/canvas/arc.gif", new Point(0, 0));
		canvas.setCursor(canvas.activeCursor);
		repaintCanvas();
	}
	public boolean isSelectedTool_Arc() {
		return arc.isSelected();
	}
	
	public void selectTool_Token() {
		token.setSelected(true);
		canvas.activeCursor = GraphicsTools.getCursor("pneditor/canvas/token_or_fire.gif", new Point(16, 0));
		canvas.setCursor(canvas.activeCursor);
		repaintCanvas();
	}
	public boolean isSelectedTool_Token() {
		return token.isSelected();
	}

	public ListEditor<Role> getRoleEditor() {
		return roleEditor;
	}

	public JPopupMenu getPlacePopup() {
		return placePopup;
	}

	public JPopupMenu getTransitionPopup() {
		return transitionPopup;
	}

	public JPopupMenu getArcEdgePopup() {
		return arcEdgePopup;
	}

	public JPopupMenu getSubnetPopup() {
		return subnetPopup;
	}

	public JPopupMenu getCanvasPopup() {
		return canvasPopup;
	}
	
	//per tab
	protected Canvas canvas = new Canvas(this);
	protected JPopupMenu placePopup;
	protected JPopupMenu transitionPopup;
	protected JPopupMenu arcEdgePopup;
	protected JPopupMenu subnetPopup;
	protected JPopupMenu canvasPopup;
	

	//per application
	protected JToggleButton select, place, transition, arc, token;
	protected Action setLabel, setTokens, setArcMultiplicity, delete;
	protected Action setPlaceStatic;
	protected Action addSelectedTransitionsToSelectedRoles;
	protected Action removeSelectedTransitionsFromSelectedRoles;
	protected Action convertTransitionToSubnet;
	protected Action replaceSubnet;
	protected Action saveSubnetAs;
	protected Action cutAction, copyAction, pasteAction;
	
	//per application
	protected Action openSubnet;
	protected Action closeSubnet;

	public void openSubnet() {
		openSubnet.actionPerformed(null);
	}

	public void closeSubnet() {
		closeSubnet.actionPerformed(null);
	}
	
	public void refreshAll() {
		canvas.repaint();
		enableOnlyPossibleActions();
		getRoleEditor().refreshSelected();
	}
	
	public void repaintCanvas() {
		canvas.repaint();
	}
	
	
	
	protected void enableOnlyPossibleActions() {
		boolean isDeletable = clickedElement != null &&
			!(clickedElement instanceof ReferencePlace) ||
			!selection.isEmpty() &&
			!CollectionTools.containsOnlyInstancesOf(selection.getElements(), ReferencePlace.class);
		boolean isCutable = isDeletable;
		boolean isCopyable = isCutable;
		boolean isPastable = !clipboard.isEmpty();
		boolean isPlaceNode = clickedElement instanceof PlaceNode;
		boolean isArc = clickedElement instanceof Arc;
		boolean isTransitionNode = clickedElement instanceof TransitionNode;
		boolean isTransition = clickedElement instanceof Transition;
		boolean isSubnet = clickedElement instanceof Subnet;
		boolean areSubnets = !selection.getSubnets().isEmpty();
		boolean areTransitionNodes = !selection.getTransitionNodes().isEmpty();
		boolean areTransitions = !selection.getTransitions().isEmpty();
		boolean roleSelected = !roleEditor.getSelectedElements().isEmpty();
		boolean isParent = !document.petriNet.isCurrentSubnetRoot();

		cutAction.setEnabled(isCutable);
		copyAction.setEnabled(isCopyable);
		pasteAction.setEnabled(isPastable);
		delete.setEnabled(isDeletable);
		setArcMultiplicity.setEnabled(isArc);
		setTokens.setEnabled(isPlaceNode);
		setLabel.setEnabled(isPlaceNode || isTransitionNode);
		addSelectedTransitionsToSelectedRoles.setEnabled((isTransitionNode || areTransitionNodes) && roleSelected);
		removeSelectedTransitionsFromSelectedRoles.setEnabled((isTransitionNode || areTransitionNodes) && roleSelected);
		convertTransitionToSubnet.setEnabled(isTransition || areTransitions || isSubnet || areSubnets);
		replaceSubnet.setEnabled(isSubnet || areSubnets);
		saveSubnetAs.setEnabled(isSubnet);
		openSubnet.setEnabled(isSubnet);
		closeSubnet.setEnabled(isParent);
		undo.setEnabled(getUndoManager().isUndoable());
		redo.setEnabled(getUndoManager().isRedoable());
		setPlaceStatic.setEnabled(isPlaceNode);
	}
	
	public void windowClosed(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
	public void windowClosing(WindowEvent e) {
		quitApplication();
	}
	
	/**
	 * Terminates the application
	 */
	public void quitApplication() {
		if (!this.isModified()) {
			quitNow();
		}
		mainFrame.setState(Frame.NORMAL);
		mainFrame.setVisible(true);
		int answer = JOptionPane.showOptionDialog(
			this.getParentFrame(),
			"Any unsaved changes will be lost. Really quit?",
			"Quit",
			JOptionPane.DEFAULT_OPTION,
			JOptionPane.WARNING_MESSAGE,
			null,
			new String[] {"Quit", "Cancel"},
			"Cancel");
		if (answer == JOptionPane.YES_OPTION) {
			quitNow();
		}
	}

	private void quitNow() {
		savePreferences();
		System.exit(0);
	}

	protected JToolBar toolBar = new JToolBar();

	protected void setupFrameIcons() {
		List<Image> icons = new LinkedList<Image>();
		icons.add(GraphicsTools.getBufferedImage("icon16.png"));
		icons.add(GraphicsTools.getBufferedImage("icon32.png"));
		icons.add(GraphicsTools.getBufferedImage("icon48.png"));
		mainFrame.setIconImages(icons);
	}

	protected void setupMainFrame() {
		List<FileType> openSaveFiletypes = new LinkedList<FileType>();
		openSaveFiletypes.add(new PflowFileType());
		List<FileType> importFiletypes = new LinkedList<FileType>();
		importFiletypes.add(new ViptoolPnmlFileType());
		List<FileType> exportFiletypes = new LinkedList<FileType>();
		exportFiletypes.add(new ViptoolPnmlFileType());
		exportFiletypes.add(new EpsFileType());
		exportFiletypes.add(new PngFileType());
		
		Action newFile = new NewFileAction(this);
		Action openFile = new OpenFileAction(this, openSaveFiletypes);
		Action saveFile = new SaveAction(this, openSaveFiletypes);
		Action saveFileAs = new SaveFileAsAction(this, openSaveFiletypes);
		Action importFile = new ImportAction(this, importFiletypes);
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
		toolBar.add(importFile);
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
		toolBar.addSeparator();
		toolBar.add(addSelectedTransitionsToSelectedRoles);
		toolBar.add(removeSelectedTransitionsFromSelectedRoles);
		
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
		
		JMenu rolesMenu = new JMenu("Roles");
		rolesMenu.setMnemonic('R');
		menuBar.add(rolesMenu);
		
		JMenu subnetMenu = new JMenu("Subnet");
		subnetMenu.setMnemonic('S');
		menuBar.add(subnetMenu);
		
		//asus 2012 algorithms menu
		JMenu algorithmsMenu = new JMenu("Algorithms");
		algorithmsMenu.setMnemonic('A');
		menuBar.add(algorithmsMenu);
		
		//asus 2012 algorithms submenu items
		algorithmsMenu.add(new BoundednessAction(this));

		JMenu helpMenu = new JMenu("Help");
		helpMenu.add(new AboutAction(this));
		menuBar.add(helpMenu);
		
		fileMenu.add(newFile);
		fileMenu.add(openFile);
		fileMenu.add(saveFile);
		fileMenu.add(saveFileAs);
		fileMenu.add(importFile);
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
		elementMenu.add(setPlaceStatic);
		elementMenu.addSeparator();
		elementMenu.add(setArcMultiplicity);
		
		rolesMenu.add(addSelectedTransitionsToSelectedRoles);
		rolesMenu.add(removeSelectedTransitionsFromSelectedRoles);
		
		drawMenu.add(selectTool_SelectionAction);
		drawMenu.addSeparator();
		drawMenu.add(selectTool_PlaceAction);
		drawMenu.add(selectTool_TransitionAction);
		drawMenu.add(selectTool_ArcAction);
		drawMenu.add(selectTool_TokenAction);
		
		subnetMenu.add(openSubnet);
		subnetMenu.add(closeSubnet);
		subnetMenu.add(replaceSubnet);
		subnetMenu.add(saveSubnetAs);
		subnetMenu.add(convertTransitionToSubnet);
		
		placePopup = new JPopupMenu();
		placePopup.add(setLabel);
		placePopup.add(setTokens);
		placePopup.add(setPlaceStatic);
		placePopup.addSeparator();
		placePopup.add(cutAction);
		placePopup.add(copyAction);
		placePopup.add(delete);
		
		transitionPopup = new JPopupMenu();
		transitionPopup.add(setLabel);
		transitionPopup.add(convertTransitionToSubnet);
		transitionPopup.add(addSelectedTransitionsToSelectedRoles);
		transitionPopup.add(removeSelectedTransitionsFromSelectedRoles);
		transitionPopup.addSeparator();
		transitionPopup.add(cutAction);
		transitionPopup.add(copyAction);
		transitionPopup.add(delete);
		
		Font boldFont = new Font(Font.SANS_SERIF, Font.BOLD, 12);
		
		canvasPopup = new JPopupMenu();
		canvasPopup.add(closeSubnet).setFont(boldFont);
		canvasPopup.add(pasteAction);
		
		subnetPopup = new JPopupMenu();
		subnetPopup.add(openSubnet).setFont(boldFont);
		subnetPopup.add(setLabel);
		subnetPopup.add(replaceSubnet);
		subnetPopup.add(saveSubnetAs);
		subnetPopup.add(convertTransitionToSubnet);
		subnetPopup.add(addSelectedTransitionsToSelectedRoles);
		subnetPopup.add(removeSelectedTransitionsFromSelectedRoles);
		subnetPopup.addSeparator();
		subnetPopup.add(cutAction);
		subnetPopup.add(copyAction);
		subnetPopup.add(delete);
		
		arcEdgePopup = new JPopupMenu();
		arcEdgePopup.add(setArcMultiplicity);
		arcEdgePopup.add(delete);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
		splitPane.setDividerSize(6);
		splitPane.setOneTouchExpandable(true);
		splitPane.setLeftComponent(getRoleEditor());
		splitPane.setRightComponent(canvas);
		splitPane.setDividerLocation(120);
		
		mainFrame.add(splitPane, BorderLayout.CENTER);
		mainFrame.add(toolBar, BorderLayout.NORTH);
		
		mainFrame.addWindowListener(this);
		mainFrame.setLocation(50, 50);
		mainFrame.setSize(700,450);
		mainFrame.setVisible(true);
	}

	public Marking getCurrentMarking() {
		return getDocument().petriNet.getInitialMarking();
	}

	public void setCurrentMarking(Marking currentMarking) {
	}

	protected LocalClipboard clipboard = new LocalClipboard();

	public LocalClipboard getClipboard() {
		return clipboard;
	}

	private boolean isModified = false;

	public boolean isModified() {
		return isModified;
	}

	public void setModified(boolean isModified) {
		this.isModified = isModified;
		mainFrame.setTitle(getNewWindowTitle());
	}

	private String getNewWindowTitle() {
		String windowTitle = "";
		if (getCurrentFile() != null) {
			windowTitle += getCurrentFile().getName();
		}
		else {
			windowTitle += "Untitled";
		}
		if (isModified()) {
			windowTitle += " [modified]";
		}
		windowTitle += " - " + getAppShortName();
		return windowTitle;
	}

	private File currentFile = null;

	public File getCurrentFile() {
		return currentFile;
	}

	public void setCurrentFile(File currentFile) {
		this.currentFile = currentFile;
		mainFrame.setTitle(getNewWindowTitle());
	}

	public String getAppShortName() {
		return APP_NAME;
	}

	public String getAppLongName() {
		return APP_NAME + ", version " + APP_VERSION;
	}
}
