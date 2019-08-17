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

import java.io.DataOutputStream;
import java.io.ObjectOutput;
import java.io.OutputStream;
import java.io.IOException;

public class Output extends DataOutputStream implements ObjectOutput {
    public Output(OutputStream out) {
        super(out);
    }

    public void writeObject(Object obj) throws IOException {
        if (obj == null) {
            super.writeLong(SimpleStream.NULL);
        } else {
//                super.writeInt(SERIALIZABLE);
//                finalChanceProcessor.write(this,obj);
            Processor p = SimpleStream.getProcessorFor(obj.getClass());
            if (p != null) {
                writeLong(p.getId());
                p.write(this, obj);
            } else {
                throw new IOException("class " + obj.getClass() + " is not supported");
            }
        }
    }
}
