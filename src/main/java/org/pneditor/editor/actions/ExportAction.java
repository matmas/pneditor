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
package org.pneditor.editor.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.pneditor.editor.Root;
import org.pneditor.editor.filechooser.FileChooserDialog;
import org.pneditor.editor.filechooser.FileType;
import org.pneditor.editor.filechooser.FileTypeException;
import org.pneditor.util.GraphicsTools;
import org.pneditor.util.StringTools;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class ExportAction extends AbstractAction {

    private Root root;
    private List<FileType> fileTypes;

    public ExportAction(Root root, List<FileType> fileTypes) {
        this.root = root;
        this.fileTypes = fileTypes;
        String name = "Export...";
        putValue(NAME, name);
        putValue(SMALL_ICON, GraphicsTools.getIcon("pneditor/export.gif"));
        putValue(SHORT_DESCRIPTION, name);
    }

    public void actionPerformed(ActionEvent e) {
        FileChooserDialog chooser = new FileChooserDialog();

        if (root.getCurrentFile() != null) {
            chooser.setSelectedFile(new File(StringTools.getExtensionCutOut(root.getCurrentFile().getName())));
        }

        for (FileType fileType : fileTypes) {
            chooser.addChoosableFileFilter(fileType);
        }
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setCurrentDirectory(root.getCurrentDirectory());

        if (chooser.showDialog(root.getParentFrame(), "Export") == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            FileType chosenFileType = (FileType) chooser.getFileFilter();

            if (!file.exists() || JOptionPane.showOptionDialog(
                    root.getParentFrame(),
                    "Selected file already exists. Overwrite?",
                    "Export to " + file.getName(),
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    new String[]{"Overwrite", "Cancel"},
                    "Cancel") == JOptionPane.YES_OPTION) {
                try {
                    chosenFileType.save(root.getDocument(), file);
                } catch (FileTypeException ex) {
                    JOptionPane.showMessageDialog(root.getParentFrame(), ex.getMessage());
                }
            }
            root.setCurrentDirectory(chooser.getCurrentDirectory());
        }
    }
}
