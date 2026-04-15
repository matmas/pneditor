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
package org.pneditor.util;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.pneditor.util.GraphicsTools;
import org.pneditor.util.ListModel;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class ListEditor<E> extends JPanel implements ActionListener, ListSelectionListener {

    private JScrollPane scrollPane;
    private JList list;
    private JPanel topPanel, buttonBar;
    public JButton addButton, deleteButton, editButton;
    private JLabel title;
    private ListModel<E> model;
    private Frame parent;

    public ListEditor(String title, ListModel<E> model, Frame parent) {
        this.model = model;
        this.parent = parent;
        list = new JList(model);

        setLayout(new BorderLayout());

        scrollPane = new JScrollPane(list);

        buttonBar = new JPanel(new GridLayout(1, 3));

        this.title = new JLabel(title);

        addButton = new SmallButton();
        editButton = new SmallButton();
        deleteButton = new SmallButton();

        addButton.setIcon(GraphicsTools.getIcon("pneditor/Add16.gif"));
        editButton.setIcon(GraphicsTools.getIcon("pneditor/Preferences16.gif"));
        deleteButton.setIcon(GraphicsTools.getIcon("pneditor/Remove16.gif"));

        addButton.setToolTipText("Add");
        editButton.setToolTipText("Edit");
        deleteButton.setToolTipText("Delete");

        buttonBar.add(addButton);
        buttonBar.add(editButton);
        buttonBar.add(deleteButton);

        topPanel = new JPanel(new BorderLayout());
        topPanel.add(this.title, BorderLayout.NORTH);
        topPanel.add(buttonBar, BorderLayout.CENTER);

        this.add(topPanel, BorderLayout.NORTH);
        this.add(scrollPane, BorderLayout.CENTER);

        addButton.addActionListener(this);
        editButton.addActionListener(this);
        deleteButton.addActionListener(this);
        list.addListSelectionListener(this);

        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }

    public void addListSelectionListener(ListSelectionListener listSelectionListener) {
        list.addListSelectionListener(listSelectionListener);
    }

    public void setModel(ListModel<E> listModel) {
        model = listModel;
        list.setModel(model);
    }

    class SmallButton extends JButton {

        public SmallButton() {
            setMargin(new Insets(0, 0, 0, 0));
        }

    }

    public void addNew() {
        model.addNew();
        list.setSelectedIndex(model.getSize() - 1);
    }

    public void deleteCurrent() {
        int firstSelectedIndex = list.getSelectedIndex();
        model.delete(list.getSelectedIndices());
        int lastIndex = list.getModel().getSize() - 1;
        list.setSelectedIndex(Math.min(firstSelectedIndex, lastIndex));
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            addNew();
        } else if (e.getSource() == deleteButton) {
            deleteCurrent();
        } else if (e.getSource() == editButton) {
            openPropertiesDialog(parent, list.getSelectedIndex());
            refreshSelected();
        }
    }

    private void openPropertiesDialog(Frame parent, int selectedIndex) {
        JDialog dialog = new PropertiesDialog<E>(parent, model.getElementAt(selectedIndex));
        dialog.setVisible(true);
    }

    public void refreshSelected() {
        list.setSelectedIndices(list.getSelectedIndices());
    }

    public void valueChanged(ListSelectionEvent e) {
        boolean isEditable = list.getSelectedIndices().length == 1;
        editButton.setEnabled(isEditable);

        boolean isDeletable = list.getSelectedIndices().length > 0;
        deleteButton.setEnabled(isDeletable);
    }

    public List<E> getSelectedElements() {
        List<E> selectedElements = new LinkedList<E>();
        for (int i : list.getSelectedIndices()) {
            selectedElements.add(model.getElementAt(i));
        }
        return selectedElements;
    }

    @SuppressWarnings("unchecked")
    public E getSelectedElement() {
        return (E) list.getSelectedValue();
    }
}
