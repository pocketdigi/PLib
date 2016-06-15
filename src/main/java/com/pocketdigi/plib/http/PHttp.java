package com.pocketdigi.plib.http;

import com.pocketdigi.plib.core.PApplication;
import com.pocketdigi.plib.util.RuntimeUtil;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Exception on 16/6/8.
 */
public class PHttp {
    private static PHttp instance;
    PInterceptor interceptor;
    OkHttpClient okHttpClient;
    Cache cache;
    //缓存 10m
    static final long maxSizeOfCache=10*1024*1024;
    static final long connectTimeOut=10;
    static final long readTimeOut=20;
    public static PHttp getInstance() {
        if (instance == null)
            instance = new PHttp();
        return instance;
    }

    private PHttp() {
        interceptor=new PInterceptor();
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.addInterceptor(interceptor);
        String cacheDir = RuntimeUtil.getContextCacheDir(PApplication.getInstance());
        File cacheDirFile=new File(cacheDir+"/phttp");
        if(!cacheDirFile.exists()){
            cacheDirFile.mkdir();
        }
        cache=new Cache(cacheDirFile,maxSizeOfCache);
        httpClientBuilder.cache(cache);
        httpClientBuilder.connectTimeout(connectTimeOut, TimeUnit.MINUTES);
        httpClientBuilder.readTimeout(readTimeOut, TimeUnit.MINUTES);
        httpClientBuilder.retryOnConnectionFailure(true);
//        httpClientBuilder.addInterceptor(new PNetworkInterceptor());
        okHttpClient = httpClientBuilder.build();
    }

    public void addRequest(final PRequest request){
        Request realRequest = request.buildRequest();
        Call call=okHttpClient.newCall(realRequest);
        call.enqueue(request);
        request.setCall(call);
    }

}
