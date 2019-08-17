/**
 * ====================================================================
 * Universal Java VM Launcher
 *
 * Universal Java VM Launcher is a new tool for running other Java Virtual
 * machines.
 *
 * Copyright (C) 2002-2008 Taha BEN SALAH
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * ====================================================================
 */
package net.vpc.toolbox.jlauncher;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
//import net.vpc.common.utils.LogUtils;
//import org.apache.maven.cli.MavenCli;

public class JLauncher {

    public static final String APP_VERSION = "1.4.0";
    public static final String APP_DATE = "2012-07-29";
    public static final String APP_FULL_NAME = "JLauncher v" + APP_VERSION;
    public static final String PREFIX = "jlauncher.";
    ArrayList loadedArgFiles = new ArrayList();

//    public static void main(String[] args) {
//        MavenCli c = new MavenCli();
////        CliRequest cr=new CliRequest();
//        LogUtils.configure(Level.ALL, new String[]{""});
//        System.setProperty(MavenCli.MULTIMODULE_PROJECT_DIRECTORY, "org");
//        c.doMain(new String[]{
//                    "-DgroupId=rhino",
//                    "-DartifactId=js",
//                    "-Dversion=1.7R2",
//                    "dependency:get"
////            "-D" + MavenCli.MULTIMODULE_PROJECT_DIRECTORY + "=.",
////            "clean",
////            "compile"
//        }, ".", System.out, System.err);
//    }

