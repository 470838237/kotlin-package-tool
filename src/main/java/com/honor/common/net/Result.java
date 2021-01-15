package com.honor.common.net;


import com.google.gson.Gson;

public class Result implements IResult, Cloneable {

    protected String code = "-999";
    protected String message;

    protected boolean success;

    /**
     * @return true 成功，false  失败
     */
    public boolean success() {
        return success;
    }

    public void success(boolean success) {
        this.success = success;
    }

    public Result() {
    }

    public Result(String code) {
        this.code = code;
    }

    public Result(boolean success) {
        this.success = success;
    }


    /**
     * @return 错误码
     */
    public String getCode() {
        return code;
    }

    public Result setCode(String code) {
        this.code = code;
        return this;
    }

    /**
     * @return 失败信息
     */
    public String getMessage() {
        return message;
    }

    public void setMessage(String errorMessage) {
        this.message = errorMessage;
    }

    /**
     * @return 返回json字符串
     */
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }


    @Override
    public Result clone() {
        try {
            return (Result) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return new Gson().fromJson(toString(), getClass());
        }
    }
}
