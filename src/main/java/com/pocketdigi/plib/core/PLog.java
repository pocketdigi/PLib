package com.pocketdigi.plib.core;

import android.text.TextUtils;
import android.util.Log;

/**
 * Log类，在正式包中不会输出Log
 * Created by fhp on 14-9-2.
 */
public class PLog {
    //在Android Studio里，Library项目一定是release发布的，所以不能再用BuildConfig.DEBUG判断
    //需要在application里的onCreate里，加 PLog.DEBUG=BuildConfig.DEBUG
    public static boolean DEBUG = false;

    public static int v(String tag, String msg) {
        if (TextUtils.isEmpty(msg))
            return 0;
        if (DEBUG)
            return Log.v(tag, msg);
        return 0;
    }

    public static int v(String tag, String msg, Throwable tr) {
        if (TextUtils.isEmpty(msg))
            return 0;
        if (DEBUG)
            return Log.v(tag, msg, tr);
        return 0;
    }

    public static int d(String tag, String msg) {
        if (TextUtils.isEmpty(msg))
            return 0;
        if (DEBUG)
            return Log.d(tag, msg);
        return 0;
    }

    public static int d(String tag, String msg, Throwable tr) {
        if (TextUtils.isEmpty(msg))
            return 0;
        if (DEBUG)
            return Log.d(tag, msg, tr);
        return 0;
    }

    public static int i(String tag, String msg) {
        if (TextUtils.isEmpty(msg))
            return 0;
        if (DEBUG)
            return Log.i(tag, msg);
        return 0;
    }

    public static int i(String tag, String msg, Throwable tr) {
        if (TextUtils.isEmpty(msg))
            return 0;
        if (DEBUG)
            return Log.i(tag, msg, tr);
        return 0;
    }

    public static int w(String tag, String msg) {
        if (TextUtils.isEmpty(msg))
            return 0;
        if (DEBUG)
            return Log.w(tag, msg);
        return 0;
    }

    public static int w(String tag, String msg, Throwable tr) {
        if (TextUtils.isEmpty(msg))
            return 0;
        if (DEBUG)
            return Log.w(tag, msg, tr);
        return 0;
    }

    public static boolean isLoggable(String s, int i) {
        return Log.isLoggable(s, i);
    }

    public static int w(String tag, Throwable tr) {
        if (DEBUG)
            return Log.w(tag, tr);
        return 0;
    }

    public static int e(String tag, String msg) {
        if (TextUtils.isEmpty(msg))
            return 0;
        if (DEBUG)
            return Log.e(tag, msg);
        return 0;
    }

    public static int e(String tag, String msg, Throwable tr) {
        if (TextUtils.isEmpty(msg))
            return 0;
        if (DEBUG)
            return Log.e(tag, msg, tr);
        return 0;
    }

    public static int e(Object obj, String msg) {
        if (TextUtils.isEmpty(msg))
            return 0;
        if (DEBUG)
            return Log.e(obj.getClass().getSimpleName(), msg);
        return 0;
    }

    public static int d(Object obj, String msg) {
        if (TextUtils.isEmpty(msg))
            return 0;
        if (DEBUG)
            return Log.d(obj.getClass().getSimpleName(), msg);
        return 0;
    }

    public static int v(Object obj, String msg) {
        if (TextUtils.isEmpty(msg))
            return 0;
        if (DEBUG)
            return Log.v(obj.getClass().getSimpleName(), msg);
        return 0;
    }

    public static int w(Object obj, String msg) {
        if (TextUtils.isEmpty(msg))
            return 0;
        if (DEBUG)
            return Log.w(obj.getClass().getSimpleName(), msg);
        return 0;
    }

    public static int d(Object obj, Object msgObj) {
        if (DEBUG)
            return Log.d(obj.getClass().getSimpleName(), msgObj.toString());
        return 0;
    }


}