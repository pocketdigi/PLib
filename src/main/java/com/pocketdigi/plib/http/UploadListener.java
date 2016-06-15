package com.pocketdigi.plib.http;

/**
 * 上传进度监听
 * Created by Exception on 16/6/15.
 */
public interface UploadListener<T> extends PResponseListener<T>{
    void onUpload(long uploadBytes,long totalLength);
}
