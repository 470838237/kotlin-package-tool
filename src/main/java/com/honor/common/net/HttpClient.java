package com.honor.common.net;


import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpClient {
    protected static ExecutorService service = Executors.newFixedThreadPool(8);
    protected HttpConfig config;
    private static DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
    private static FileHttpClient fileHttpClient = new FileHttpClient();

    public HttpClient(HttpConfig config) {
        this.config = config;
    }

    public HttpConfig getConfig() {

        return config;
    }

    public static DefaultHttpClient getDefaultHttpClient() {
        return defaultHttpClient;
    }


    public static FileHttpClient getFileHttpClient() {
        return fileHttpClient;
    }

    public <T extends Response> T doSyncRequest(Request request, T object) {
        if (object == null)
            object = (T) new Response();
        try {
            return (T) object.parseResponse(this, request);
        } catch (IOException e) {
            object.success(false);
            object.setMessage("NET ERROR");
            object.setThrowable(e);
            object.setResponseCode(StatusCode.NET_ERROR);
        }
        return object;
    }

    public <T extends Response> void doAsyncRequest(final Request request, final ResponseHandler<T> handler) {
        service.execute(new Runnable() {
            @Override
            public void run() {
                if (handler == null) {
                    doSyncRequest(request, new Response() {
                        @Override
                        protected void parseBody(Body body) {
                            super.parseBody(body);
                            close();
                        }
                    });
                } else {
                    handler.setUrl(request.getUrl());
                    T result = handler.getResponseObject();
                    result = doSyncRequest(request, result);
                    handler.onFinish(result);
                }
            }
        });
    }

}
