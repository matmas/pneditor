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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import org.pneditor.editor.canvas.Canvas;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class DrawingBoard extends JPanel {

    private JScrollBar verticalScrollBar = new JScrollBar(JScrollBar.VERTICAL, 0, 10000, 0, 10000);
    private JScrollBar horizontalScrollBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 10000, 0, 10000);

    public DrawingBoard(Canvas canvas) {
        this.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        constraints.weighty = 1;
        this.add(canvas, constraints);
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.weightx = 0;
        constraints.weighty = 0;
        this.add(verticalScrollBar, constraints);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weightx = 0;
        constraints.weighty = 0;
        this.add(horizontalScrollBar, constraints);

        verticalScrollBar.setUnitIncrement(30);
        horizontalScrollBar.setUnitIncrement(30);
    }

    public JScrollBar getVerticalScrollBar() {
        return verticalScrollBar;
    }

    public JScrollBar getHorizontalScrollBar() {
        return horizontalScrollBar;
    }
}
