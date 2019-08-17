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

import java.io.*;

public class SerializationUtils {
    public static Object getObjectFromSerializedForm(byte[] bytes) throws ClassNotFoundException {
        ObjectInputStream b = null;
        try {
            b = new ObjectInputStream(new ByteArrayInputStream(bytes));
            return b.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e.toString());
        } finally {
            try {
                if (b != null) {
                    b.close();
                }
            } catch (IOException e) {
            }
        }
    }

    public static byte[] getSerializedFormOf(Object object) {
        ObjectOutputStream b = null;
        try {
            ByteArrayOutputStream bb = new ByteArrayOutputStream();
            b = new ObjectOutputStream(bb);
            b.writeObject(object);
            return bb.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e.toString());
        } finally {
            try {
                if (b != null) {
                    b.close();
                }
            } catch (IOException e) {
            }
        }
    }

}
