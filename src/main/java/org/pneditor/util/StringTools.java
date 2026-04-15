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

/**
 * This class contains some simple string management functions.
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class StringTools {

    /**
     * This method converts strings like "deCamelCase" to "De camel case"
     *
     * @param string string to be converted
     */
    public static String deCamelCase(String string) {
        StringBuffer sb = new StringBuffer(string);
        sb.setCharAt(0, string.substring(0, 1).toUpperCase().charAt(0)); //first letter -> uppercase
        for (int i = 1; i < sb.length(); i++) {
            String currentChar = sb.substring(i, i + 1); //for each char in string:

            if (currentChar.equals(currentChar.toUpperCase())) { //if current char is uppercase
                sb.insert(i++, " "); //insert space
                sb.setCharAt(i, currentChar.toLowerCase().charAt(0)); //current char -> lowercase
            }
        }
        return sb.toString();
    }

    /**
     * Returns extension of a file i.e. from File("example.html.txt") returns
     * "txt"
     *
     * @param file file to get the extension from
     * @return extension of the file
     */
    public static String getExtension(File file) {
        return getExtension(file.getName());
    }

    private static String getExtension(String filename) {
        String ext = null;
        String s = filename;
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }

        if (ext != null) {
            return ext;
        } else {
            return "";
        }
    }

    public static String getExtensionCutOut(String filename) {
        String extension = getExtension(filename);
        String result = filename.substring(0, filename.length() - 1 - extension.length());
        return result;
    }
}
