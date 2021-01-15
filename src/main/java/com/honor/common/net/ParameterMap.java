package com.honor.common.net;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ParameterMap<T> extends HashMap<String, T> {
    public ParameterMap(Map<String, T> params) {
        putAll(params);
    }

    public ParameterMap() {
    }

    public JsonObject toJson() {
        return toJson(false);
    }


    public JsonObject toJson(boolean sort) {

        JsonObject json = new JsonObject();
        Collection<String> keys = this.keySet();
        if (sort) {
            keys = new ArrayList<>(this.keySet());
            Collections.sort((ArrayList<String>) keys);
        }
        for (String key : keys) {
            json.addProperty(key, String.valueOf(get(key)));
        }
        return json;
    }

    @Override
    public T put(String key, T value) {
        if (value == null) {
            value = (T) "";
        }
        return super.put(key, value);
    }

    public String toJsonString() {
        return toJson(false).toString();
    }


    public String toFormString() {
        return toFormString(false);
    }

    public String toFormString(boolean sort, boolean encode) {
        Collection<String> keys = this.keySet();
        if (sort) {
            keys = new ArrayList<>(this.keySet());
            Collections.sort((ArrayList<String>) keys);
        }
        StringBuilder stringBuilder = new StringBuilder();
        int i = 0;
        for (String key : keys) {
            i++;
            String value = String.valueOf(get(key));
            try {
                if (encode)
                    value = URLEncoder.encode(value, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            stringBuilder.append(key).append("=").append(value);
            if (i < keys.size()) {
                stringBuilder.append("&");
            }
        }

        return stringBuilder.toString();
    }

    public String toFormString(boolean sort) {
        return toFormString(sort, false);
    }

    public String toJsonString(boolean sort) {
        return toJson(sort).toString();
    }

    public static ParameterMap parse(String jsonString) {
        Type collectionType = new TypeToken<ParameterMap<String>>() {
        }.getType();
        return new Gson().fromJson(jsonString, collectionType);
    }

    public static ParameterMap parse(JsonObject json) {
        Type collectionType = new TypeToken<ParameterMap<String>>() {
        }.getType();
        return new Gson().fromJson(json, collectionType);
    }

}
