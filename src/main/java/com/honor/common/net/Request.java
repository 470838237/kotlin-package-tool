package com.honor.common.net;


public class Request {

    public static final String GET = "GET";
    public static final String POST = "POST";


    private String url;
    private String method;
    private boolean async;

    private Body body;

    public Body getBody() {
        return body;
    }



    public void setBody(Body body) {
        this.body = body;
    }

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }


    public String getUrl() {

        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

}
