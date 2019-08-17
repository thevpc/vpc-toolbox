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

import java.util.Vector;


/**
 * @author Taha Ben Salah (taha.bensalah@gmail.com)
 * @creationtime 7 sept. 2007 01:18:45
 */
public class JLauncherRuntimeInfo {
    public static JLauncherRuntimeInfo getInstance(){
        JLauncherRuntimeInfo info=new JLauncherRuntimeInfo();
        info.version=System.getProperty("jlauncher.version");
        if(info.version==null){
            return null;
        }
        info.file=System.getProperty("jlauncher.file");
        int i=0;
        Vector a=new Vector();
        while(true){
            String s=System.getProperty("jlauncher.file["+i+"]");
            if(s!=null){
                a.add(s);
                i++;
            }else{
                break;
            }
        }
        info.files= (String[]) a.toArray(new String[a.size()]);
        int argc=0;
        argc=Integer.parseInt(System.getProperty("jlauncher.argc"));
        info.args=new String[argc];
        for(i=0;i<argc;i++){
            info.args[i]=System.getProperty("jlauncher.args["+i+"]");
        }
        return info;
    }

    private String[] args;
    private String file;
    private String[] files;
    private String version;

    public JLauncherRuntimeInfo() {

    }

    public String getFile() {
        return file;
    }

    public String getVersion() {
        return version;
    }

    public String getArgument(int i) {
        return args[i];
    }

    public int getArgumentsCount(){
        return args.length;
    }

    public String getFile(int i) {
        return files[i];
    }

    public int getFilesCount(){
        return files.length;
    }
}
