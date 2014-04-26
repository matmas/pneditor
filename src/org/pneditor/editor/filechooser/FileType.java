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

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;
import org.pneditor.petrinet.Document;
import org.pneditor.petrinet.PetriNet;
import org.pneditor.util.StringTools;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public abstract class FileType extends FileFilter {

    public static Set<FileType> getAllFileTypes() {
        Set<FileType> allFileTypes = new HashSet<FileType>();
        allFileTypes.add(new EpsFileType());
        allFileTypes.add(new PflowFileType());
        allFileTypes.add(new PflowxFileType());
        allFileTypes.add(new PngFileType());
        allFileTypes.add(new ViptoolPnmlFileType());
        return allFileTypes;
    }

    public abstract String getExtension();

    public abstract String getName();

    public abstract void save(Document document, File file) throws FileTypeException;

    public abstract Document load(File file) throws FileTypeException;

    public abstract Icon getIcon();

    public BufferedImage getPreview(File file) {
        try {
            Document document = load(file);
            PetriNet petriNet = document.petriNet;
            BufferedImage image = petriNet.getRootSubnet().getPreview(petriNet.getInitialMarking());
            return image;
        } catch (FileTypeException ex) {
        }
        return null;
    }

    public String getDescription() {
        return getName() + " (*." + getExtension() + ")";
    }

    public boolean accept(File file) {
        if (file.isDirectory()) { //Show also directories in the filters
            return true;
        }

        String extension = StringTools.getExtension(file);
        if (extension != null) {
            if (extension.equals(getExtension())) {
                return true;
            }
        }
        return false;
    }

    public static FileType getAcceptingFileType(File file, Collection<FileType> fileTypes) {
        for (FileType fileType : fileTypes) {
            if (fileType.accept(file)) {
                return fileType;
            }
        }
        return null;
    }
}
