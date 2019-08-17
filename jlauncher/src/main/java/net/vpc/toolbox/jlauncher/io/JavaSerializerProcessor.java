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

import java.io.IOException;
import net.vpc.toolbox.jlauncher.io.Input;

public class JavaSerializerProcessor extends Processor {
    public JavaSerializerProcessor() {
        super(Object.class, SimpleStream.SERIALIZABLE);
    }

    public void write(Output out, Object o) throws IOException {
        byte[] b = SerializationUtils.getSerializedFormOf(o);
        out.writeInt(b.length);
        out.write(b);
    }

    public Object read(Input in) throws IOException {
        int l = in.readInt();
        byte[] b = new byte[l];
//            in.readFully(b);
        in.readFully(b);
        try {
            return SerializationUtils.getObjectFromSerializedForm(b);
        } catch (ClassNotFoundException e) {
            throw new IOException(e.toString());
        }
    }
}
