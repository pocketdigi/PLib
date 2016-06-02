package com.pocketdigi.plib.rest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;

/**
 * Request
 * Created by fhp on 16/5/31.
 */
public class RESTRequest<T> {
    String url;
    HttpMethod method;
    Class<T> responseClazz;
    //弱引用监听器
//    WeakReference<RESTResponseListener<T>> responseListener;
    RESTResponseListener<T> responseListener;

    HttpHeaders headers;
    MultiValueMap<String,Object> params;
    //是否加入全局参数
    boolean isGlobalParamsJoin=true;
    //是否加入全局Header
    boolean isGlobalHeaderJoin=true;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public Class<T> getResponseClazz() {
        return responseClazz;
    }

    public void setResponseClazz(Class<T> responseClazz) {
        this.responseClazz = responseClazz;
    }

    public RESTResponseListener<T> getResponseListener() {
//        return responseListener==null?null:responseListener.get();
        return responseListener;
    }

    public void setResponseListener(RESTResponseListener<T> responseListener) {
//        this.responseListener = new WeakReference<>(RESTResponseListener);
        this.responseListener =responseListener;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }

    public MultiValueMap<String, Object> getParams() {
        return params;
    }

    public void setParams(MultiValueMap<String, Object> params) {
        this.params = params;
    }

    public boolean isGlobalParamsJoin() {
        return isGlobalParamsJoin;
    }

    public void setGlobalParamsJoin(boolean globalParamsJoin) {
        isGlobalParamsJoin = globalParamsJoin;
    }

    public boolean isGlobalHeaderJoin() {
        return isGlobalHeaderJoin;
    }

    public void setGlobalHeaderJoin(boolean globalHeaderJoin) {
        isGlobalHeaderJoin = globalHeaderJoin;
    }
}
