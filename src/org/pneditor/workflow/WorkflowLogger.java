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
package org.pneditor.workflow;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import org.pneditor.petrinet.Transition;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class WorkflowLogger {

    private static String defaultLogDirectory = System.getProperty("user.home") + "/logs";

    public static void log(String dirName, String workflowFilename, String caseId, Transition transition, String userId) throws IOException {
        File directory = new File(dirName);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File file = new File(dirName + "/" + workflowFilename + ".log");
        FileOutputStream fileOutputStream = new FileOutputStream(file, true);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
        PrintStream out = new PrintStream(bufferedOutputStream);
        out.println(caseId + "	" + new Date().getTime() + "	" + userId + "	" + transition.getFullLabel());
        bufferedOutputStream.close();
        fileOutputStream.close();
    }

    public static void log(String workflowFilename, String caseId, Transition transition, String userId) throws IOException {
        log(defaultLogDirectory, workflowFilename, caseId, transition, userId);
    }

}
