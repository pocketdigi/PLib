package com.pocketdigi.plib.upload;

import com.pocketdigi.plib.core.PApplication;

import java.lang.ref.WeakReference;

/**
 * 上传进度
 * 传完后，结果用getResponse()获取，再自行解析
 */
public class UploadProgress {
    public final static int STATE_DEFAULT = 0;
    public final static int STATE_WAIT = 1;
    public final static int STATE_UPLOADING = 2;
    public final static int STATE_UPLOADED = 3;
    public final static int STATE_UPLOAD_FAILURE = 4;

    int state;
    //文件已上传大小和文件大小
    long uploadedSize, fileSize;
    int failureType = -1;
    UploadTask uploadTask;
    //上传结束后，服务端的返回，需要调用方自行处理上传结果
    String response;

    public UploadProgress(UploadTask uploadTask) {
        state = STATE_DEFAULT;
        this.uploadTask = uploadTask;
    }

    public int getState() {
        return state;
    }

    /**
     * 修改状态，会发事件通知
     *
     * @param state
     */
    public void setState(int state) {
        if (this.state != state) {
            this.state = state;
            PApplication.getInstance().postEvent(new UploadProgressChangeEvent(this));
        }
    }

    public int getFailureType() {
        return failureType;
    }

    public void setFailureType(int failureType) {
        this.failureType = failureType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public long getUploadedSize() {
        return uploadedSize;
    }

    public void setUploadedSize(long uploadedSize) {
        this.uploadedSize = uploadedSize;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Override
    public boolean equals(Object o) {
        // TODO 自动生成的方法存根
        UploadProgress other = (UploadProgress) o;
        return uploadTask == other.uploadTask;
    }

    public UploadTask getUploadTask() {
        return uploadTask;
    }
}
