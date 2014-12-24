package com.pocketdigi.plib.upload;

import java.util.Map;

/**
 * Created by fhp on 14/12/22.
 */
public class UploadTask {
    //待上传文件路径，参数名
    String filePath,fileArgName;
    //其他参数
    Map<String,String> otherParams,headers;
    String apiUrl;
    byte[] fileBytes;
    boolean isCancel;
    //如果该api需要登录，传入sessionId

    public UploadTask(String apiUrl,String fileArgName) {
        this.fileArgName = fileArgName;
        this.apiUrl=apiUrl;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileArgName() {
        return fileArgName;
    }

    public Map<String, String> getOtherParams() {
        return otherParams;
    }

    public void setOtherParams(Map<String, String> otherParams) {
        this.otherParams = otherParams;
    }

    /**
     * 是否取消
     * @return
     */
    public boolean isCancel() {
        return isCancel;
    }

    public void setCancel(boolean isCancel) {
        this.isCancel = isCancel;
    }

    public String getApiUrl() {
        return apiUrl;
    }
    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public byte[] getFileBytes() {
        return fileBytes;
    }

    public void setFileBytes(byte[] fileBytes) {
        this.fileBytes = fileBytes;
    }
}
