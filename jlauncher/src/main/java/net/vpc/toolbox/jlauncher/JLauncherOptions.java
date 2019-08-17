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
import java.util.Iterator;
import java.util.Vector;

/**
 * @author Taha Ben Salah (taha.bensalah@gmail.com)
 * @creationtime 7 sept. 2007 01:26:58
 */
public class JLauncherOptions {

    private String workdir;
    private String classpath;
    private String mainClass;
    private String stdout;
    private String stderr;
    private String javaHome;
    private String javaVersion;
    private String echo;
    private String type;
    private Vector macros = new Vector();
    private Vector jvmArgs = new Vector();
    private Vector appArgs = new Vector();
    private Vector argsfiles = new Vector();
    private File loadedFile;

    public JLauncherOptions(File file) throws IOException {
        load(file);
    }

    public void store() throws IOException {
        store(loadedFile);
    }

    public void setFile(File file) {
        loadedFile = file;
    }

    public void store(File file) throws IOException {
        PrintStream p = new PrintStream(new FileOutputStream(file));
        if (echo != null && echo.trim().length() > 0) {
            p.println("jlauncher.echo " + echo);
        }
        if (type != null && type.trim().length() > 0) {
            p.println("jlauncher.type " + type);
        }
        if (javaHome != null && javaHome.trim().length() > 0) {
            p.println("jlauncher.java.home " + javaHome);
        }
        if (workdir != null && workdir.trim().length() > 0) {
            p.println("jlauncher.workdir " + workdir);
        }
        if (classpath != null && classpath.trim().length() > 0) {
            p.println("jlauncher.classpath " + classpath);
        }
        if (javaVersion != null && javaVersion.trim().length() > 0) {
            p.println("jlauncher.java.min.version " + javaVersion);
        }
        for (Iterator i = macros.iterator(); i.hasNext();) {
            String m = (String) i.next();
            if (m != null && m.trim().length() > 0) {
                p.println("jlauncher.macro " + m);
            }
        }
        for (Iterator i = jvmArgs.iterator(); i.hasNext();) {
            String m = (String) i.next();
            if (m != null && m.trim().length() > 0) {
                p.println("jvm.arg " + m);
            }
        }
        if (mainClass != null && mainClass.trim().length() > 0) {
            p.println("jlauncher.main.class " + mainClass);
        }
        for (Iterator i = appArgs.iterator(); i.hasNext();) {
            String m = (String) i.next();
            if (m != null && m.trim().length() > 0) {
                p.println("app.arg " + m);
            }
        }
        p.close();
    }

    public void load(String[] args) {
        jvmArgs.clear();
        appArgs.clear();
        macros.clear();
        for (int i = 0; i < args.length; i++) {
            String line = args[i];
            line = line.trim();
            if (line.length() > 0 && !line.startsWith("#")) {
                if (line.startsWith("jlauncher.")) {
                    line = line.substring("jlauncher.".length());
                    String[] cmd = parseCommand(line);
                    String k = cmd[0];
                    String v = cmd[1];
                    if (k.equals("macro")) {
                        macros.add(v);
                    } else if (k.equals("echo")) {
                        echo = v;
                    } else if (k.equals("workdir")) {
                        workdir = v;
                    } else if (k.equals("type")) {
                        type = v;
                    } else if (k.equals("out") || k.equals("stdout")) {
                        stdout = v;
                    } else if (k.equals("err") || k.equals("stderr")) {
                        stderr = v;
                    } else if (k.equals("type")) {
                        type = v;
                    } else if (k.equals("classpath") || k.equals("classlib")) {
                        this.classpath = v;
                    } else if (k.equals("main.class") || k.equals("app.class") || k.equals("main") || k.equals("class")) {
                        this.mainClass = v;
                    } else if (k.equals("java.home")) {
                        this.javaHome = v;
                    } else if (k.equals("java.version.min")) {
                        this.javaVersion = v;
                    } else {
                        System.err.println("JLauncher : ignored "+line);
                        //throw new IllegalArgumentException();
                    }
                } else if (line.startsWith("jvm.arg")) {
                    String[] cmd = parseCommand(line);
                    jvmArgs.add(cmd[1]);
                } else if (line.startsWith("app.arg")) {
                    String[] cmd = parseCommand(line);
                    appArgs.add(cmd[1]);
                } else {
                    throw new IllegalArgumentException(line);
                }
            }
        }
    }

