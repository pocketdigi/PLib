package com.pocketdigi.plib.rest;

import org.springframework.http.HttpHeaders;

/**
 * Created by fhp on 16/6/1.
 */
public abstract class AbstractHeaderProcessor {
    protected HttpHeaders globalHeaders;

    protected abstract void processHeader(RESTRequest request);

    public void addGlobalHeader(String key,String value) {
        if(globalHeaders==null) {
            globalHeaders=new HttpHeaders();
        }
        globalHeaders.add(key,value);
    }

    public void removeGlobalHeader(String key){
        if(globalHeaders!=null) {
            globalHeaders.remove(key);
        }
    }

}
