package com.pocketdigi.plib.volley;

/**
 * Created by fhp on 14-9-12.
 */
public interface ResponseListener{
    /**成功**/
    public void onSuccess(Object response);
    /**服务器错误**/
    public void onServerError();
    /**网络错误**/
    public void onNetWorkError();
}