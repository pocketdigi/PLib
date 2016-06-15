package com.pocketdigi.plib.http;

import com.pocketdigi.plib.core.PLog;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by Exception on 16/6/12.
 */
public class PNetworkInterceptor implements Interceptor {
    DownProgressListener downProgressListener = new DownProgressListener() {

        @Override
        public void update(long bytesRead, long contentLength, boolean done) {
            PLog.e(this,"bytesRead = [" + bytesRead + "], contentLength = [" + contentLength + "], done = [" + done + "]");
            System.out.format("%d%% done\n", (100 * bytesRead) / contentLength);
        }
    };

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        Response.Builder bodyBuilder = originalResponse.newBuilder().body(new ProgressResponseBody(originalResponse.body(), downProgressListener));
        return bodyBuilder.build();
    }
}