    public void load(File file) throws IOException {
        loadedFile = file;
        Vector ar = new Vector();
        BufferedReader stream = null;
        try {
            stream = new BufferedReader(new FileReader(file));
            String line;
            while ((line = stream.readLine()) != null) {
                line = line.trim();
                if (line.length() > 0 && !line.startsWith("#")) {
                    ar.add(line);
                }
            }
            stream.close();
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        load((String[]) ar.toArray(new String[ar.size()]));
    }

    public String getWorkdir() {
        return workdir;
    }

    public void setWorkdir(String workdir) {
        this.workdir = workdir;
    }

    public String getClasspath() {
        return classpath;
    }

    public void setClasspath(String classpath) {
        this.classpath = classpath;
    }

    public String getMainClass() {
        return mainClass;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public String getJavaHome() {
        return javaHome;
    }

    public void setJavaHome(String javaHome) {
        this.javaHome = javaHome;
    }

    public String getJavaVersion() {
        return javaVersion;
    }

    public void setJavaVersion(String javaVersion) {
        this.javaVersion = javaVersion;
    }

    public String getEcho() {
        return echo;
    }

    public void setEcho(String echo) {
        this.echo = echo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Vector getJvmArgs() {
        return jvmArgs;
    }

    public Vector getAppArgs() {
        return appArgs;
    }

    public Vector getMacros() {
        return macros;
    }

    public Vector getArgsfiles() {
        return argsfiles;
    }

    public String getSunVMOptionXms() {
        String prefix = "-Xms";
        for (int i = 0; i < jvmArgs.size(); i++) {
            String v = (String) jvmArgs.get(i);
            if (v.startsWith(prefix)) {
                return v.substring(prefix.length());
            }
        }
        return null;
    }

    public String getSunVMOptionXmx() {
        String prefix = "-Xmx";
        for (int i = 0; i < jvmArgs.size(); i++) {
            String v = (String) jvmArgs.get(i);
            if (v.startsWith(prefix)) {
                return v.substring(prefix.length());
            }
        }
        return null;
    }

    public void setSunVMOptionXms(String s) {
        if (s.trim().length() == 0) {
            for (Iterator i = jvmArgs.iterator(); i.hasNext();) {
                String ss = (String) i.next();
                if (ss.startsWith("-Xms")) {
                    i.remove();
                    break;
                }
            }

        } else if (isValidXmem(s)) {
            String opt = "-Xms" + s;
            boolean found = false;
            for (int i = 0; i < jvmArgs.size(); i++) {
                String v = (String) jvmArgs.get(i);
                if (v.startsWith("-Xms")) {
                    jvmArgs.set(i, opt);
                    found = true;
                    break;
                }
            }
            if (!found) {
                jvmArgs.add(opt);
            }
        }
    }

    private boolean isValidXmem(String s) {
        if (!s.toLowerCase().endsWith("m") && !s.toLowerCase().endsWith("k")) {
            try {
                int i = Integer.parseInt(s);
                return i >= 1000;
            } catch (NumberFormatException e) {
                return false;
            }
        } else {
            try {
                int i = Integer.parseInt(s.substring(0, s.length() - 1));
                return i >= 1;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }

    public void setSunVMOptionXmx(String s) {
        if (s.trim().length() == 0) {
            for (Iterator i = jvmArgs.iterator(); i.hasNext();) {
                String ss = (String) i.next();
                if (ss.startsWith("-Xmx")) {
                    i.remove();
                    break;
                }
            }
        } else if (isValidXmem(s)) {
            String opt = "-Xmx" + s;
            boolean found = false;
            for (int i = 0; i < jvmArgs.size(); i++) {
                String v = (String) jvmArgs.get(i);
                if (v.startsWith("-Xmx")) {
                    jvmArgs.set(i, opt);
                    found = true;
                    break;
                }
            }
            if (!found) {
                jvmArgs.add(opt);
            }
        }
    }

    public static String[] parseCommand(String arg) {
        String cmdKey;
        String cmdValue;
        char[] cmdPosChars = new char[]{':', '=', ' ', '\t'};
        int cmdPos = -1;
        for (int i = 0; i < cmdPosChars.length; i++) {
            int p = arg.indexOf(cmdPosChars[i]);
            if ((p > 0 && (cmdPos < 0 || p < cmdPos))) {
                cmdPos = p;
            }
        }
        if (cmdPos < 0) {
            cmdKey = arg;
            cmdValue = "";
        } else {
            cmdKey = arg.substring(0, cmdPos);
            cmdValue = arg.substring(cmdPos + 1);
        }
        return new String[]{cmdKey.trim(), cmdValue.trim()};
    }
}
