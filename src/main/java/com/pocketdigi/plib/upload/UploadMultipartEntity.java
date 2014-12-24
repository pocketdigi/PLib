package com.pocketdigi.plib.upload;

import com.pocketdigi.plib.core.PApplication;

import org.apache.http.entity.mime.MultipartEntity;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 上传文件，处理进度,每秒报告一次进度
 * Created by fhp on 14/12/23.
 */
public class UploadMultipartEntity extends MultipartEntity {
    UploadProgress progress;

    public UploadMultipartEntity(UploadProgress progress) {
        super();
        this.progress = progress;
    }

    @Override
    public void writeTo(OutputStream outstream) throws IOException {
        super.writeTo(new CountingOutputStream(outstream));
    }

    public void setProgress(UploadProgress progress) {
        this.progress = progress;
    }

    public class CountingOutputStream extends FilterOutputStream {
        private long transferred;
        long t1;

        public CountingOutputStream(OutputStream out) {
            super(out);
            this.transferred = 0;
            t1 = System.currentTimeMillis();
        }

        public void write(byte[] b, int off, int len) throws IOException {
            out.write(b, off, len);
            this.transferred += len;
            progress.setUploadedSize(transferred);
            reportProgress();
        }

        public void write(int b) throws IOException {
            out.write(b);
            this.transferred++;
            progress.setUploadedSize(transferred);
            reportProgress();
        }

        private void reportProgress() {
            long t2 = System.currentTimeMillis();
            if (t2 - t1 > 1000) {
                t1 = t2;
                PApplication.getInstance().postEvent(new UploadProgressChangeEvent(progress));
            }
        }
    }
}
