package org.ieszaidinvergeles.dam.chaterbot.HTTP;

import java.util.List;

public class HTTPR {

    private int statusCode;
    private String content;
    private List<Header> headers;

    public HTTPR(int statusCode, String content, List<Header> headers) {
        this.statusCode = statusCode;
        this.content = content;
        if (headers != null && headers.size() <= 0) {
            this.headers = null;
        } else {
            this.headers = headers;
        }
    }

    public HTTPR(int statusCode, String content) {
        this(statusCode, content, null);
    }

    public HTTPR(int statusCode) {
        this(statusCode, null, null);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public void setHeaders(List<Header> headers) {
        this.headers = headers;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getContent() {
        return (content == null ? "" : content);
    }

    public void setContent(String content) {
        this.content = content;
    }

}
