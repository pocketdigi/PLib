package com.pocketdigi.plib.network;

/**
 * Http请求
 * Created by fhp on 14-9-12.
 */
public abstract class HttpRequest {
    public static final int METHOD_POST=1;
    public static final int METHOD_GET=0;
    public int method=METHOD_GET;
    public boolean needCache=true;

}