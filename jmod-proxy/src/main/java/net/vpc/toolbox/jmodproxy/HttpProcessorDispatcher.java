package net.vpc.toolbox.jmodproxy;

import java.io.*;
import java.net.Socket;

/**
 * @author taha.bensalah@gmail.com on 7/27/16.
 */
public class HttpProcessorDispatcher implements HttpProcessor {
    private BufferedReader dest_in;
    private Writer dest_out;
    private String destAddress = "localhost";
    private int destPort = 9090;
    private Socket dest;

    public HttpProcessorDispatcher(String destAddress, int destPort) throws IOException {
        this.destAddress = destAddress;
        this.destPort = destPort;
        this.dest = new Socket(destAddress,destPort);
        dest_in = new BufferedReader(new InputStreamReader(dest.getInputStream()));
        dest_out = new OutputStreamWriter((dest.getOutputStream()));
    }

    @Override
    public Writer getOut() {
        return dest_out;
    }

    @Override
    public BufferedReader getIn() {
        return dest_in;
    }

}
