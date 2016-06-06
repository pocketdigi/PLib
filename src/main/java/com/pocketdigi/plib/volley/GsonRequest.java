package com.pocketdigi.plib.volley;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pocketdigi.plib.core.PLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * 自动用gson转换的request
 * Created by Exception on 16/6/3.
 */
public class GsonRequest<T> extends Request<T>{
    Response.Listener<T> listener=null;
    Class<T> responseType;
    //默认缓存7天
    long cacheMaxAge= 7*24*60*60*1000;
    protected TreeMap<String,String> params;
    public GsonRequest(int method, String url,Response.Listener<T> listener,Class<T> responseType, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.listener=listener;
        this.responseType=responseType;
        params=new TreeMap<>();
    }
    public GsonRequest(int method, String url, Response.Listener<T>  listener,Class<T> responseType) {
        this(method, url, listener,responseType,null);
    }


    @Override
    protected void deliverResponse(T response) {
        if(listener!=null)
            listener.onResponse(response);
    }


    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers,"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        PLog.d(this,"URL:"+getUrl());
        PLog.d(this,"Response:"+parsed);
        Gson gson=new Gson();
        T object = gson.fromJson(parsed, responseType);
        //缓存不会判断服务端返回的header允不允许，只会根据shouldCache
        return Response.success(object,AlwaysCacheHttpHeaderParser.parseCacheHeaders(response,cacheMaxAge));
    }

    public void addParam(String key,String value) {
        params.put(key,value);
    }




}
