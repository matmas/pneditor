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
package org.pneditor.editor.filechooser;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.pneditor.util.GraphicsTools;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class Sidebar extends JPanel implements ActionListener, PropertyChangeListener {

    private JFileChooser fileChooser;
    private JButton delete = new JButton("Delete file", GraphicsTools.getIcon("pneditor/Delete16.gif"));

    public Sidebar(JFileChooser fileChooser) {
        super();
        this.fileChooser = fileChooser;
        Preview preview = new Preview(fileChooser);
        setLayout(new BorderLayout());
        add(preview, BorderLayout.CENTER);
        add(delete, BorderLayout.NORTH);
        delete.addActionListener(this);
        fileChooser.addPropertyChangeListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == delete) {
            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile != null && JOptionPane.showOptionDialog(
                    fileChooser,
                    "Delete " + selectedFile.getName() + "?\nThis action is irreversible!",
                    "Delete",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    new String[]{"Delete permanently", "Cancel"},
                    "Cancel") == JOptionPane.YES_OPTION) {
                selectedFile.delete();
                fileChooser.setSelectedFile(new File(""));
                fileChooser.rescanCurrentDirectory();
            }
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        delete.setEnabled(fileChooser.getSelectedFile() != null);
    }

}
