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

import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;
import org.pneditor.util.StringTools;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class PropertiesDialog<E> extends JDialog implements ActionListener {

    private E e;
    private JButton saveButton, cancelButton;
    private Map<Field, JComponent> controls = new HashMap<Field, JComponent>();

    public PropertiesDialog(Frame parent, E e) {
        super(parent);
        setModal(true);
        this.e = e;
        Class clazz = e.getClass();
        setTitle(clazz.getSimpleName() + " properties");
        try {
            for (Field field : clazz.getFields()) {
                String name = StringTools.deCamelCase(field.getName());
                if (field.getType() == boolean.class) {
                    JCheckBox checkBox = new JCheckBox(name, field.getBoolean(e));
                    controls.put(field, checkBox);
                    add(new JLabel());
                    add(checkBox);
                } else if (field.getType() == String.class) {
                    JTextField textField = new JTextField((String) field.get(e));
                    controls.put(field, textField);
                    JLabel label = new JLabel(name + ": ");
                    label.setHorizontalAlignment(JLabel.RIGHT);
                    add(label);
                    add(textField);
                }
            }
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(PropertiesDialog.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            Logger.getLogger(PropertiesDialog.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
        setLayout(new GridLayout(controls.size() + 1, 2));

        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
        add(saveButton);
        add(cancelButton);

        saveButton.addActionListener(this);
        cancelButton.addActionListener(this);

        pack();
        setLocationByPlatform(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == saveButton) {
            try {
                for (Field field : controls.keySet()) {
                    if (field.getType() == boolean.class) {
                        field.setBoolean(this.e, ((JCheckBox) controls.get(field)).isSelected());
                    } else if (field.getType() == String.class) {
                        field.set(this.e, ((JTextField) controls.get(field)).getText());
                    }
                }
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(PropertiesDialog.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
            } catch (IllegalAccessException ex) {
                Logger.getLogger(PropertiesDialog.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
            }
            this.dispose();
        } else if (e.getSource() == cancelButton) {
            this.dispose();
        }
    }
}
