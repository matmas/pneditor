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

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class Main {
	public static void main(String[] args) {
		new RootPflow();
//		try {
//			Document document = new DocumentImporter().readFromFileWithXslt(new File("/home/matmas/original.pnml"), new PnmlFileType().getLoadXslt());
//			root.setDocument(document);
//			new DocumentExporter(document, root.getDocument().petriNet.getInitialMarking()).writeToFileWithXslt(new File("/home/matmas/new.pnml"), new PnmlFileType().getSaveXslt());
//			root.setDocument(new Document());
//			document = new DocumentImporter().readFromFileWithXslt(new File("/home/matmas/new.pnml"), new PnmlFileType().getLoadXslt());
//			root.setDocument(document);
//			Thread.sleep(1000);
//			root.quitApplication();
//		} catch (InterruptedException ex) {
//			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//		} catch (JAXBException ex) {
//			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//		} catch (IOException ex) {
//			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//		} catch (TransformerException ex) {
//			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//		}
	}
}
