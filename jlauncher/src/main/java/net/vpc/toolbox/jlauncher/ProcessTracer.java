/**
 * ====================================================================
 *             Universal Java VM Launcher
 *
 * Universal Java VM Launcher is a new tool for running other Java Virtual
 * machines.
 *
 * Copyright (C) 2002-2008 Taha BEN SALAH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * ====================================================================
 */
package net.vpc.toolbox.jlauncher;

import java.io.*;

class ProcessTracer implements Runnable {
    BufferedReader in;
    String prompt;
    boolean stopped;
    private PrintStream out;

    ProcessTracer(String prompt, PrintStream out, InputStream in) throws IOException {
        this.in = new BufferedReader(new InputStreamReader(in));
        this.out = out;
        this.prompt = prompt;
        stopped = false;
    }

    public void run() {
        String read = null;
        while (!stopped) {
            try {
                read = in.readLine();
                if (read == null) break;
                out.print(prompt);
                out.println(read);
            } catch (Throwable e) {
                try {
                    e.printStackTrace();
                } catch (Exception e1) {
                }
                break;
            }
        }
    }
}
