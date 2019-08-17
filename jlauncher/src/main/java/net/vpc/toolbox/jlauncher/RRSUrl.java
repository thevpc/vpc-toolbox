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

/**
 * Created by IntelliJ IDEA.
 * User: TAHA
 * Date: 7 fevr. 2004
 * Time: 10:10:10
 * To change this template use Options | File Templates.
 */
class RRSUrl {
    private String serverAddress;
    private int serverPort;
    private String fileName;
    private String target = ".";
    private String version;

    /**
     * rrs://server[:port][/path_to_folder]/file_name[{version}][>local_path_to_store_to]
     *
     * @param url
     */
    public RRSUrl(String url) {
        String[] s = LaunchUtils.split(url, '>', false);
        if (s != null) {
            url = s[0];
            target = s[1];
        }

        int i1 = url.indexOf('/', 6);
        if (i1 == -1) {
            throw new IllegalArgumentException("Uncorrect Url " + url + " . acceptable pattern is rrs://server[:port][/path_to_folder]/file_name[{version}][>local_path_to_store_to]");
        }
        int i3 = -1;
        if (url.endsWith("}")) {
            i3 = url.lastIndexOf('{');
        }
        serverAddress = url.substring(6, i1);
        s = LaunchUtils.split(getServerAddress(), ':', true);
        if (s != null) {
            serverAddress = s[0];
            serverPort = Integer.parseInt(s[1]);
        }
        if (i3 > 0) {
            fileName = url.substring(i1 + 1, i3);
            version = url.substring(i3 + 1, url.length() - 1);
        } else {
            fileName = url.substring(i1 + 1);
        }
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public int getServerPort() {
        return serverPort;
    }

    public String getFileName() {
        return fileName;
    }

    public String getTarget() {
        return target;
    }

    public String getVersion() {
        return version;
    }
}
