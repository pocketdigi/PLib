package com.pocketdigi.plib.http;

/**
 * Created by Exception on 16/6/12.
 */
public interface PResponseListener<T> {
    void onResponse(PRequest request, T response);
    void onError(PRequest request, Exception e);

}
