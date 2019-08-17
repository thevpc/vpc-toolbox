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

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Classpath Maker
 *
 * @author Taha BEN SALAH (taha.bensalah@ant-inter.com)
 *         User: taha
 *         Date: 11-mai-2004
 *         Time: 21:47:13
 */
class ClasspathMaker {
    static Vector providers = new Vector();

    static {
        registerProvider(new RemoteResourceServerProvider());
    }

    public static void registerProvider(ResourceProvider provider) {
        providers.add(provider);
    }

    private static FilenameFilter jarZipFilenameFilter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
            String n = name.toLowerCase();
            return n.endsWith(".jar") || n.endsWith(".zip");
        }
    };

    private static FileFilter folderFileFilter = new FileFilter() {
        public boolean accept(File pathname) {
            return pathname.isDirectory();
        }
    };

    private static ArrayList listLibraries(File[] folders, ArrayList libraries, JLauncher jLauncher) {
        if (libraries == null) {
            libraries = new ArrayList();
        }
        for (int i = 0; i < folders.length; i++) {
            if (!(folders[i].exists())) {
                if (!jLauncher.showConfirm(LaunchUtils.getString("JLauncher.resourceMissing.title"),
                        LaunchUtils.getString("JLauncher.resourceMissing.msg", new Object[]{folders[i].getAbsolutePath()}))) {
                    System.exit(-1);
                }
            } else if (folders[i].isFile()) {
                if (folders[i].getName().toLowerCase().endsWith(".jar") || folders[i].getName().toLowerCase().endsWith(".zip")) {
                    libraries.add(folders[i]);
                }
            } else {
                File[] jars = folders[i].listFiles(jarZipFilenameFilter);
                if (jars == null) {
                    if (!jLauncher.showConfirm(LaunchUtils.getString("JLauncher.resourceMissing.title"),
                            LaunchUtils.getString("JLauncher.resourceMissing.msg", new Object[]{folders[i].getAbsolutePath()}))) {
                        System.exit(-1);
                    }
                }
                boolean isJar = new File(folders[i], "jars").exists();
                if (isJar) {
                    for (int j = 0; jars!=null && j < jars.length; j++) {
                        libraries.add(jars[j]);
                    }
                    File[] folderChildren = folders[i].listFiles(folderFileFilter);
                    LaunchUtils.sort(folderChildren);
                    listLibraries(folderChildren, libraries, jLauncher);
                } else {
                    libraries.add(folders[i]);
                }
            }
        }
        return libraries;
    }

    public static String makeClasspath(File root, String resources, JLauncher jLauncher) {
        String path_separator = ";";//System.getProperty("path.separator");
        StringTokenizer st = new StringTokenizer(resources, path_separator);
        ArrayList vector = new ArrayList();
        while (st.hasMoreTokens()) {
            vector.add(st.nextToken());
        }
        ArrayList libraries = listLibraries(getResourceFiles(root, (String[]) vector.toArray(new String[vector.size()]), jLauncher), null, jLauncher);
        StringBuffer newpath = new StringBuffer();
        String rootPrefix = "";
        try {
            rootPrefix = root.getCanonicalPath() + System.getProperty("file.separator");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
        for (int i = 0; i < libraries.size(); i++) {
            File libFile = (File) libraries.get(i);
            if (i > 0) {
                newpath.append(System.getProperty("path.separator"));
            }
            String lib = libFile.getAbsolutePath();
            try {
                lib = libFile.getCanonicalPath();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (lib.startsWith(rootPrefix)) {
                lib = lib.substring(rootPrefix.length());
            }
            newpath.append(lib);
        }
        return newpath.toString();
    }

    public static File[] getResourceFiles(File rootFolder, String[] libFolders, JLauncher jLauncher) {
        // for the moment the will be only file from local machine
        // later i will give some files from network or database
        try {
            rootFolder = rootFolder.getCanonicalFile();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        ArrayList v = new ArrayList();
        for (int i = 0; i < libFolders.length; i++) {
            String currentLib = libFolders[i];
            boolean isMandatory = currentLib.endsWith("!");
            if (isMandatory) {
                currentLib = currentLib.substring(0, currentLib.length() - 1);
            }
            boolean provided = false;
            for (int p = 0; p < providers.size(); p++) {
                ResourceProvider provider = (ResourceProvider) providers.elementAt(p);
                if (provider.acceptUrlPrefix(currentLib)) {
                    File[] ress = provider.getResources(currentLib);
                    if (ress != null && ress.length > 0) {
                        for (int r = 0; r < ress.length; r++) {
                            v.add(ress[r]);
                        }
                        provided = true;
                        break;
                    }
                }
            }

            if (!provided) {
                File resourceFile = new File(currentLib);
                File f;
                if (resourceFile.isAbsolute()) {
                    f = resourceFile;
                } else {
                    f = new File(rootFolder.getAbsolutePath() + System.getProperty("file.separator") + resourceFile);
                }
                if (f.exists()) {
                    v.add(f);
                    provided = true;
                } else if (f.getName().indexOf('*')>=0 || f.getName().indexOf('?')>=0) {
                    File pf = f.getParentFile();
                    final String exp = replaceString(replaceString(replaceString(f.getName(),".", "\\."),"*", ".*"),"?", ".");
                    File[] fs = pf.listFiles(new FilenameFilter() {
                        public boolean accept(File dir, String name) {
                            return name.matches(exp);
                        }
                    });
                    if (fs != null) {
                        for (int j = 0; j < fs.length; j++) {
                            v.add(fs[j]);
                            provided = true;
                        }
                    }
                }
            }
            if (!provided && isMandatory) {

                if (!jLauncher.showConfirm(LaunchUtils.getString("JLauncher.resourceMissing.title"),
                        LaunchUtils.getString("JLauncher.resourceMissing.msg", new Object[]{libFolders[i]}))) {
                    System.exit(-1);
                }
            }

        }
        File[] ret = new File[v.size()];
        for (int i = 0; i < v.size(); i++) {
            ret[i] = (File) v.get(i);
        }
        return ret;
    }

    public static String replaceString(String string, String oldPortion, String newPotion) {
        String x = "";
        for (int i = 0; i < string.length();) {
            String souschaine = substring(string, i, i + oldPortion.length());
            if (oldPortion.equals(souschaine)) {
                x = x + newPotion;
                i += oldPortion.length();
            } else {
                x = x + string.charAt(i);
                i++;
            }
        }

        return x;
    }

    public static String substring(String string, int start, int end) {
        if (string == null || string.length() == 0)
            return "";
        if (start < 0)
            start = 0;
        if (end > string.length())
            end = string.length();
        if (end <= start)
            return "";
        else
            return string.substring(start, end);
    }
    

}
