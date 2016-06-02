package com.pocketdigi.plib.rest;

import org.springframework.http.HttpStatus;

/**
 * Created by fhp on 16/5/31.
 */
public interface RESTResponseListener<T> {
    void onResponse(T data);

    /**
     * 如果是io错误，一般是没网，两个参数都空
     * @param status http状态码
     * @param responseText 返回的文本
     */
    void onError(HttpStatus status,String responseText);
}
