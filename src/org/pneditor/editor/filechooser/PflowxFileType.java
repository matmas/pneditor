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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.Icon;
import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerException;
import org.pneditor.petrinet.Document;
import org.pneditor.petrinet.Marking;
import org.pneditor.petrinet.PetriNet;
import org.pneditor.petrinet.xml.DocumentExporter;
import org.pneditor.petrinet.xml.DocumentImporter;
import org.pneditor.util.GraphicsTools;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class PflowxFileType extends FileType {

    @Override
    public String getName() {
        return "PetriFlow subnets";
    }

    @Override
    public String getExtension() {
        return "pflowx";
    }

    @Override
    public Icon getIcon() {
        final Icon icon = GraphicsTools.getIcon("pneditor/filechooser/pflowx.gif");
        return icon;
    }

    @Override
    public void save(Document document, File file) throws FileTypeException {
        try {
            final InputStream xslt = getClass().getResourceAsStream("/xslt/save.xslt");
            PetriNet petriNet = document.petriNet;
            Marking initialMarking = petriNet.getInitialMarking();
            new DocumentExporter(document, initialMarking).writeToFileWithXslt(file, xslt);
        } catch (FileNotFoundException ex) {
            throw new FileTypeException(ex.getMessage());
        } catch (JAXBException ex) {
            if (!file.exists()) {
                throw new FileTypeException("File not found.");
            } else if (!file.canRead()) {
                throw new FileTypeException("File can not be read.");
            } else {
                throw new FileTypeException("Selected file is not compatible.");
            }
        } catch (IOException ex) {
            throw new FileTypeException(ex.getMessage());
        } catch (TransformerException ex) {
            throw new FileTypeException(ex.getMessage());
        }
    }

    @Override
    public Document load(File file) throws FileTypeException {
        try {
            final InputStream xslt = null;
            Document document = new DocumentImporter().readFromFileWithXslt(file, xslt);
            document.petriNet.getRootSubnet().setViewTranslationToCenterRecursively();
            return document;
        } catch (JAXBException ex) {
            if (!file.exists()) {
                throw new FileTypeException("File not found.");
            } else if (!file.canRead()) {
                throw new FileTypeException("File can not be read.");
            } else {
                throw new FileTypeException("Selected file is not compatible.");
            }
        } catch (IOException ex) {
            throw new FileTypeException(ex.getMessage());
        } catch (TransformerException ex) {
            throw new FileTypeException(ex.getMessage());
        }
    }

}
