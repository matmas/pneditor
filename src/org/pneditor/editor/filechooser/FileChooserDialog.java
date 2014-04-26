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

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class FileChooserDialog extends JFileChooser {

    private Sidebar sidebar = new Sidebar(this);

    public FileChooserDialog() {
        setFileView(new FileIconView());
        setAccessory(sidebar);
    }

    @Override
    public File getSelectedFile() {
        File file = super.getSelectedFile();
        if (file == null) {
            return null;
        } else if (file.exists() && getFileFilter().getDescription().equals("All Files")) {
            return file;
        } else if (getFileFilter().accept(file)) {
            return file;
        } else {
            return new File(file.getAbsolutePath() + "." + ((FileType) getFileFilter()).getExtension());
        }
    }

    @Override
    public void addChoosableFileFilter(FileFilter filter) {
        super.addChoosableFileFilter(filter);
        if (getChoosableFileFilters().length > 1) { // first filter is always "All files"
            setFileFilter(getChoosableFileFilters()[1]);
        }
    }

    public Sidebar getSidebar() {
        return sidebar;
    }
}
