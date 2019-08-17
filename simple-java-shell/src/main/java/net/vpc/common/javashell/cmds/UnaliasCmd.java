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

import net.vpc.common.javashell.JShellCmdSyntaxError;
import net.vpc.common.javashell.AbstractJavaShellCommand;
import net.vpc.common.javashell.JShellCommandContext;

/**
 * @author vpc (taha.bensalah@gmail.com)
 * @lastmodified 26 oct. 2004
 * Time: 23:08:51
 */
public class UnaliasCmd extends AbstractJavaShellCommand {

    public UnaliasCmd() {
        super("unalias");
    }
    
    public void exec(String[] args, JShellCommandContext context) {
        int commandArgsCount = args.length;
        if (commandArgsCount == 1 && args[0].equals("-a")) {
            for (String k : context.getGlobalContext().aliases().getAll()) {
                context.getGlobalContext().aliases().set(k, null);
            }
        } else if (commandArgsCount > 0) {
            for (String a : args) {
                context.getGlobalContext().aliases().set(a, null);
            }
        } else {
            throw new JShellCmdSyntaxError(1,args, getName(), getHelp(), "Wrong arguments");
        }
    }

    public String getHelp() {
        return "unalias -a | (<name> ...)";
    }

    public String getHelpHeader() {
        return "remove alias named or clear all alias with -a option";
    }
}
