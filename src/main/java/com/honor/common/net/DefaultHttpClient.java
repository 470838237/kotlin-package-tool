package com.honor.common.net;


public class DefaultHttpClient extends HttpClient {

    public DefaultHttpClient(HttpConfig config) {
        super(config);
    }

    public DefaultHttpClient() {
        super(getDefault());
    }


    public static HttpConfig getDefault() {
        HttpConfig config = new HttpConfig();
        config.setConnectTimeout(5 * 1000);
        config.setReadTimeout(5 * 1000);
        config.setFollowRedirect(true);
        config.setUserCache(true);
        config.addHeader("connection", "close");
        return config;
    }

    /**
     * ---------------post async-------------------------
     */
    public void postAsync(String url) {
        postAsync(url, null, null);
    }

    public void postAsync(String url, ParameterMap<String> params) {
        postAsync(url, params, null);
    }


    public <T extends Response> void postAsync(String url, ResponseHandler<T> handler) {
        postAsync(url, null, handler);
    }

    public <T extends Response> void postAsync(String url, ParameterMap<String> params, ResponseHandler<T> handler) {
        Request request = createRequest(url, params, true);
        doAsyncRequest(request, handler);
    }


    /**
     * ---------------post sync-------------------------
     */
    public <T extends Response> T postSync(String url) {
        return postSync(url, null, null);
    }

    public <T extends Response> T postSync(String url, T object) {
        return postSync(url, null, object);
    }


    public <T extends Response> T postSync(String url, ParameterMap<String> params, T object) {
        Request request = createRequest(url, params, true);
        return doSyncRequest(request, object);
    }


    /**
     * ---------------get async-------------------------
     */
    public void getAsync(String url) {
        getAsync(url, null);
    }

    public <T extends Response> void getAsync(String url, ResponseHandler<T> handler) {
        Request request = createRequest(url, null, false);
        doAsyncRequest(request, handler);
    }

    /**
     * ---------------get sync-------------------------
     */


    public <T extends Response> T getSync(String url) {
        return getSync(url, null);
    }

    public <T extends Response> T getSync(String url, T object) {
        Request request = createRequest(url, null, false);
        return doSyncRequest(request, object);
    }


    private Request createRequest(String url, ParameterMap<String> params, boolean post) {
        Request request = new Request();
        if (post)
            request.setMethod(Request.POST);
        else
            request.setMethod(Request.GET);

        request.setAsync(true);
        request.setUrl(url);
        if (!post) {
            return request;
        }
        if (params == null) params = new ParameterMap<>();
        IParamsEncode paramsEncode = config.getParamsEncode();
        if (paramsEncode != null) {
            byte[] result = paramsEncode.encode(config.getBaseParams(), params);
            request.setBody(Body.create(paramsEncode.getContentType(), result));
        } else {
            params.putAll(config.getBaseParams());
            request.setBody(Body.create(config.getContentType(), params.toFormString()));
        }

        return request;
    }


}
