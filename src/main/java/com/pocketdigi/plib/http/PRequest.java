package com.pocketdigi.plib.http;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.StringDef;
import android.webkit.MimeTypeMap;

import com.google.gson.Gson;
import com.pocketdigi.plib.util.UiThreadExecutor;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.framed.Header;

/**
 * Created by Exception on 16/6/12.
 */
public class PRequest<T> implements Callback {
    protected Request.Builder builder;
    String method;
    protected RequestBody requestBody;
    protected TreeMap<String, String> params;
    protected Object postObject;
    protected PResponseListener<T> responseListener;
    protected Class<T> responseType;
    protected Call call;
    protected Handler handler;
    protected TreeMap<String,String> headerMap;
    protected String url;
    public PRequest(@METHOD String method, String url, PResponseListener<T> responseListener, Class<T> responseType) {
        this.responseListener = responseListener;
        this.method = method;
        this.responseType = responseType;
        builder = new Request.Builder();
        builder.url(url);
        handler = new Handler(Looper.getMainLooper());
        this.url=url;
    }

    public PRequest(String url, PResponseListener<T> responseListener, Class<T> responseType) {
        this(GET, url, responseListener, responseType);
    }

    public PRequest(String url) {
        this(GET, url, null, null);
    }

    /**
     * 添加 header
     * @param key header key
     * @param value header value
     */
    public void addHeader(String key, String value) {
        if(headerMap==null)
            headerMap=new TreeMap<>();
        headerMap.put(key,value);
    }

    public TreeMap<String, String> getHeaders(){
        return headerMap;
    }


    /**
     * 添加POST参数
     * @param key 参数 key
     * @param value 参数 value
     */
    public void addParam(String key, String value) {
        if (params == null)
            params = new TreeMap<>();
        params.put(key, value);
    }

    public TreeMap<String, String> getParams() {
        return params;
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
                bodyBuilder.add(key, value);
            }
            requestBody = bodyBuilder.build();
        }
        builder.headers(Headers.of(headerMap));
        builder.method(method, requestBody);
        builder.tag(this);
        return builder.build();
    }

    /**
     * 取消请求
     */
    public void cancel() {
        if (call != null) {
            call.cancel();
        }
    }


    @Override
    public void onFailure(Call call, final IOException e) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                responseListener.onError(PRequest.this, e);
            }
        });

    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        final String responseText = response.body().string();
        if (responseType == String.class) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    responseListener.onResponse(PRequest.this, (T) responseText);
                }
            });

        } else {
            //
            Gson gson = new Gson();
            final T obj = gson.fromJson(responseText, responseType);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    responseListener.onResponse(PRequest.this, obj);
                }
            });
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
