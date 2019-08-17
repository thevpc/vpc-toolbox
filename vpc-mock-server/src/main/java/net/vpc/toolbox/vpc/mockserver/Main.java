/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.toolbox.vpc.mockserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author vpc
 */
public class Main {

    private static Gson g = new GsonBuilder().setPrettyPrinting().create();
    private Map<String, HttpResponse> responses = new HashMap<>();
    private HttpResponse errorResponse;
    private File dir;
    private int port;

    public static void main(String[] args) {
        int port = 0;
        File dir = null;
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("-port")) {
                i++;
                port = Integer.parseInt(args[i]);
            } else if (arg.equals("-dir")) {
                i++;
                dir = new File(args[i]);
            } else {
                System.out.println("HttpMockServer -port <port> -dir <dir>");
                return;
            }
        }
        new Main(port, dir).run();
    }

    public Main(int port, File dir) {
        this.port = port;
        this.dir = dir;
    }

    private static void writeFile(String path, HttpResponse r) throws FileNotFoundException {
        PrintStream ps = new PrintStream(path);
        ps.print(g.toJson(r));
        ps.close();
    }

    private static String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public HttpResponse loadHttpResponseFile(File file) {
        if (!file.exists()) {
            return null;
        }
        String t;
        try {
            t = readFile(file.getPath(), Charset.defaultCharset());
            HttpResponse y = g.fromJson(t, HttpResponse.class);
            if (y != null && y.getRequest() != null) {
                return y;
            }
        } catch (IOException ex) {
            //ignore..
            System.err.println("Error loading " + file);
            ex.printStackTrace();
        }
        return null;
    }

    public void loadMocks(File file) {
        if (file == null) {
            file = new File(".");
        }
        Map<String, HttpResponse> m = new HashMap<>();
        File[] children = file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.toLowerCase().endsWith(".hmock.json")) {
                    return true;
                }
                return false;
            }
        });
        if (children != null) {
            for (File c : children) {
                try {
                    HttpResponse y = loadHttpResponseFile(c);
                    if (y != null) {
                        m.put(y.getRequest(), y);
                    }
                } catch (Exception ex) {
                    System.err.println("Error loading " + c);
                    ex.printStackTrace();
                }
            }
        }
        HttpResponse err = loadHttpResponseFile(new File(file,"hmock-error.json"));
        if (err != null) {
            errorResponse = err;
        }
        responses = m;
    }

    public HttpResponse getResponse(String request) {
        HttpResponse v = responses.get(request);
        if (v == null) {
            v = errorResponse;
        }
        if (v == null) {
            v = new HttpResponse();
            v.setCode(404);
            v.setJson(g.fromJson("{'error':'url not found'}", JsonObject.class));
        }
        return v;
    }

    public void run() {
        loadMocks(dir);

        if (responses.isEmpty()) {
            HttpResponse r = new HttpResponse();
            r.setRequest("/hello");
            r.setCode(200);
            r.setContentType("application/vnd.api+json");
            r.setText("{}");
            File y = new File("hello.hmock.json");
            try {
                writeFile(y.getPath(), r);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        String context = "/";
        final HttpHandler handler = new HttpHandler() {
            @Override
            public void handle(com.sun.net.httpserver.HttpExchange he) throws IOException {
                OutputStream b = he.getResponseBody();
                String request = he.getRequestURI().toString();
                HttpResponse msg = getResponse(request);
                String contentType = msg.getContentType();
                String t = null;

                if (msg.getJson() != null) {
                    t = msg.getJson().toString();
                    if (contentType == null) {
                        contentType = "application/vnd.api+json";
                    }
                } else {
                    t = msg.getText();
                    if (contentType == null) {
                        contentType = "text/plain";
                    }
                }

                byte[] bytes = t.getBytes();

                he.setAttribute("Content-Type", "application/vnd.api+json");
                Headers hh = he.getResponseHeaders();
                hh.add("Content-Type", "application/vnd.api+json");
                for (Map.Entry<String, String> ee : msg.getAttributes().entrySet()) {
                    hh.add(ee.getKey(), ee.getValue());
                }
                he.sendResponseHeaders(200, bytes.length);
                he.getResponseBody().write(bytes);
            }
        };
        HttpServer httpServer = null;
        try {
            //Create HttpServer which is listening on the given port
            httpServer = HttpServer.create(new InetSocketAddress(port <= 0 ? 8080 : port), 0);
            //Create a new context for the given context and handler
            httpServer.createContext(context, handler);
            //Create a default executor
            httpServer.setExecutor(null);

        } catch (Exception e) {
            e.printStackTrace();
        }
        httpServer.start();
//        try {
//            Object ii = new Object();
//            synchronized (ii) {
//                ii.wait();
//            }
//        } catch (InterruptedException ex) {
//            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }
}
