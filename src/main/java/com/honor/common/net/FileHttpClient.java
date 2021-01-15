package com.honor.common.net;

import java.io.File;

public class FileHttpClient extends HttpClient {


    public FileHttpClient(HttpConfig config) {
        super(config);
    }

    public FileHttpClient() {
        super(getDefault());
    }

    public static HttpConfig getDefault() {
        HttpConfig config = new HttpConfig();
        config.setConnectTimeout(5 * 1000);
        config.setReadTimeout(5 * 1000);
        config.setFollowRedirect(true);
        config.setUserCache(false);
        config.addHeader("Connection", "close");
        config.addHeader("Charset", "UTF-8");
        return config;
    }

    /**
     * 文件上传
     */

    public <T extends Response> void uploadFile(String url, String filePath, ResponseHandler<T> handler) {
        File file = new File(filePath);
        if (!file.exists()) {
            T response = handler.getResponseObject();
            response.setMessage("uploadFile:filePath is not exists");
            response.success(false);
            response.setResponseCode(StatusCode.UPLOAD_FILE_NOT_EXIST);
            handler.onFinish(response);
            return;
        }

        Request request = new Request();
        request.setBody(Body.create(Body.FILE, file));
        request.setMethod(Request.POST);
        request.setAsync(true);
        request.setUrl(url);
        doAsyncRequest(request, handler);
    }

    public <T extends Response> void downloadFile(String url, ResponseHandler<T> handler) {
        Request request = new Request();
        request.setBody(Body.create(Body.JSON, new String()));
        request.setMethod(Request.GET);
        request.setAsync(true);
        request.setUrl(url);
        doAsyncRequest(request, handler);
    }


    public <T extends Response> T  downloadFileSync(String url, T result) {
        Request request = new Request();
        request.setBody(Body.create(Body.JSON, new String()));
        request.setMethod(Request.GET);
        request.setAsync(true);
        request.setUrl(url);
        return doSyncRequest(request,result);
    }


}
