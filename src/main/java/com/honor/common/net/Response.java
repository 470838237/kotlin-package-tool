package com.honor.common.net;


import com.honor.common.tools.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Dns;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class Response implements IResult {
    private int responseCode;
    private boolean success;
    private HttpURLConnection connection;
    protected String message;
    private long contentLength;
    private ParameterMap<String> headers = new ParameterMap<>();
    private Request request;
    private Throwable throwable;
    private static OkHttpClient okHttpClient;
    private String url;

    /**
     * @return 通知游戏错误
     */
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public String getErrorMsg() {
        if (throwable == null)
            return message;
        return Utils.getStackTrace(throwable);
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public Response() {

    }


    public ParameterMap<String> getHeaders() {
        return headers;
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public String getContentType() {
        return headers.get("content-type");
    }

    public long getContentLength() {
        return contentLength;
    }


    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    private Response userSystemHttp(HttpClient client, Request request) throws IOException {
        Body requestBody = request.getBody();
        if (request.getMethod() == Request.POST) {
            if (requestBody == null) {
                success(false);
                setMessage("request body is null");
                setResponseCode(StatusCode.REQUEST_BODY_NULL);
                return this;
            }
            if (requestBody.getContentType() != null) {
                client.getConfig().addHeader("content-type", requestBody.getContentType());
            }
        }

        HttpURLConnection connection = ConnectionFactory.createConnection(client.getConfig(), request);
        if (Request.POST.equals(request.getMethod())) {
            InputStream inputStream = requestBody.getInputStream();
            OutputStream outputStream = connection.getOutputStream();
            if (inputStream != null) {
                int length;
                byte[] b = new byte[4096];
                while ((length = inputStream.read(b)) != -1) {
                    outputStream.write(b, 0, length);
                }
                inputStream.close();
            } else {
                byte[] output = request.getBody().data();
                outputStream.write(output, 0, (int) requestBody.getContentLength());
            }
            outputStream.flush();
            outputStream.close();
        }
        this.connection = connection;
        this.request = request;
        Map<String, List<String>> responseHeaders = connection.getHeaderFields();
        if (responseHeaders != null) {
            Set<String> keys = responseHeaders.keySet();
            for (String key : keys) {
                String value = "";
                List<String> lists = responseHeaders.get(key);
                if (lists != null) {
                    for (String temp : lists) {
                        value = value + temp + ";";
                    }
                }
                if (value.length() > 0)
                    value = value.substring(0, value.length() - 1);
                headers.put(key, value);
            }

        }
        contentLength = connection.getContentLength();
        responseCode = connection.getResponseCode();
        if (responseCode >= 300 && responseCode < 400) {
            String redirectUrl = getHeader("location");
            request.setUrl(redirectUrl);
            return client.doSyncRequest(request, this);
        }
        if (responseCode >= 200 && responseCode < 300) {
            success = true;
            this.body = body();
            if (body != null) {
                parseBody(body);
            } else {
                success = false;
                setMessage("response body is null");
            }
        } else {
            success = false;
            InputStream errorStream = connection.getErrorStream();
            if (errorStream != null) {
                byte[] b = new byte[4096];
                StringBuilder sb = new StringBuilder();
                int length;
                while ((length = errorStream.read(b)) != -1) {
                    sb.append(new String(b, 0, length));
                }
                message = sb.toString();
                errorStream.close();
            }
        }
        return this;
    }

    private Response useOkHttp(final HttpClient client, final Request request) throws IOException {
        final Body requestBody = request.getBody();
        if (request.getMethod() == Request.POST) {
            if (requestBody == null) {
                success(false);
                setMessage("request body is null");
                setResponseCode(StatusCode.REQUEST_BODY_NULL);
                return this;
            }
        }
        if (okHttpClient == null) {
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
            clientBuilder.readTimeout(client.getConfig().getReadTimeout(), TimeUnit.MILLISECONDS);
            clientBuilder.connectTimeout(client.getConfig().getConnectTimeout(), TimeUnit.MILLISECONDS);
            clientBuilder.followRedirects(client.getConfig().isFollowRedirect());
            okHttpClient = clientBuilder.build();
        } else {
            okHttpClient = okHttpClient.newBuilder().readTimeout(client.getConfig().getReadTimeout(), TimeUnit.MILLISECONDS)
                    .connectTimeout(client.getConfig().getConnectTimeout(), TimeUnit.MILLISECONDS)
                    .followRedirects(client.getConfig().isFollowRedirect()).build();
        }
        HttpUrl httpUrl = null;
        if (request.getUrl() == null || (httpUrl = HttpUrl.parse(request.getUrl())) == null) {
            success = false;
            responseCode = StatusCode.INVALID_URL;
            message = "invalid url " + request.getUrl();
            return this;
        }
        okhttp3.Request.Builder rqBuilder = new okhttp3.Request.Builder().url(httpUrl);
        ParameterMap<String> rqHeaders = client.getConfig().getHeaders();
        for (String key : rqHeaders.keySet()) {
            rqBuilder.addHeader(key, rqHeaders.get(key));
        }

        if (Request.POST.equals(request.getMethod())) {
            rqBuilder.post(new RequestBody() {
                @Override
                public MediaType contentType() {
                    String contentType = requestBody.getContentType();
                    if (contentType == null) return null;
                    return MediaType.parse(contentType);
                }

                @Override
                public void writeTo(BufferedSink bufferedSink) throws IOException {
                    InputStream inputStream = request.getBody().getInputStream();
                    if (inputStream != null) {
                        int length;
                        byte[] b = new byte[4096];
                        while ((length = inputStream.read(b)) != -1) {
                            bufferedSink.write(b, 0, length);
                        }
                        inputStream.close();
                    } else {
                        byte[] output = request.getBody().data();
                        bufferedSink.write(output, 0, (int) request.getBody().getContentLength());
                    }
                }
            });
        }
        okhttp3.Request rq = rqBuilder.build();
        okhttp3.Response rp = okHttpClient.newCall(rq).execute();
        responseCode = rp.code();
        contentLength = rp.body().contentLength();
        Headers okHeader = rp.headers();
        for (String key : okHeader.names()) {
            headers.put(key, okHeader.get(key));
        }
        if (responseCode >= 300 && responseCode < 400) {
            String redirectUrl = getHeader("location");
            if (redirectUrl != null) {
                request.setUrl(redirectUrl);
                return client.doSyncRequest(request, this);
            }
        }
        if (rp.isSuccessful()) {
            success = true;
            body = Body.create(getContentType(), rp.body().byteStream(), contentLength);
            if (body != null) {
                parseBody(body);
            } else {
                success = false;
                setMessage("response body is null");
            }
        } else {
            success = false;
            message = rp.message();
        }
        return this;
    }

    public String getUrl() {
        return url;
    }

    public Response parseResponse(HttpClient client, Request request) throws IOException {
        url = request.getUrl();
        if (client.getConfig().isUseOkHttp()) {
            return useOkHttp(client, request);
        } else {
            return userSystemHttp(client, request);
        }
    }


    private Body body;

    public Body body() {
        if (success) {
            try {
                if (body == null)
                    body = Body.create(getContentType(), connection.getInputStream(), contentLength);
                if (contentLength == -1)
                    contentLength = body.getContentLength();
                return body;
            } catch (IOException e) {
                throwable = e;
                success = false;
                message = e.getMessage();
                System.out.println("HonorResponse url:" + request.getUrl() + "\nErrorMsg:" + getErrorMsg());
            }
        }
        return null;
    }

    @Override
    public boolean success() {
        return success;
    }

    @Override
    public void success(boolean success) {

    }

    protected void parseBody(Body body) {


    }

    public void close() {
        if (connection != null)
            connection.disconnect();
        if (body != null && body.getInputStream() != null) {
            try {
                body.getInputStream().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
