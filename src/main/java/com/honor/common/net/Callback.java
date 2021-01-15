package com.honor.common.net;


import java.io.Serializable;

public interface Callback<T> extends Serializable {
    void onFinish(T result);
}
