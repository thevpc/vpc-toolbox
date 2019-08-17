/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.toolbox.jmodproxy;

import net.vpc.toolbox.jmodproxy.util.MultipleWriter;
import net.vpc.toolbox.jmodproxy.util.WriterPrefixer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author vpc
 */
public class JModProxy {

    ExecutorService pool = Executors.newCachedThreadPool();
    int counter = 1;
    int proxyPort = 9080;

    String destAddress = "10.25.1.16";
    int destPort = 9000;
//    String destAddress = "localhost";
//    int destPort = 9090;

    private JModProxyHeaderRewriter headerRewriter = new PortJModProxyHeaderRewriter(proxyPort, destPort);
    private JModProxyFilter proxyFilter = new JModProxyFilter() {

        @Override
        public void checkRequest(HttpRequest request) throws HttpStatusCodeException {
            if (request.getMethod().equals("HEAD")) {
                throw new HttpStatusCodeException("402", "HEAD IS NOT ALLOWED");
            }
        }

    };

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        JModProxy jModProxy = new JModProxy();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if("-p".equals(arg)){
                i++;
                jModProxy.proxyPort=Integer.parseInt(args[i]);
            }
            if("-tp".equals(arg)){
                i++;
                jModProxy.destPort=Integer.parseInt(args[i]);
            }
            if("-ta".equals(arg)){
                i++;
                jModProxy.destAddress=(args[i]);
            }
        }
        jModProxy.run();
    }

    public void run() {
        try {
            System.out.println("j_mod_proxy started on port " + proxyPort);
            System.out.println("config is :");
            System.out.println(config());
            ServerSocket ss = new ServerSocket(proxyPort);
            while (true) {
                final Socket s = ss.accept();
                pool.submit(new Runnable() {

                    @Override
                    public void run() {
                        process(s);
                    }
                });
            }
        } catch (IOException ex) {
            Logger.getLogger(JModProxy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String config() {
        return "<Proxy *>\n"
                + "\tOrder Deny,Allow\n"
                + "\tDeny from all\n"
                + "\tAllow from 192.168.0\n"
                + "\tProxyPass / http://" + destAddress + ":" + destPort + "\n"
                + "</Proxy> ";
    }

    private static class Ctx {

        HttpRequest request;
        Socket src;
        BufferedReader src_in;
        Writer src_out;
        HttpProcessor httpProcessor;
//        Socket dest;
//        InputStream dest_in;
//        OutputStream dest_out;
        boolean requestHeader = true;
        boolean responseHeader = true;
        boolean enabled = true;
        int index;
        Writer logRequest = null;
        Writer logResponse = null;
        Writer wtarget = null;
        Writer wsrc = null;
    }

    DecimalFormat df = new DecimalFormat("00000");

    public File getTempFolder(){
        File f=new File(System.getProperty("user.home"),"tmp/proxy/");
        f.mkdirs();
        return f;
    }
    private Writer nextFileRequest(int x) throws IOException {
        return new FileWriter(new File(getTempFolder(),  df.format(x) + ".req"));
    }

    private Writer nextFileResponse(int x) throws IOException {
        return new FileWriter(new File(getTempFolder(), df.format(x) + ".resp"));
    }

    private void process(Socket src) {
        try {
            final Ctx c = new Ctx();
            c.index = counter++;
            c.src = src;
            c.enabled = true;
            c.src_in = new BufferedReader(new InputStreamReader(src.getInputStream()));
            c.src_out = new OutputStreamWriter(src.getOutputStream());
            c.logRequest = nextFileRequest(c.index);
            c.logResponse = nextFileResponse(c.index);


            String line1 = c.src_in.readLine();//readLine(c.src_in);
            c.request = new HttpRequest();
            if (line1 != null) {
                String[] rl = line1.split(" ");
                c.request.setUserRequestLine(line1);
                c.request.setRequestLine(headerRewriter.rewriteRequestURL(line1));
                c.request.setMethod(rl[0]);
                c.request.setUrl(rl[1]);
                c.request.setVersion(rl[2]);
            }
//            c.logRequest.write((c.request.getRequestLine() == null ? "<<NULL>>" : c.request.getRequestLine()));
//            c.logRequest.write(("\n"));
//            c.logRequest.flush();
//            System.out.println((c.request.getRequestLine() == null ? "<<NULL>>" : c.request.getUserRequestLine()));
            if (c.request.getUserRequestLine() == null) {
                c.src_out.write("HTTP/1.1 404 Ammar\n");
                c.src_out.flush();
                c.src_in.close();
                c.src_out.close();
                return;
            }
            try {
                proxyFilter.checkRequest(c.request);
            } catch (HttpStatusCodeException ex) {
                c.src_out.write(("HTTP/1.1 " + ex.getStatusCode() + " " + ex.getStatusMessage() + "\n"));
                c.src_out.flush();
                c.src_in.close();
                c.src_out.close();
                return;
            }
            if (c.enabled) {
                c.httpProcessor = new HttpProcessorDispatcher(destAddress, destPort);
                c.wtarget =new MultipleWriter()
                        .add(new WriterPrefixer(new OutputStreamWriter(System.out), "["+c.index+"]TARGET> "))
                        .add(new WriterPrefixer(c.logRequest, "TARGET> "))
                        .add(c.httpProcessor.getOut());
                c.wsrc =new MultipleWriter()
                        .add(new WriterPrefixer(new OutputStreamWriter(System.out), "["+c.index+"]SOURCE> "))
                        .add(new WriterPrefixer(c.logRequest, "SRC> "))
                        .add(c.src_out);
//                c.dest = new Socket(destAddress, destPort);
//                c.dest_in = c.dest.getInputStream();
//                c.dest_out = (c.dest.getOutputStream());
                c.wtarget.write(c.request.getRequestLine() == null ? null : (c.request.getRequestLine() + "\n"));
                c.wtarget.flush();
            }
            pool.submit(new Runnable() {

                @Override
                public void run() {
                    try {
                        try {
                            char[] buffer = new char[1024];
                            while (true) {
                                if (c.requestHeader) {
                                    String line = c.src_in.readLine();
                                    if (line == null) {
                                        return;
                                    }
                                    if (line.length() == 0) {
                                        c.requestHeader = false;
                                        String hv = line;
                                        c.wtarget.write(hv);
                                        c.wtarget.write("\n");
                                        c.wtarget.flush();
                                    } else {
                                        HttpHeader h = rewriteRequestHeader(parseHeader(line));
                                        String hv = h.getName() + ": " + h.getValue();
                                        c.wtarget.write(hv);
                                        c.wtarget.write("\n");
                                        c.wtarget.flush();
                                    }
                                } else {
//                                    if (c.src_in.available() <= 0) {
//                                        break;
//                                    }
                                    int nbr = c.src_in.read(buffer);
                                    if (nbr <= 0) {
                                        break;
                                    }
                                    c.wtarget.write(buffer, 0, nbr);
                                }
                            }
                        } finally {
                            c.wtarget.close();
                            System.out.println("closed logRequest " + c.index);
                        }
                    } catch (SocketException ex) {
                        ex.printStackTrace();
                    } catch (Exception ex) {
                        Logger.getLogger(JModProxy.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            );
            pool.submit(new Runnable() {

                @Override
                public void run() {
                    char[] buffer = new char[1024];
                    try {
                        try {
                            String statusLine = null;
                            if (c.enabled) {
                                statusLine = c.httpProcessor.getIn().readLine();
                                c.wsrc.write(statusLine);
                                c.wsrc.write(("\n"));
                                c.wsrc.flush();
                            }
                            while (true) {
                                if (c.enabled) {
                                    if (c.responseHeader) {
                                        String line = c.httpProcessor.getIn().readLine();
                                        if (line == null) {
                                            return;
                                        }
                                        if (line.length() == 0) {
                                            String hv = line;
                                            c.responseHeader = false;
                                            c.wsrc.write(hv);
                                            c.wsrc.write("\n");
                                            c.wsrc.flush();
                                        } else {
                                            HttpHeader h = rewriteResponseHeader(parseHeader(line));
                                            String hv = h.getName() + ":" + h.getValue();
                                            c.wsrc.write(hv);
                                            c.wsrc.write("\n");
                                            c.wsrc.flush();
                                        }
                                    } else {
                                        int nbr = c.httpProcessor.getIn().read(buffer);
                                        if (nbr <= 0) {
                                            break;
                                        }
                                        c.wsrc.write(buffer, 0, nbr);
                                    }

                                } else {
                                    return;
                                }
                            }
                        } finally {
                            if (c.wsrc != null) {
                                c.wsrc.close();
                            }
                            System.out.println("closed logResponse " + c.index);
                        }
                    } catch (SocketException ex) {
                        return;
                    } catch (Exception ex) {
                        Logger.getLogger(JModProxy.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            );
        } catch (SocketException ex) {
            return;
        } catch (Exception ex) {
            Logger.getLogger(JModProxy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private HttpHeader rewriteRequestHeader(HttpHeader line) {
        return line == null ? null : headerRewriter.rewriteRequestHeader(line);
    }

    private HttpHeader rewriteResponseHeader(HttpHeader line) {
        return line == null ? null : headerRewriter.rewriteResponseHeader(line);
    }

    private String readLine(InputStream in) throws IOException {
        StringBuilder sb = null;
        int c;
        while ((c = in.read()) != -1) {
            if (sb == null) {
                sb = new StringBuilder();
            }
            char cc = (char) c;
            if (cc == '\n') {
                if (sb.charAt(sb.length() - 1) == '\r') {
                    sb.deleteCharAt(sb.length() - 1);
                }
                break;
            }
            sb.append(cc);
        }
        while (sb == null && (c = in.read()) != -1) {
            if (sb == null) {
                sb = new StringBuilder();
            }
            char cc = (char) c;
            if (cc == '\n') {
                if (sb.charAt(sb.length() - 1) == '\r') {
                    sb.deleteCharAt(sb.length() - 1);
                }
                break;
            }
            sb.append(cc);
        }
        return sb == null ? null : sb.toString();
    }

    private HttpHeader parseHeader(String line) {
        if (line == null || line.length() == 0) {
            return null;
        }
        int i = line.indexOf(':');
        if (i <= 0) {
            return null;
        }
        return new HttpHeader(line.substring(0, i).trim(), line.substring(i + 1).trim());
    }

}