    public static void main2(String[] args) {
        JLauncher launcher = new JLauncher();
        try {
            launcher.launch(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean showConfirm(String title, String message) {
        return JOptionPane.OK_OPTION != JOptionPane.showConfirmDialog(null,
                message,
                JLauncher.APP_FULL_NAME + " : " + title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE);
    }

    public void showError(String title, String message) {
        int i = JOptionPane.showOptionDialog(
                null,
                message,
                APP_FULL_NAME + " : " + title,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.ERROR_MESSAGE,
                null,
                new Object[]{"Ok", "Help"}, "Ok"
        );
        if (i == 1) {
            helpWindow();
        }
    }

    public static File getAppFile(String path) {
        File f = new File(path);
        if (f.isAbsolute()) {
            return f;
        } else {
            if (getAppFolder() == null) {
                return f;
            }
            return new File(getAppFolder(), path);
        }
    }

    public static File getAppFolder() {
        String[] paths = LaunchUtils.getSystemClassPathForClass(LaunchUtils.class.getName());
        String best = null;
        for (int i = 0; i < paths.length; i++) {
            if (new File(paths[i]).exists()) {
                if (best == null) {
                    best = paths[i];
                } else if (new File(best).isDirectory() && new File(paths[i]).isFile()) {
                    best = paths[i];
                } else if (new File(paths[i]).getName().equalsIgnoreCase("run.jar")) {
                    best = paths[i];
                }
            }
        }
        if (best != null) {
            return new File(best).getParentFile();
        }
        return null;
    }

    private String[] loadArgsFromFile(File file) {
        if (loadedArgFiles.contains(file)) {
            return new String[0];
        }
        loadedArgFiles.add(file);
        try {
            System.out.println("loadArgsFromFile " + file.getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader stream;
        ArrayList args = new ArrayList();
        try {
            stream = new BufferedReader(new FileReader(file));

            String line;
            while ((line = stream.readLine()) != null) {
                line = line.trim();
                if (line.length() > 0 && !line.startsWith("#")) {
                    args.add(line);
                }
            }
            return (String[]) args.toArray(new String[args.size()]);
        } catch (FileNotFoundException e) {
            showError("Missing Jex File",
                    "<HTML><Font color=red>Missing Jex File " + file.getAbsolutePath() + "</Font><BR>" + e.toString() + "</HTML>");
            System.exit(-3);
        } catch (IOException e) {
            showError("Missing Jex File",
                    "<HTML><Font color=red>Missing Jex File " + file.getAbsolutePath() + " </Font><BR>" + e.toString() + "</HTML>");
            System.exit(-3);
        }
        return new String[0];
    }

    //    public static void launch(String[] args,String className,String[] libFolders,String requiredVersion) {
    public String[] prepareArgs(String[] args, File workDir, boolean mayLoadDefault) {
        ArrayList v = new ArrayList(args.length);
        boolean shouldLoadDefault = true;
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith(PREFIX)) {
                String[] cmd = JLauncherOptions.parseCommand(args[i].substring(PREFIX.length()));
                if ("argsfile".equals(cmd[0])) {
                    shouldLoadDefault = false;
                    if (!"off".equalsIgnoreCase(cmd[1])) {
                        if ("on".equalsIgnoreCase(cmd[1])) {
                            cmd[1] = "run.jex";
                        }
                        String[] extra = loadArgsFromFile(getAppFile(cmd[1]));
                        extra = prepareArgs(extra, getAppFile(cmd[1]).getParentFile(), false);
                        for (int j = 0; j < extra.length; j++) {
                            v.add(extra[j]);
                        }
                    }
                } else {
                    v.add(args[i]);
                }
            } else {
                v.add(args[i]);
            }
        }
        File defaultArgs = new File(workDir, "run.jex");
//        try {
//            JOptionPane.showMessageDialog(null,"do load "+defaultArgs.getCanonicalPath()+" ?"+mayLoadDefault +"&&"+ shouldLoadDefault +"&&"+ defaultArgs.exists());
//        } catch (IOException e) {
//        }
        if (mayLoadDefault && shouldLoadDefault && defaultArgs.exists()) {
            String[] extra = loadArgsFromFile(defaultArgs);
            extra = prepareArgs(extra, defaultArgs.getParentFile(), false);
            for (int j = 0; j < extra.length; j++) {
                v.add(extra[j]);
            }
        }
        return (String[]) v.toArray(new String[v.size()]);
    }

    private static boolean mustWait = false;

    public void launch(String[] args0) throws IOException {
        String[] args = args0;
        File launcherFolder = getAppFolder();
        if (launcherFolder == null) {
            launcherFolder = new File(".");
        }
//        if(appFolder!=null){
//            try {
//                System.setProperty("user.dir",appFolder.getCanonicalPath());
//            } catch (IOException e) {
//            }
//        }

//        for(Iterator i=System.getProperties().entrySet().iterator();i.hasNext();){
//            Map.Entry e=(Map.Entry) i.next();
//            System.out.println(e.getKey()+" = "+e.getValue());
//        }
        ArrayList jvmArgs = new ArrayList();
        ArrayList appArgs = new ArrayList();
//        int status=-1;
        String javaCommand;
        String javaHome = System.getProperty("java.home");
        String osName = System.getProperty("os.name");

        if (osName.toLowerCase().indexOf("windows") >= 0) {
            javaCommand = "javaw";
        } else {
            javaCommand = "java";
        }

        String soutfile = null;
        String serrfile = null;
        String launchFolder = launcherFolder.getCanonicalPath();
//        String extraClassPath = "";
        String classLib = "";
        String requiredVersion = null;
        String className = null;
//        boolean mustWait = false;
//        boolean verbose = false;

        args = prepareArgs(args, getAppFolder(), true);
        if (args.length == 0) {
            help();
            return;
        }
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith(PREFIX)) {
                String[] cmd = JLauncherOptions.parseCommand(args[i].substring(PREFIX.length()));
                String cmdKey = cmd[0];
                String cmdValue = cmd[1];
                if (cmdKey.equals("type")) {
                    if ("console".equals(cmdValue) || "con".equals(cmdValue)) {
                        javaCommand = "java";
                    } else if ("window".equals(cmdValue) || "win".equals(cmdValue)) {
                        javaCommand = "javaw";
                    } else {
                        System.err.println("Unknown JVM type '" + cmdValue + "'");
                        help();
                        return;
                    }
                } else if (cmdKey.equals("?") || cmdKey.equals("help")) {
                    if ("win".equals(cmdValue) || "window".equals(cmdValue)) {
                        helpWindow();
                        return;
                    } else if ("con".equals(cmdValue) || "console".equals(cmdValue) || "".equals(cmdValue)) {
                        help();
                        return;
                    } else {
                        helpWindow();
                        return;
                    }

                } else if (cmdKey.equals("macro")) {
                    if (cmdValue.startsWith("load(") && cmdValue.endsWith(")")) {
                        String toLoad = cmdValue.substring("load(".length(), cmdValue.length() - 1);
                        boolean provided = false;
                        for (int p = 0; p < ClasspathMaker.providers.size(); p++) {
                            ResourceProvider provider = (ResourceProvider) ClasspathMaker.providers.elementAt(p);
                            if (provider.acceptUrlPrefix(toLoad)) {
                                File[] ress = provider.getResources(toLoad);
                                if (ress != null && ress.length > 0) {
                                    provided = true;
                                    break;
                                }
                            }
                        }
                        if (!provided) {
                            System.err.print("unable to load " + toLoad);
                        }
                    } else if (cmdValue.startsWith("mv(") && cmdValue.endsWith(")")) {
                        String[] mvargs = cmdValue.substring("mv(".length(), cmdValue.length() - 1).split(",");
                        File srcFile = new File(launcherFolder + "/" + mvargs[0].trim()).getCanonicalFile();
                        File destFile = new File(launcherFolder + "/" + mvargs[1].trim()).getCanonicalFile();
                        macro_mv(srcFile, destFile);
                    } else {
                        System.err.print("unknow macro " + cmdValue);
                        System.err.print("all known macros are : load(url),mv(srcFile,destFile)");
                    }

                } else if (cmdKey.equals("echo")) {
//                        String fileName = cmdValue;
                    PrintStream ps = createPrintStream(cmdValue, System.out);

                    System.setOut(ps);
                    System.setErr(ps);
//                        if ("on".equalsIgnoreCase(fileName)||"".equals(fileName)) {
//                            verbose = true;
//                        } else if ("off".equalsIgnoreCase(fileName)) {
//                            verbose = false;
//                        } else if ("win".equals(fileName)) {
//                            verbose = true;
//                            mustWait = true;
//                            PrintStream ps = createPrintStream("win:JLauncher Echo...",System.out);
//                            System.setOut(ps);
//                            System.setErr(ps);
//                        } else {
//                            verbose = true;
//                            try {
//                                PrintStream ps = new PrintStream(new FileOutputStream(fileName));
//                                System.setOut(ps);
//                                System.setErr(ps);
//                            } catch (FileNotFoundException e) {
//                                if(verbose){
//                                    System.out.println(e);
//                                }
//                            }
//                        }
                } else if (cmdKey.equals("java.home")) {
                    javaHome = cmdValue;
                } else if (cmdKey.equals("java.version.min")) {
                    requiredVersion = cmdValue;
                } else if (cmdKey.equals("main.class") || cmdKey.equals("main") || cmdKey.equals("app.class")) {
                    className = cmdValue;
                } else if (cmdKey.equals("classlib") || cmdKey.equals("classpath")) {
                    classLib = classLib + ";" + cmdValue;
                } else if (cmdKey.equals("out")) {
                    if (cmdValue.length() > 0) {
                        soutfile = cmdValue;
                    } else {
                        soutfile = null;
                    }
                } else if (cmdKey.equals("err")) {
                    if (cmdValue.length() > 0) {
                        serrfile = cmdValue;
                    } else {
                        serrfile = null;
                    }
                } else if (cmdKey.equals("workdir")) {
                    launchFolder = getAppFile(cmdValue).getAbsolutePath();
                } else {
                    System.err.println("Unknown JLauncher Option " + args[i]);
                    help();
                    return;
//                        jvmArgs.add("-" + args[i].substring(4));
                }
            } else {
                String[] cmd = JLauncherOptions.parseCommand(args[i]);
                if (cmd[0].equals("jvm.arg")) {
                    jvmArgs.add(cmd[1]);
                } else if (cmd[0].equals("app.arg")) {
                    appArgs.add(cmd[1]);
                } else {
                    System.err.println("Unknown JLauncher Option " + args[i]);
                    help();
                    return;
                }
            }
        }
        String java_version = System.getProperty("java.version");
        if (requiredVersion != null && java_version.compareTo(requiredVersion) < 0) {
            // working within jre 1.1
            showError("Java Runtime Missing",
                    "<HTML><Font color=red>Java Runtime Environment version " + requiredVersion + " or later is required (current is " + java_version + ")</Font></HTML>");
            System.exit(-1);
            return;
        }
        if (className == null) {
            // working within jre 1.1
            StringBuffer sb = new StringBuffer();
            sb.append("<HTML><Font color=red>");
            sb.append("Class name is required.<BR>");
            sb.append("</Font>");
            try {
                sb.append("Current Folder : ").append(new File(".").getCanonicalPath()).append("<BR>");
                sb.append("App Folder : ").append(getAppFile(".").getCanonicalPath()).append("<BR>");
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (loadedArgFiles.size() == 0) {
                sb.append("<Font color=red>run.jex file is missing...</Font>");
            } else {
                sb.append("-JVMmain.class=<main class> or -JVMmain=<main class> is missing...<BR>");
                sb.append("Loaded Jex Files :");
                sb.append("<OL>");
                for (Iterator i = loadedArgFiles.iterator(); i.hasNext();) {
                    File file = (File) i.next();
                    try {
                        sb.append("<LI>").append(file.getCanonicalPath()).append("</LI>");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                sb.append("</OL>");
            }
            sb.append("<BR>");
            sb.append("<BR>Create Config File Example?");
            sb.append("</HTML>");
            int r = JOptionPane.showOptionDialog(null,
                    sb.toString(),
                    APP_FULL_NAME + " : Error",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Create", "Help", "Exit"}, "Create");
            if (r == 0) {
                JFileChooser jfc = new JFileChooser(".");
                jfc.setSelectedFile(getAppFile("run.jex"));
                r = jfc.showSaveDialog(null);
                if (r == JFileChooser.APPROVE_OPTION) {
                    File f = jfc.getSelectedFile();
                    PrintWriter out = new PrintWriter(new FileOutputStream(f));
                    BufferedReader bi = null;
                    try {
                        bi = new BufferedReader(new InputStreamReader(JLauncher.class.getResource("/net/vpc/toolbox/jlauncher/Sample.jex").openStream()));
                        String line;
                        while ((line = bi.readLine()) != null) {
                            out.println(line);
                        }
                    } catch (IOException e) {
                        if (bi != null) {
                            try {
                                bi.close();
                            } catch (IOException e1) {
                                if (out != null) {
                                    e1.printStackTrace(out);
                                } else {
                                    e1.printStackTrace();
                                }
                            }
                        }
                        if (out != null) {
                            try {
                                out.close();
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                }
            } else if (r == 1) {
                helpWindow();
            } else {
                System.exit(-1);
            }
            return;
        }

        ArrayList allArgs = new ArrayList();
        allArgs.add(javaHome + File.separator + "bin" + File.separator + javaCommand);
        allArgs.add("-classpath");
        String cp = classLib == null ? "." : ClasspathMaker.makeClasspath(getAppFile(launchFolder), classLib, this);
        if (cp.length() == 0) {
            cp = ".";
        }
        allArgs.add(cp);
//        if (cp != null && cp.length() > 0 && extraClassPath.length() > 0) {
//            cp = cp + System.getProperty("path.separator") + extraClassPath;
//        } else if (extraClassPath.length() > 0) {
//            cp = extraClassPath;
//        }
        allArgs.add("-Djlauncher.workdir=" + new File(".").getCanonicalPath());
        allArgs.add("-Djlauncher.version=" + APP_VERSION);
        allArgs.add("-Djlauncher.argc=" + args0.length);
        for (int i = 0; i < args0.length; i++) {
            allArgs.add("-Djlauncher.args[" + i + "]=" + args0[i]);
        }
        allArgs.add("-Djlauncher.file=" + (loadedArgFiles.size() == 0 ? "" : ((File) loadedArgFiles.get(0)).getCanonicalPath()));
        for (int i = 0; i < loadedArgFiles.size(); i++) {
            allArgs.add("-Djlauncher.file[" + (i) + "]=" + ((File) loadedArgFiles.get(i)).getCanonicalPath());
        }

        allArgs.addAll(jvmArgs);
        allArgs.add(className);
        allArgs.addAll(appArgs);
//        if (verbose) {
        System.out.println("Universal Java VM launcher version " + APP_VERSION + " (c) Vpc Open Source Fondation 2002-2005 (contact : taha.bensalah@gmail.net)");
        System.out.println();
        System.out.println("#############");
        System.out.println("## Arguments:");
        System.out.println("##");
        for (int i = 0; i < args.length; i++) {
            System.out.println("  " + args[i]);
        }
        System.out.println("#############");
        System.out.println();
        System.out.println("#############");
        System.out.println("## Script   :");
        System.out.println("##");
        File newWorkingFolder = getAppFile(launchFolder) == null ? new File(".") : getAppFile(launchFolder).getCanonicalFile();
        macro_mv(getAppFile(".jex/add"), getAppFile("."));
        macro_del(getAppFile(".jex/remove"), getAppFile("."));
        System.out.println("  cd '" + newWorkingFolder + "'");
        StringBuffer argsString = new StringBuffer("  ");
        for (int i = 0; i < allArgs.size(); i++) {
            if (i > 0) {
                argsString.append(" ");
            }
            argsString.append('\'').append(allArgs.get(i)).append('\'');
        }
        System.out.println("  " + argsString.toString());
        System.out.println("#############");

        try {
            Process process = Runtime.getRuntime().exec((String[]) allArgs.toArray(new String[0]), null, newWorkingFolder);
            if (!traceProcess(process, soutfile, serrfile)) {
                if (!mustWait) {
                    System.out.println("That's all folks");
                    //System.exit(0);
                }
            }
        } catch (Throwable e) {
            showError("Java Runtime Missing",
                    "<HTML><Font color=red>Error occurs while launching program</Font><BR>" + e + "</HTML>");
            System.exit(-1);
        }
        System.out.println("that's all folks");
    }

    private static void macro_mv(File source, File dest) throws IOException {
        if (source.exists()) {
            System.out.println("macro_mv(" + source.getCanonicalPath() + "," + dest.getCanonicalPath() + ")");
            if (source.isFile()) {
                if (dest.exists() && dest.isDirectory()) {
                    source.renameTo(new File(dest, source.getName()));
                } else {
                    source.renameTo(dest);
                }
            } else if (source.isDirectory()) {
                File[] files = source.listFiles();
                for (int i = 0; i < files.length; i++) {
                    File file = files[i];
                    if (file.isDirectory()) {
                        File file1 = new File(dest, file.getName());
                        file1.mkdirs();
                        macro_mv(file, file1);
                    } else if (file.isFile()) {
                        file.renameTo(new File(dest, file.getName()));
                    }
                }
                source.delete();
            }
        } else {
            System.out.println("ignore macro_mv(" + source.getCanonicalPath() + "," + dest.getCanonicalPath() + ")");
        }
    }

    private static void macro_del(File source, File dest) throws IOException {
        if (source.exists()) {
            System.out.println("macro_del(" + source.getCanonicalPath() + "," + dest.getCanonicalPath() + ")");
            if (source.isFile()) {
                if (dest.exists() && dest.isDirectory()) {
                    new File(dest, source.getName()).delete();
                } else {
                    dest.delete();
                }
            } else if (source.isDirectory()) {
                File[] files = source.listFiles();
                for (int i = 0; i < files.length; i++) {
                    File file = files[i];
                    if (file.isDirectory()) {
                        File file1 = new File(dest, file.getName());
                        macro_del(file, file1);
                    } else if (file.isFile()) {
                        new File(dest, file.getName()).delete();
                    }
                }
                source.delete();
            }
        } else {
            System.out.println("ignore macro_mv(" + source.getCanonicalPath() + "," + dest.getCanonicalPath() + ")");
        }
    }

    private Hashtable alreadyLoadedPrintStream = new Hashtable();

    public PrintStream createPrintStream(String name, PrintStream defaultStream) throws FileNotFoundException {
        if (name == null || "".equals(name)) {
            return defaultStream;
        } else if ("on".equalsIgnoreCase(name)) {
            mustWait = true;
            return defaultStream;
        } else if ("off".equalsIgnoreCase(name)) {
            mustWait = true;
            return NullOutput.NULL_PRINT_STREAM;
        } else if ("out".equals(name) || "stdout".equals(name)) {
            mustWait = true;
            return System.out;
        } else if ("err".equals(name) || "stderr".equals(name)) {
            mustWait = true;
            return System.err;
        } else if ("win".equals(name)) {
            mustWait = true;
            PrintStream ps = (PrintStream) alreadyLoadedPrintStream.get(name);
            if (ps == null) {
                ps = new PrintStream(new WindowTextOutput("Console..."));
                alreadyLoadedPrintStream.put(name, ps);
            }
            return ps;
        } else if (name.startsWith("win:")) {
            mustWait = true;
            PrintStream ps = (PrintStream) alreadyLoadedPrintStream.get(name);
            if (ps == null) {
                ps = new PrintStream(new WindowTextOutput(name.substring(4)));
                alreadyLoadedPrintStream.put(name, ps);
            }
            return ps;
        } else {
            PrintStream ps = (PrintStream) alreadyLoadedPrintStream.get(name);
            if (ps == null) {
                File appFile = getAppFile(name);
//                try {
//                    JOptionPane.showMessageDialog(null,appFile.getCanonicalPath());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                ps = new PrintStream(new FileOutputStream(appFile));
                alreadyLoadedPrintStream.put(name, ps);
            }
            return ps;
        }
    }

    public boolean traceProcess(final Process process, String soutfile, String serrfile) throws IOException {
        String prefix = "[Process " + System.identityHashCode(process) + "]";
        ProcessTracer out = null;
        ProcessTracer err = null;
//        boolean needsToBlock = (soutfile != null && !"off".equalsIgnoreCase(soutfile)) || (serrfile != null && !"off".equalsIgnoreCase(serrfile));
//        boolean needsToBlock = (soutfile != null) || (serrfile != null);
        PrintStream streamOut = createPrintStream(soutfile, System.out);
        PrintStream streamErr = createPrintStream(serrfile, System.err);

        if (streamOut != null) {
            out = new ProcessTracer(prefix + "OUT> ", streamOut, process.getInputStream());
            new Thread(out).start();
        }
        if (streamErr != null) {
            err = new ProcessTracer(prefix + "ERR> ", streamErr, process.getErrorStream());
            new Thread(err).start();
        }
        if (mustWait) {
            System.out.println("Attached to Process...");
            try {
                process.waitFor();
            } catch (Throwable e) {
                System.out.println(e);
            }
            if (err != null) {
                err.stopped = true;
            }
            if (out != null) {
                out.stopped = true;
            }
            mustWait = false;
            System.out.println(prefix + " exited with " + process.exitValue());
        } else {
            System.out.println("Detached from Process...");
        }
        System.out.println("That's all folks!");
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(3000);
                    process.waitFor();
                    int t = process.exitValue();
                    if (t != 0) {
                        showError("Java Runtime Missing",
                                "<HTML><Font color=red>Error occurs while launching program</Font><BR>Program exited with code " + t + "</HTML>");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

        return mustWait;
    }

//    public static void showAWTErrorAndExit(String title,Throwable error){
//        ByteArrayOutputStream b = new ByteArrayOutputStream();
//        PrintStream ps = new PrintStream(b);
//        error.printStackTrace(ps);
//        showAWTErrorAndExit(title,b.toString());
//    }
//    public static void println(Throwable msg) {
//        ByteArrayOutputStream b = new ByteArrayOutputStream();
//        PrintStream ps = new PrintStream(b);
//        msg.printStackTrace(ps);
//        println(b.toString());
//    }
//    public static void showAWTErrorAndExit(String title, String msg) {
//        // working within jre 1.1
//        println(title + " : " + msg);
//        Frame frame = new Frame(title);
//        String[] messages = (String[]) lineWrapToStringVector(msg, 80, true, null).toArray(new String[0]);
//        Panel labelsPanel = new Panel(new GridLayout(messages.length, 1));
//        for (int i = 0; i < messages.length; i++) {
//            //labels[i]=new Label(msg,Label.CENTER);
//            labelsPanel.add(new Label(replaceString(messages[i], "\t", "  "), Label.LEFT));
//        }
//        Button okButton = new Button("Ok");
//        okButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                System.exit(0);
//            }
//        });
//
//        frame.setLayout(new BorderLayout());
//        frame.add(labelsPanel, BorderLayout.CENTER);
//
//        Panel bar = new Panel(new FlowLayout());
//        class GlueComponent extends Component {
//
//            public Dimension getMinimumSize() {
//                return new Dimension(0, 0);
//            }
//
//            public Dimension getPreferredSize() {
//                return new Dimension(0, 0);
//            }
//
//            public Dimension getMaximumSize() {
//                return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
//            }
//        }
//        bar.add(new GlueComponent());
//        bar.add(okButton);
//        bar.add(new GlueComponent());
//        frame.add(bar, BorderLayout.PAGE_END);
//        frame.setResizable(false);
//        frame.pack();
//        frame.show();
//        frame.addWindowListener(new WindowAdapter() {
//            public void windowClosing(WindowEvent e) {
//                System.exit(0);
//            }
//        });
//        return;
//    }
    public void help() {
        help(System.out);
    }

    public void helpWindow() {
//        PrintStream out = new PrintStream(new WindowTextOutput("Help"));
//        help(out);
//
        try {
            JFrame frame = new JFrame();
            JEditorPane editorPane = new JEditorPane(JLauncher.class.getResource("/net/vpc/toolbox/jlauncher/HelpContents.html"));
            JScrollPane p = new JScrollPane(editorPane);
            frame.setTitle("Help...");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(p);
            frame.setSize(400, 400);
            frame.setVisible(true);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void help(PrintStream out) {
        BufferedReader bi = null;
        try {
            bi = new BufferedReader(new InputStreamReader(JLauncher.class.getResource("/net/vpc/toolbox/jlauncher/HelpContents.txt").openStream()));
            String line;
            while ((line = bi.readLine()) != null) {
                out.println(line);
            }
        } catch (IOException e) {
            if (bi != null) {
                try {
                    bi.close();
                } catch (IOException e1) {
                    e1.printStackTrace(out);
                }
            }
        }
    }
}
