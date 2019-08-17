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
import java.util.Hashtable;
import net.vpc.toolbox.jlauncher.io.AlreadyRegistredProcessorException;

public class SimpleStream {
    public static final long SERIALIZABLE = -9;
    public static final long ARRAY = -2;
//    public static final long VOID=-1;
    public static final long NULL = 0;

//    public static final long INT=1;
//    public static final long LONG=2;
//    public static final long SHORT=3;
//    public static final long DOUBLE=4;
//    public static final long BYTE=5;
//    public static final long CHAR=6;
//    public static final long FLOAT=7;
//    public static final long BOOLEAN=8;
//
//    public static final long BIG_INT=11;
//    public static final long BIG_DECIMAL=12;
//
//    public static final long UTF=31;
//    public static final long UTILDATE=32;
//    public static final long HASHMAP=33;
//    public static final long HASHTABLE=34;
//    public static final long VECTOR=35;
//
//    public static final long SQLDATE=133;
//    public static final long SQLTIME=134;
//    public static final long SQLTIMESTAMP=135;


    private static Hashtable processors = new Hashtable();
    private static Hashtable processorsById = new Hashtable();

    private static final JavaSerializerProcessor finalChanceProcessor = new JavaSerializerProcessor();
    private static final ArrayProcessor arrayProcessor = new ArrayProcessor();

    private SimpleStream() {

    }


    public static void register(Processor p) {
        if (p == null) {
            throw new NullPointerException("Null Stream Prcessor");

        } else if (processors.containsKey(p.getProcessedClass())) {
            throw new AlreadyRegistredProcessorException(p.getProcessedClass().getName() + " as " + p.getId());

        } else if (processorsById.containsKey(new Long(p.getId()))) {
            throw new AlreadyRegistredProcessorException(p.getId() + " as " + p.getProcessedClass().getName());
        }
        processors.put(p.getProcessedClass(), p);
        processorsById.put(new Long(p.getId()), p);
    }

    static {
        processorsById.put(new Long(ARRAY), arrayProcessor);
        processorsById.put(new Long(SERIALIZABLE), finalChanceProcessor);
    }

    public static Processor getProcessorFor(long id) throws IOException {
        Processor p = (Processor) processorsById.get(new Long(id));
        if (p != null) {
            return p;
        } else {
            throw new IOException("class with code " + id + " is not supported");
        }
    }

    public static Processor getProcessorFor(Class c) {
        if (c.isArray() && !c.getComponentType().isPrimitive()) {
            return arrayProcessor;
        } else {
            Processor p = (Processor) processors.get(c);
            if (p != null) {
                return p;
            }
            return finalChanceProcessor;
        }
    }
}
