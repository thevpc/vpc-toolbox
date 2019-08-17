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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * User: taha
 * Date: 17 fevr. 2004
 * Time: 19:52:49
 */
class NullOutput extends OutputStream {
    public static final NullOutput NULL = new NullOutput();
    public static final PrintStream NULL_PRINT_STREAM = new PrintStream(NULL);

    private NullOutput() {
    }

    public void write(int b) throws IOException {
    }

    public void write(byte[] b) throws IOException {
    }

    public void write(byte[] b, int off, int len) throws IOException {
    }
}
