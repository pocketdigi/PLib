package com.pocketdigi.plib.http;

import android.support.annotation.StringDef;
import android.webkit.MimeTypeMap;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Exception on 16/6/12.
 */
public class PRequest<T> implements Callback {
    Request.Builder builder;
    String method;
    RequestBody requestBody;
    HashMap<String, String> params;
    Object postObject;
    PResponseListener<T> responseListener;
    Class<T> responseType;
    Call call;
    public PRequest(@METHOD String method, String url, PResponseListener<T> responseListener, Class<T> responseType) {
        this.responseListener = responseListener;
        this.method = method;
        this.responseType = responseType;

        builder = new Request.Builder();
        builder.url(url);
    }

    public PRequest(String url, PResponseListener<T> responseListener, Class<T> responseType) {
        this(GET, url, responseListener, responseType);

    }

    public PRequest(String url) {
        this(GET, url, null, null);
    }


    public void addHeader(String key, String value) {
        builder.addHeader(key, value);
    }

    public void addParam(String key, String value) {
        if (params == null)
            params = new HashMap<>();
        params.put(key, value);
    }

    public Call getCall() {
        return call;
    }

    public void setCall(Call call) {
        this.call = call;
    }

    public void setPostObject(Object obj) {
        postObject = obj;
    }

    public Request buildRequest() {
        if (postObject != null) {
            //post json
            requestBody = new JSONBody(postObject);
        } else if (params != null) {
            //表单post
            FormBody.Builder bodyBuilder = new FormBody.Builder();
            Set<String> keySet = params.keySet();
            for (String key : keySet) {
                String value = params.get(key);
                bodyBuilder.add(key,value);
            }
            requestBody = bodyBuilder.build();
        }
        builder.method(method, requestBody);
        builder.tag(this);
        return builder.build();
    }

    /**
     * 取消请求
     */
    public void cancel(){
        if(call!=null) {
            call.cancel();
        }
    }


    @Override
    public void onFailure(Call call, IOException e) {
        responseListener.onError(this, e);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        String responseText = response.body().string();
        if (responseType == String.class) {
            responseListener.onResponse(this, (T) responseText);
        } else {
            //
            Gson gson = new Gson();
            T obj = gson.fromJson(responseText, responseType);
            responseListener.onResponse(this, obj);
        }
    }

    @StringDef({GET, POST, PUT, DELETE, PATCH})
    public @interface METHOD {
    }

    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";
    public static final String PATCH = "PATCH";


}
