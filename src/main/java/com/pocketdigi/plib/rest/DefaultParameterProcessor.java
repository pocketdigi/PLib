package com.pocketdigi.plib.rest;

import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 默认的参数处理器，支持全局参数
 * Created by fhp on 16/6/1.
 */
public class DefaultParameterProcessor extends AbstractParameterProcessor {
    @Override
    public void processParameter(RESTRequest request) {
        if(request.getMethod()== HttpMethod.GET) {
            addGlobalParamsForGet(request);
        }else{
            addGlobalParamsForPost(request);
        }
    }

    /**
     * 添加GET全局参数，不允许url里存在参数,默认实现只是将参数加到url中，如果需要签名，自己覆盖方法
     * @param request
     */
    private void addGlobalParamsForGet(RESTRequest request){
        MultiValueMap params = request.getParams();
        String url = request.getUrl();
        if(globalParams==null&&params ==null)
            return;
        if (params == null) {
            params = new LinkedMultiValueMap<>();
        }
        if(globalParams!=null&&request.isGlobalParamsJoin()){
            Set<Map.Entry<String, String>> entries = globalParams.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                params.add(entry.getKey(),entry.getValue());
            }
        }
//
        StringBuilder sb = new StringBuilder();
        sb.append(url);
        Set<Map.Entry<String, List<Object>>> entries = params.entrySet();
        int count=0;
        int size = entries.size();
        for (Map.Entry<String, List<Object>> entry : entries) {
            String key = entry.getKey();
            String value="";
            List<Object> objectList = entry.getValue();
            if(objectList!=null&&objectList.size()>0) {
                value=objectList.get(0).toString();
            }
            if(count==0)
                sb.append("?");
            sb.append(key).append("=").append(value);
            if(count<size-1){
                sb.append("&");
            }
            count++;
        }
        request.setUrl(sb.toString());
    }

    /**
     * 添加POST全局参数
     */
    private MultiValueMap<String,Object> addGlobalParamsForPost(RESTRequest request){
        MultiValueMap<String,Object> params =request.getParams();
        if(globalParams==null&&params==null)
            return null;
        if (params == null) {
            params = new LinkedMultiValueMap<>();
        }
        if(globalParams!=null&&request.isGlobalParamsJoin()){
            Set<Map.Entry<String, String>> entrySet = globalParams.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                params.add(entry.getKey(),entry.getValue());
            }
        }
        return params;
    }

}
