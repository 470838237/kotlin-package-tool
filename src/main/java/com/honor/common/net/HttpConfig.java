package com.honor.common.net;


public class HttpConfig {

    private ParameterMap<String> headers = new ParameterMap<>();
    private ParameterMap<String> baseParams = new ParameterMap<>();
    private int readTimeout;
    private int connectTimeout;
    private boolean userCache;
    private boolean followRedirect;
    private IParamsEncode paramsEncode;
    private String contentType;
    private boolean useOkHttp;

    public boolean isUseOkHttp() {
        return useOkHttp;
    }

    public void setUseOkHttp(boolean useOkHttp) {
        this.useOkHttp = useOkHttp;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public ParameterMap<String> getBaseParams() {
        return baseParams;
    }

    public HttpConfig addBaseParams(ParameterMap<String> baseParams) {
        this.baseParams.putAll(baseParams);

        return this;
    }

    public HttpConfig addBaseParam(String key, String value) {
        baseParams.put(key, value);
        return this;
    }

    public IParamsEncode getParamsEncode() {
        return paramsEncode;
    }

    public HttpConfig setParamsEncode(IParamsEncode paramsEncode) {
        this.paramsEncode = paramsEncode;
        return this;
    }

    public boolean isFollowRedirect() {
        return followRedirect;
    }

    public HttpConfig setFollowRedirect(boolean followRedirect) {
        this.followRedirect = followRedirect;
        return this;
    }


    public ParameterMap<String> getHeaders() {
        return headers;
    }

    public HttpConfig addHeaders(ParameterMap<String> headers) {
        headers.putAll(headers);
        return this;
    }

    public HttpConfig addHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public HttpConfig setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public HttpConfig setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public boolean isUserCache() {
        return userCache;
    }

    public HttpConfig setUserCache(boolean userCache) {
        this.userCache = userCache;
        return this;
    }

}
