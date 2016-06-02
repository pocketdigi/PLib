package com.pocketdigi.plib.rest;

import java.util.HashMap;
import java.util.Map;

/**
 * 参数处理器
 * 可以添加全局参数
 * Created by fhp on 16/6/1.
 */
public abstract class AbstractParameterProcessor {
    protected Map<String,String> globalParams;
    public abstract void processParameter(RESTRequest request);

    /**
     * 添加全局参数
     * @param key
     * @param value
     */
    public final void addGlobalParameter(String key,String value){
        if(globalParams==null)
            globalParams=new HashMap<>();
        globalParams.put(key,value);
    }

    /**
     * 移除全局参数
     * @param key
     */
    public final void removeGlobalParameter(String key) {
        if(globalParams!=null)
            globalParams.remove(key);
    }

}
