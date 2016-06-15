package com.pocketdigi.plib.http;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * 上传文件专用body
 * Created by Exception on 16/6/15.
 */
public class UploadBodyWrapper<T> extends RequestBody {
    RequestBody realBody;
    BufferedSink tempSink;
    UploadListener<T> uploadListener;

    public UploadBodyWrapper(RequestBody realBody, UploadListener<T> uploadListener) {
        this.realBody = realBody;
        this.uploadListener = uploadListener;

    }

    @Override
    public MediaType contentType() {
        return realBody.contentType();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        if (tempSink == null) {
            //包装
            tempSink = Okio.buffer(sink(sink));
        }
        //写入
        realBody.writeTo(tempSink);

        //必须调用flush，否则最后一部分数据可能不会被写入
        tempSink.flush();
    }

    private Sink sink(Sink sink) {
        return new ForwardingSink(sink) {
            //当前写入字节数
            long bytesWritten = 0L;
            //总字节长度，避免多次调用contentLength()方法
            long contentLength = 0L;
            long t1 = System.currentTimeMillis(), t2;

            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                if (contentLength == 0) {
                    //获得contentLength的值，后续不再调用
                    contentLength = realBody.contentLength();
                }
                //增加当前写入的字节数
                bytesWritten += byteCount;
                //回调
                if (uploadListener != null) {
                    t2 = System.currentTimeMillis();
                    if (t2 - t1 > 1000) {
                        uploadListener.onUpload(bytesWritten, contentLength);
                        t1 = t2;
                    }
                }
            }
        };
    }

}
