package com.honor.common.net;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class ConnectionFactory {

    private ConnectionFactory() {
    }

    public static HttpURLConnection createConnection(HttpConfig config, Request request) throws IOException {
        URL url = new URL(request.getUrl());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(request.getMethod());
        connection.setReadTimeout(config.getReadTimeout());
        connection.setConnectTimeout(config.getConnectTimeout());
        connection.setUseCaches(config.isUserCache());
        connection.setInstanceFollowRedirects(config.isFollowRedirect());
        ParameterMap<String> headers = config.getHeaders();
        for (String key : headers.keySet()) {
            String value = headers.get(key);
            connection.setRequestProperty(key, value);
        }
        connection.setDoInput(true);
        if (Request.POST.equals(request.getMethod())) {
            connection.setDoOutput(true);
        }

        return connection;
    }


}
