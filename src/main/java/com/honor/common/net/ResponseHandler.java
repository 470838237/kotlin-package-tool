package com.honor.common.net;


public abstract class ResponseHandler<T extends Response> implements Callback<T> {


    private String url;

    public T getResponseObject() {
        Class<T> clazz = (Class<T>) GenericParser.getClassGeneric(this.getClass());
        T object = null;
        try {
            object = clazz.newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return object;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
