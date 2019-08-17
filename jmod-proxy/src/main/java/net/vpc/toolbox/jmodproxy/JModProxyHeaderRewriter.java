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
public interface JModProxyHeaderRewriter {

    public String rewriteRequestURL(String url);

    public HttpHeader rewriteRequestHeader(HttpHeader line);

    public HttpHeader rewriteResponseHeader(HttpHeader line);
}
