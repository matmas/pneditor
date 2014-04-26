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
import javax.swing.Icon;
import javax.swing.filechooser.FileView;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class FileIconView extends FileView {

    @Override
    public Icon getIcon(File file) {
        if (file.isDirectory()) {
            return super.getIcon(file);
        }
        FileType fileType = FileType.getAcceptingFileType(file, FileType.getAllFileTypes());
        if (fileType != null) {
            return fileType.getIcon();
        }
        return super.getIcon(file);
//		if (file.isDirectory()) {
//			return super.getIcon(file);
//		}
//		if (cache.containsKey(file)) {
//			return cache.get(file);
//		}
//		try {
//			for (Element element : new DocumentImporter().readFromFile(file).petriNet.getRootSubnet().getElements()) {
//				if (element instanceof ReferencePlace) {
//					cache.put(file, pflowxIcon);
//					return pflowxIcon;
//				}
//			}
//			cache.put(file, pflowIcon);
//			return pflowIcon;
//		} catch (JAXBException ex) {
//		}
//		cache.put(file, super.getIcon(file));
//		return super.getIcon(file);
    }

}
