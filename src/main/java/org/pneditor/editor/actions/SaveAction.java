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
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import org.pneditor.editor.Root;
import org.pneditor.editor.filechooser.FileType;
import org.pneditor.editor.filechooser.FileTypeException;
import org.pneditor.util.GraphicsTools;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class SaveAction extends AbstractAction {

    private Root root;
    private List<FileType> fileTypes;

    public SaveAction(Root root, List<FileType> fileTypes) {
        this.root = root;
        this.fileTypes = fileTypes;
        String name = "Save";
        putValue(NAME, name);
        putValue(SMALL_ICON, GraphicsTools.getIcon("pneditor/Save16.gif"));
        putValue(SHORT_DESCRIPTION, name);
        putValue(MNEMONIC_KEY, KeyEvent.VK_S);
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl S"));
    }

    public void actionPerformed(ActionEvent e) {
        File file = root.getCurrentFile();
        if (file != null) {
            try {
                FileType fileType = FileType.getAcceptingFileType(file, fileTypes);
                fileType.save(root.getDocument(), file);
                root.setModified(false);
            } catch (FileTypeException ex) {
                JOptionPane.showMessageDialog(root.getParentFrame(), ex.getMessage());
            }
        } else {
            new SaveFileAsAction(root, fileTypes).actionPerformed(e);
        }

    }

}
