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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * Contains XSLT related functions.
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class Xslt {

    /**
     * Transforms one XML file to another XML using a XSLT transformation
     *
     * @param fileToTransform XML source file to transform from
     * @param xslt XSLT file to transform with
     * @param output XML destination file to store the result
     * @return output file
     * @throws java.io.IOException
     * @throws javax.xml.transform.TransformerException
     */
    public static File transformXml(File fileToTransform, InputStream xslt, File output) throws IOException, TransformerException {
        Source xmlSource = new StreamSource(fileToTransform);
        Source xsltSource = new StreamSource(xslt); //or DOMSource or SAXSource
//		Source xmlSource = new SAXSource(new InputSource(fileToTransform.toString()));
//		Source xsltSource = new SAXSource(new InputSource(xslt)); //or DOMSource or SAXSource
        Result result = new StreamResult(output);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer(xsltSource);
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.transform(xmlSource, result);
        return output;
    }

}
