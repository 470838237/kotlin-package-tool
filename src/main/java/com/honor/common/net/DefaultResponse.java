package com.honor.common.net;


public class DefaultResponse extends Response {
    @Override
    protected void parseBody(Body body) {
        String content = body.string();
        parseBody(content);
        close();
    }
    protected void parseBody(String body) {
        
    }

}
