package com.pocketdigi.plib.http;

import android.support.annotation.NonNull;

import com.pocketdigi.plib.util.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 下载文件的Request,暂不支持断点续传
 * Created by Exception on 16/6/12.
 */
public class PDownFileRequest extends PRequest<File> {
    String savePath;
    File tmpFile;
    DownProgressListener downProgressListener;

    public PDownFileRequest(@NonNull String url, @NonNull String savePath, DownProgressListener downProgressListener) {
        this(PRequest.GET, url, null, downProgressListener);
        this.savePath = savePath;

    }

    public PDownFileRequest(@METHOD String method, String url, PResponseListener<File> responseListener, DownProgressListener downProgressListener) {
        super(method, url, responseListener, null);
        this.downProgressListener = downProgressListener;
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        InputStream in = response.body().byteStream();
        tmpFile = new File(savePath + ".tmp");
        FileOutputStream fileOutputStream = new FileOutputStream(tmpFile);
        byte[] buffer = new byte[2048];
        int len = 0;
        long totalLen = Long.parseLong(response.header("Content-Length"));
        long downloadLength = 0;
        long t1 = System.currentTimeMillis();
        long t2 = 0;
        while (!call.isCanceled() && (len = in.read(buffer)) > 0) {
            fileOutputStream.write(buffer, 0, len);
            downloadLength += len;
            t2 = System.currentTimeMillis();
            //间隔超过1s再发通知
            if ((t2 - t1) > 1000) {
                System.out.println("间隔:" + (t2 - t1));
                t1 = t2;
                downProgressListener.update(downloadLength, totalLen, false);
            }
        }
        if (call.isCanceled()) {
            if (tmpFile != null) {
                tmpFile.delete();
            }
        } else {
            //下载完成，重命名
            FileUtils.rename(savePath + ".tmp", savePath);
        }
        downProgressListener.update(downloadLength, totalLen, downloadLength == totalLen);
        fileOutputStream.close();
        response.body().close();
    }

}
