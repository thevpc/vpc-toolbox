package net.vpc.common.javashell;

import net.vpc.common.javashell.cmds.*;
import net.vpc.common.javashell.util.ExecProcessInfo;
import net.vpc.common.javashell.util.ProcessWatcher;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimpleJShell extends JShell {

    public static void main(String[] args) {
        JShell shell = new SimpleJShell();
        shell.run(args);
    }

    public SimpleJShell() {
        setExternalExecutor(new SimpleJShellExternalExecutor());
    }

    @Override
    public JShellContext createContext() {
        JShellContext globalContext = super.createContext();
        JShellAliasManager a = globalContext.aliases();
        a.set("ll", "ls");
        a.set(".", "source");
        a.set("dir", "ls");
        a.set("whatis", "man");
        a.set("help", "man");
        a.set("?", "man");
        a.set("cls", "clear");
        a.set("print", "echo");
        globalContext.builtins().set(new CdCmd(), new TypeCmd(), new PwdCmd(), new SourceCmd(), new ShowerrCmd(),
                new AliasCmd(), new UnaliasCmd(), new ExitCmd(), new SetCmd(), new UnsetCmd(), new ExportCmd(),
                new UnexportCmd(), new EnvCmd(), new PropsCmd(), new SetPropCmd(), new UndeclareCommandCmd()
        );
        return globalContext;
    }

    protected int runAndWait(ExecProcessInfo process) {
        try {
            return ProcessWatcher.runAndWait(process);
        } catch (IOException ex) {
            throw new JShellException(100, ex);
        }
    }

    private class SimpleJShellExternalExecutor implements JShellExternalExecutor {

        public String which(String path0, JShellContext context) {
            if (path0.contains("/")) {
                File ff = new File(path0);
                if (!ff.isAbsolute()) {
                    ff = new File(context.getCwd(), path0);
                }
                if (ff.exists()) {
                    return path0;
                }
                return null;
            }
            String classpath = context.vars().get("CLASSPATH", "");
            String path = context.vars().get("PATH", "");
//        String execExt = env("EXEC_EXT", null);
            String sexecPackages = context.vars().get("EXEC_PKG", "");
            String[] classpathArr = classpath.split(":");
            String[] pathArr = path.split(":");
            String[] sexecPackagesArr = sexecPackages.split(":");

            for (String p : classpathArr) {
                if (!p.trim().isEmpty()) {
                    String[] a = new String[sexecPackagesArr.length];
                    for (int i = 0; i < a.length; i++) {
                        a[i] = sexecPackagesArr[i] + "." + path0;
                    }
                    String[] cls = findClassesInPath(p, a, context);
                    if (cls.length > 0) {
                        return cls[0];
                    }
                }
            }
            for (String p : pathArr) {
                if (!p.trim().isEmpty()) {
                    String[] a = new String[sexecPackagesArr.length];
                    for (int i = 0; i < a.length; i++) {
                        if (sexecPackagesArr[i].isEmpty()) {
                            a[i] = path0;
                        } else {
                            a[i] = sexecPackagesArr[i] + "." + path0;
                        }
                    }
                    String[] cls = findClassesInPath(p, a, context);
                    if (cls.length > 0) {
                        return cls[0];
                    }
                    String[] cls2 = findExecFilesInPath(p, new String[]{path0}, context);
                    if (cls2.length > 0) {
                        return cls2[0];
                    }
                }
            }
            return null;
        }

        public String which_debug(String path0, JShellContext context) {
            System.out.printf("which_debug : %s\n", path0);
            if (path0.contains("/")) {
                File ff = new File(path0);
                if (!ff.isAbsolute()) {
                    ff = new File(context.getCwd(), path0);
                }
                if (ff.exists()) {
                    System.out.printf("file exists : %s\n", path0);
                    return path0;
                }
                System.out.printf("file does not exists : %s\n", path0);
                return null;
            }
            String classpath = context.vars().get("CLASSPATH", "");
            String path = context.vars().get("PATH", "");
//        String execExt = env("EXEC_EXT", null);
            String sexecPackages = context.vars().get("EXEC_PKG", "");
            String[] classpathArr = classpath.split(":");
            String[] pathArr = path.split(":");
            String[] sexecPackagesArr = sexecPackages.split(":");

            System.out.printf("classpath : %s\n", classpath);
            System.out.printf("path : %s\n", path);

            for (String p : classpathArr) {
                if (!p.trim().isEmpty()) {
                    String[] a = new String[sexecPackagesArr.length];
                    for (int i = 0; i < a.length; i++) {
                        a[i] = sexecPackagesArr[i] + "." + path0;
                    }
                    String[] cls = findClassesInPath(p, a, context);
                    if (cls.length > 0) {
                        return cls[0];
                    }
                }
            }
            for (String p : pathArr) {
                if (!p.trim().isEmpty()) {
                    System.out.printf("classpathItem : %s\n", p);
                    String[] a = new String[sexecPackagesArr.length];
                    for (int i = 0; i < a.length; i++) {
                        if (sexecPackagesArr[i].isEmpty()) {
                            a[i] = path0;
                        } else {
                            a[i] = sexecPackagesArr[i] + "." + path0;
                        }
                    }
                    System.out.printf("look findClassesInPath : %s\n", Arrays.asList(a));
                    String[] cls = findClassesInPath(p, a, context);
                    if (cls.length > 0) {
                        return cls[0];
                    }
                    System.out.printf("look findExecFilesInPath : %s\n", Arrays.asList(a));
                    String[] cls2 = findExecFilesInPath(p, new String[]{path0}, context);
                    if (cls2.length > 0) {
                        return cls2[0];
                    }
                }
            }
            return null;
        }

        @Override
        public void execExternalCommand(String[] command, JShellContext context) {

            String cmdPath = which(command[0], context);
            if (cmdPath != null) {

                context.setLastResult(null);
                ArrayList<String> cmdArr = new ArrayList<String>();
                ArrayList<String> envArr = new ArrayList<String>();
                if (cmdPath.contains("/")) {
                    cmdArr.add(cmdPath);
                } else {
                    cmdArr.add("java");
                    cmdArr.add(cmdPath);
                }

                for (int i = 1; i < command.length; i++) {
                    cmdArr.add(command[i]);
                }

                for (Map.Entry<Object, Object> entry : context.vars().getAll().entrySet()) {
                    envArr.add(entry.getKey() + "=" + entry.getValue());
                }
                int rr = 0;
                ExecProcessInfo info = new ExecProcessInfo(
                        cmdArr.toArray(new String[0]),
                        envArr.toArray(new String[0]),
                        new File(context.getCwd()),
                        context.in(),
                        context.out(),
                        context.err()
                );
                int r = runAndWait(info);
                if (r == 0) {
                    return;
                } else {
                    throw new JShellException(r, command[0] + ": Execution failed with code " + r);
                }
            } else {
                throw new JShellException(1, "not found " + command[0]);
            }
        }
    }

}
