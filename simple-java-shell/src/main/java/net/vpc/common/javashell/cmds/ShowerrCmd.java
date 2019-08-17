/**
 * ====================================================================
 *             Doovos (Distributed Object Oriented Operating System)
 *
 * Doovos is a new Open Source Distributed Object Oriented Operating System
 * Design and implementation based on the Java Platform.
 * Actually, it is a try for designing a distributed operation system in
 * top of existing centralized/network OS.
 * Designed OS will follow the object oriented architecture for redefining
 * all OS resources (memory,process,file system,device,...etc.) in a highly
 * distributed context.
 * Doovos is also a distributed Java virtual machine that implements JVM
 * specification on top the distributed resources context.
 *
 * Doovos BIN is a standard implementation for Doovos boot sequence, shell and
 * common application tools. These applications are running onDoovos guest JVM
 * (distributed jvm).
 *
 * Copyright (C) 2008-2010 Taha BEN SALAH
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
package net.vpc.common.javashell.cmds;

import net.vpc.common.javashell.AbstractJavaShellCommand;
import net.vpc.common.javashell.JShellCommandContext;
import net.vpc.common.javashell.JShellResult;

/**
 * @author vpc (taha.bensalah@gmail.com)
 * @lastmodified 20 nov. 2004 Time: 22:53:35
 */
public class ShowerrCmd extends AbstractJavaShellCommand {

    public ShowerrCmd() {
        super("showerr");
    }

    public void exec(String[] args, JShellCommandContext context) {
        JShellResult r = context.getGlobalContext().getLastResult();
        if (r.getMessage() == null) {
            context.out().println("Last command ended successfully with no errors.");
        } else {
            context.out().println("Last command ended abnormally with the following error :");
            context.out().println(r.getMessage());
            if (r.getThrowable() != null) {
                r.getThrowable().printStackTrace(context.err());
            }
        }
    }

    @Override
    public String getHelp() {
        return getName();
    }

    @Override
    public String getHelpHeader() {
        return "show the error generated by the latest command if any";
    }
}
