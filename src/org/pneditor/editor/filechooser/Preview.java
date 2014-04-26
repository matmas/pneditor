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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.SystemColor;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class Preview extends JPanel implements PropertyChangeListener {

    private ImageIcon thumbnail = null;
    private File file = null;
    public final int preferredWidth = 200;
    public final int preferredHeight = 200;

    public Preview(JFileChooser fileChooser) {
        super();
        setPreferredSize(new Dimension(preferredWidth, preferredHeight));
        fileChooser.addPropertyChangeListener(this);
    }

    public void propertyChange(PropertyChangeEvent e) {
        String propertyName = e.getPropertyName();
        boolean update = false;

        //If the directory changed, don't show an image.
        if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(propertyName)) {
            file = null;
            update = true;

            //If a file became selected, find out which one.
        } else if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(propertyName)) {
            file = (File) e.getNewValue();
            update = true;
        }

        //Update the preview accordingly.
        if (update) {
            thumbnail = null;
            if (isShowing()) {
                loadImage();
                repaint();
            }
        }
    }

    private void loadImage() {
        if (file == null) {
            thumbnail = null;
            return;
        }
        FileType fileType = FileType.getAcceptingFileType(file, FileType.getAllFileTypes());
        if (fileType == null) {
            thumbnail = null;
            return;
        }
        BufferedImage image = fileType.getPreview(file);
        if (image == null) {
            thumbnail = null;
            return;
        }
        if (image.getWidth() > image.getHeight()) {
            thumbnail = new ImageIcon(image.getScaledInstance(preferredWidth, -1, Image.SCALE_SMOOTH), "");
        } else {
            thumbnail = new ImageIcon(image.getScaledInstance(-1, preferredHeight, Image.SCALE_SMOOTH), "");
        }

    }

    @Override
    protected void paintComponent(Graphics g) {
        if (thumbnail == null) {
            loadImage();
        }
        if (thumbnail != null) {
            setBackground(Color.white);
            super.paintComponent(g);
            int x = getWidth() / 2 - thumbnail.getIconWidth() / 2;
            int y = getHeight() / 2 - thumbnail.getIconHeight() / 2;

            if (y < 0) {
                y = 0;
            }
            if (x < 0) {
                x = 0;
            }
            thumbnail.paintIcon(this, g, x, y);
        } else {
            setBackground(SystemColor.control);
            super.paintComponent(g);
        }
    }
}
