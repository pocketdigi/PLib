package com.pocketdigi.plib.rest;

import org.springframework.http.HttpHeaders;

/**
 * 默认的header处理器
 * Created by fhp on 16/6/1.
 */
public class DefaultHeaderProcessor extends AbstractHeaderProcessor{
    /**
     * 添加全局header
     * @param headers
     * @return
     */
    protected HttpHeaders addGlobalHeaders(HttpHeaders headers) {
        if(headers==null&&globalHeaders==null) {
            return null;
        }
        if(headers==null) {
            headers=new HttpHeaders();
        }
        if(globalHeaders!=null) {
            headers.putAll(globalHeaders);
        }
        return headers;

    }

    @Override
    protected void processHeader(RESTRequest request) {
        HttpHeaders httpHeaders = addGlobalHeaders(request.getHeaders());
        request.setHeaders(httpHeaders);
    }
}
