/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.toolbox.jmodproxy;

/**
 *
 * @author vpc
 */
public class PortJModProxyHeaderRewriter implements JModProxyHeaderRewriter {
    private int fromPort;
    private int toPort;

    public PortJModProxyHeaderRewriter(int fromPort, int toPort) {
        this.fromPort = fromPort;
        this.toPort = toPort;
    }
    
    public String rewriteRequestURL(String url) {
        return url.replace(":"+fromPort, ":"+toPort);
    }
    
    public HttpHeader rewriteRequestHeader(HttpHeader line) {
        return new HttpHeader(line.getName(), line.getValue().replace(":"+fromPort, ":"+toPort));
    }

    public HttpHeader rewriteResponseHeader(HttpHeader line) {
        return new HttpHeader(line.getName(), line.getValue().replace(":"+toPort, ":"+fromPort));
    }
}
