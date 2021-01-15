package com.honor.common.net;



public interface IParamsEncode {
    /**
     * @param publicParams 公共参数
     * @param noPublicParams 非公共参数
     * @return encode之后的参数
     */
    byte[] encode(ParameterMap publicParams, ParameterMap noPublicParams);

    String getContentType();
}
