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

import net.vpc.toolbox.jlauncher.io.Output;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import net.vpc.toolbox.jlauncher.io.Input;

/**
 * User: taha
 * Date: 4 oct. 2003
 * Time: 13:33:17
 */
public class RemoteResourceServerProvider extends ResourceProvider {
//    public static void main(String[] args) {
////        RRSUrl rrsUrl=new RRSUrl("rrs://mourad:98/ant.jar{1.2}");
//        RRSUrl rrsUrl = new RRSUrl("rrs://mourad/c:/ant/lib/soft/ant.jar");
//        System.out.println(rrsUrl);
//    }

    private Properties versions;
    private File versionsFile;

    public RemoteResourceServerProvider() {
        this(new File("versions.rrs"));
    }

    public RemoteResourceServerProvider(File versionsFile) {
        this.versionsFile = versionsFile;
        versions = new Properties();
        if (versionsFile.exists()) {
            FileInputStream stream = null;
            try {
                stream = new FileInputStream(versionsFile);
                versions.load(stream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void store() {
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(versionsFile);
            versions.store(stream, "");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean acceptUrlPrefix(String url) {
        return url.startsWith("rrs://");
    }

    public File[] getResources(String url) {
        RRSUrl rrsUrl = new RRSUrl(url);
        String[] files = new String[0];
        ArrayList allResources = new ArrayList();
        try {
            files = getFileNames(rrsUrl.getServerAddress(), rrsUrl.getServerPort(), rrsUrl.getFileName(), rrsUrl.getVersion());
            for (int i = 0; i < files.length; i++) {
                String server_version = getFileVersion(rrsUrl.getServerAddress(), rrsUrl.getServerPort(), files[i]);
                String local_version = versions.getProperty(files[i]);
                if ((local_version == null || server_version == null) || local_version.compareTo(server_version) < 0) {
                    //
                    System.out.print("loading " + files[i] + " (local version '" + (local_version == null ? "unknown" : local_version) + "' vs remote version '" + (server_version == null ? "unknown" : server_version) + "' ) ...");
                    File loadedFile = null;
                    try {
                        loadedFile = getFile(rrsUrl.getServerAddress(), rrsUrl.getServerPort(), files[i], new File(rrsUrl.getTarget()));
                    } finally {
                        System.out.println(loadedFile == null ? " failed" : " ok");
                        if (loadedFile != null) {
                            allResources.add(loadedFile);
                        }
                    }
                } else {
                    File f = new File(rrsUrl.getTarget(), files[i]);
                    if (f.exists()) {
                        allResources.add(f);
                    }
                }
                if (server_version != null) {
                    versions.setProperty(files[i], server_version);
                    store();
                }
            }
        } catch (IOException e) {
            System.err.println("Unable de load " + url + " : " + e.toString());
        }
        return (File[]) allResources.toArray(new File[allResources.size()]);
    }

    public static String getFileVersion(String serverAddress, int serverPort, String file) throws UnknownHostException, IOException {
        return (String) immediatRequest(serverAddress, serverPort, 1, new Object[]{file});
    }

    public static String[] getFileNames(String serverAddress, int serverPort, String fileFilter, String minVersion) throws UnknownHostException, IOException {
        return (String[]) immediatRequest(serverAddress, serverPort, 3, new Object[]{fileFilter, minVersion});
    }

    public static File getFile(String serverAddress, int serverPort, String file, File target) throws UnknownHostException, IOException {
        byte[] b = (byte[]) immediatRequest(serverAddress, serverPort, 2, new Object[]{file});
        if (b != null) {
            File f = new File(target, file);
            File parent = f.getParentFile();
            if (parent != null) {
                parent.mkdirs();
            }
            FileOutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(f);
                outputStream.write(b);
            } finally {
                if (outputStream != null) {
                    outputStream.close();
                }
            }
            return f;
        }
        return null;
    }

    public static Object immediatRequest(String serverAddress, int serverPort, int op, Object[] params) throws UnknownHostException, IOException {
        String service = "RRS";
        Input query_in;
        Socket query_socket = null;
        try {
            serverAddress = (serverAddress == null) ? "localhost" : serverAddress;
            serverPort = (serverPort <= 0) ? 9887 : serverPort;
            query_socket = new Socket(serverAddress, serverPort);
            Output query_out = new Output(query_socket.getOutputStream());
            query_in = new Input(query_socket.getInputStream());
            query_out.writeUTF("jin-protocol-1.0");
            query_out.writeUTF("[NC]");
            return request(query_in, query_out, service, op, params);
        } finally {
            if (query_socket != null) {
                query_socket.close();
            }
        }
    }

    public static Object request(Input in, Output out, String service, int operation, Object[] params) throws IOException {
        synchronized (out) {
            Object retValues = "<<ERROR OCCURS>>";
            try {
                out.writeUTF(service);
                out.writeInt(operation);
                out.writeObject(params);
                retValues = readResponse(in);
                return retValues;
            } catch (IOException e) {
                throw e;
            }
        }
    }

    public static Object readResponse(Input in) throws IOException {
//        System.query_out.println("reading response");
        int ack = in.readInt();
        switch (ack) {
            case 1://JinUtils.RESPONSE_OK:
                {
                    Object ret = in.readObject();
                    return ret;
                }
            case 2://JinUtils.RESPONSE_ERR:
                {
                    Object[] ret = (Object[]) in.readObject();
                    if (ret != null && (ret.length % 2) == 0) {
//                    System.query_out.println("\tRES="+Arrays.asList(ret));
                        Throwable jinError = null;
                        try {
                            Throwable cause = null;
                            for (int i = ret.length - 2; i >= 0; i -= 2) {
                                try {
                                    if (cause == null) {
                                        cause = (Throwable) Class.forName((String) ret[i]).getConstructor(new Class[]{String.class}).newInstance(new Object[]{(String) ret[i + 1]});
                                    } else {
                                        cause = (Throwable) Class.forName((String) ret[i]).getConstructor(new Class[]{String.class, Throwable.class}).newInstance(new Object[]{(String) ret[i + 1], cause});
                                    }
                                } catch (Throwable e) {
                                    cause = new RuntimeException((String) ret[i] + " : " + (String) ret[i + 1]);
                                }
                            }
                            jinError = cause;
                        } catch (ClassCastException e) {
                            throw new IOException("Expected a JinServiceException while recieving " + Arrays.asList(ret));
                        } catch (Throwable e) {
                            throw new IOException("Server error occurs but i was unable to get error from " + Arrays.asList(ret));
                        }
                        throw jinError instanceof IOException ? (IOException) jinError : new IOException(jinError.toString());
                    } else {
                        throw new IOException("Corrupt error response : bad error format");
                    }
                }
            default:
                {
                    throw new IOException("Corrupt response : Unknown ack " + ack);
                }
        }
    }

}
