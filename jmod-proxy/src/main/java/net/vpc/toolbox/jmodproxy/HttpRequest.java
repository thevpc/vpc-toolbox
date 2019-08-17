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
public class HttpRequest {

    private String userRequestLine;
    private String requestLine;
    private String method;
    private String url;
    private String version;

    public HttpRequest() {
    }

    public String getUserRequestLine() {
        return userRequestLine;
    }

    public void setUserRequestLine(String userRequestLine) {
        this.userRequestLine = userRequestLine;
    }

    public String getRequestLine() {
        return requestLine;
    }

    public void setRequestLine(String requestLine) {
        this.requestLine = requestLine;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "HttpRequest{" + "userRequestLine=" + userRequestLine + ", requestLine=" + requestLine + ", method=" + method + ", url=" + url + ", version=" + version + '}';
    }

    
}
