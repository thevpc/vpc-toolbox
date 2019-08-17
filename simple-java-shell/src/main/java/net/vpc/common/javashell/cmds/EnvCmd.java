/**
 * ====================================================================
 * Doovos (Distributed Object Oriented Operating System)
 * <p>
 * Doovos is a new Open Source Distributed Object Oriented Operating System
 * Design and implementation based on the Java Platform. Actually, it is a try
 * for designing a distributed operation system in top of existing
 * centralized/network OS. Designed OS will follow the object oriented
 * architecture for redefining all OS resources (memory,process,file
 * system,device,...etc.) in a highly distributed context. Doovos is also a
 * distributed Java virtual machine that implements JVM specification on top the
 * distributed resources context.
 * <p>
 * Doovos BIN is a standard implementation for Doovos boot sequence, shell and
 * common application tools. These applications are running onDoovos guest JVM
 * (distributed jvm).
 * <p>
 * Copyright (C) 2008-2010 Taha BEN SALAH
 * <p>
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * <p>
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * ====================================================================
 */
package net.vpc.common.javashell.cmds;

import net.vpc.common.javashell.AbstractJavaShellCommand;
import net.vpc.common.javashell.JShell;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import net.vpc.common.javashell.JShellCommandContext;

/**
 * @author vpc (taha.bensalah@gmail.com)
 * @lastmodified 26 oct. 2004 Time: 23:08:51
 */
public class EnvCmd extends AbstractJavaShellCommand {

    public EnvCmd() {
        super("env");
    }

    @Override
    public void exec(String[] args, JShellCommandContext context) {
//        JavaShell shell=context.getShell();
        int commandArgsCount = args.length;
        if (commandArgsCount == 0) {
            showEnvs(context);
        } else {
            for (int i = 0; i < commandArgsCount; i++) {
                String name = args[i];
                showEnv(name, context);
            }
        }
    }

    public static void showEnvs(JShellCommandContext context) {
        Properties envs = context.vars().getAll();
        String[] processEnvs = envs.keySet().toArray(new String[envs.size()]);
        Arrays.sort(processEnvs);
        for (String var : processEnvs) {
            if (!var.endsWith(".VALUES")) {
//                    System.out.print(System.identityHashCode(processEnv)+" ");
//                if (shell.isExported(var)) {
//                    System.out.print("[X] ");
//                } else {
//                    System.out.print("[ ] ");
//                }
                context.out().println(var + "=" + envs.getProperty(var) + "");
            }
        }
    }

    private static void showEnv(String name, JShellCommandContext context) {
        JShell shell = context.getShell();
        String s = context.vars().getAll().getProperty(name);
        context.out().println(s);
        String vals = context.vars().getAll().getProperty(name + ".VALUES");
        if (vals != null) {
            context.out().println("valid values are :");
            List<String> stringList = Arrays.asList(vals.split(":"));
            for (String s1 : stringList) {
                context.out().println(s1);
            }
        }

    }

    public String getHelp() {
        return "env env=val\n"
                + "or\n"
                + "set env=\n"
                + getHelpHeader();
    }

    public String getHelpHeader() {
        return "set/unset env vars";
    }
}
