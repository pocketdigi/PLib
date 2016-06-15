package com.pocketdigi.plib.http;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by Exception on 16/6/8.
 */
public class PInterceptor implements Interceptor{
    @Override
    public Response intercept(Chain chain) throws IOException {
        return chain.proceed(chain.request());
    }
}
