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
public interface JModProxyFilter {

    public void checkRequest(HttpRequest request) throws HttpStatusCodeException;
}
