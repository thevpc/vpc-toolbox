/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.toolbox.vpc.mockserver;

import com.google.gson.JsonElement;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author vpc
 */
public class HttpResponse {

    private int code;
    private Map<String, String> attributes = new HashMap<>();
    private String request;
    private String contentType;
    private JsonElement json;
    private String text;

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public JsonElement getJson() {
        return json;
    }

    public void setJson(JsonElement json) {
        this.json = json;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
