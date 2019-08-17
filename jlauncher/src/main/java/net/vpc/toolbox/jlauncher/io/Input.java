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
package net.vpc.toolbox.jlauncher.io;

import java.io.DataInputStream;
import java.io.ObjectInput;
import java.io.InputStream;
import java.io.IOException;

public class Input extends DataInputStream implements ObjectInput {
    public Input(InputStream in) {
        super(in);
    }

    public Object readObject() throws IOException {
        long c = readLong();
        if (c == SimpleStream.NULL) {
            return null;
        } else {
            return SimpleStream.getProcessorFor(c).read(this);
//                return finalChanceProcessor.read(this);
        }
    }
}
