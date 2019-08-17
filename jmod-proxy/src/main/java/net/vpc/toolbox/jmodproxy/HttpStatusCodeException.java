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
public class HttpStatusCodeException extends Exception {

    private String statusCode;
    private String statusMessage;

    public HttpStatusCodeException(String statusCode, String statusMessage) {
        super("HttpStatusCodeException(" + statusCode + "," + statusMessage + ")");
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

}
