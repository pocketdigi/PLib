package com.pocketdigi.plib.http;

import android.support.annotation.NonNull;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Exception on 16/6/14.
 */
public class PUploadRequest<T> extends PRequest<T> {
    String filePath,paramKey;
    UploadListener<T> uploadListener;
    UploadBodyWrapper<T> uploadBodyWrapper;

    /**
     * 上传文件请求
     * @param url
     * @param filePath
     * @param responseType
     */
    public PUploadRequest(@NonNull String url, String paramKey,@NonNull String filePath, UploadListener<T> listener,Class<T> responseType) {
        super(PRequest.POST, url, listener, responseType);
        this.filePath = filePath;
        this.paramKey = paramKey;
        this.uploadListener=listener;
    }


    @Override
    public Request buildRequest() {
        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
        bodyBuilder.setType(MultipartBody.FORM);
        if(params!=null) {
            Set<String> keySet = params.keySet();
            for (String key : keySet) {
                String value = params.get(key);
                bodyBuilder.addFormDataPart(key, value);
            }
        }
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IllegalArgumentException(filePath + "文件不存在");
        }
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(filePath);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        bodyBuilder.addFormDataPart(paramKey, file.getName(), RequestBody.create(MediaType.parse(type), file));
        requestBody = bodyBuilder.build();
        uploadBodyWrapper=new UploadBodyWrapper<T>(requestBody,uploadListener);
        builder.post(uploadBodyWrapper);
        return builder.build();
    }

}
