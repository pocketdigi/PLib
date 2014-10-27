package com.pocketdigi.plib.core;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.StringRes;
import android.widget.Toast;

/**
 * Toast封装类,支持非UI线程直接调用
 * Created by fhp on 14-9-2.
 */
public class PToast {

    public static void show(@StringRes final int resId)
    {
        if(!isMainThread())
        {
            new Handler(Looper.myLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(PApplication.getInstance(),resId,Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(PApplication.getInstance(),resId,Toast.LENGTH_SHORT).show();
        }

    }
    public static void show(final String message)
    {
        if(!isMainThread())
        {
            new Handler(Looper.myLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(PApplication.getInstance(),message,Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(PApplication.getInstance(),message,Toast.LENGTH_SHORT).show();
        }
    }

    private static boolean isMainThread()
    {
        return Looper.myLooper()==Looper.getMainLooper();
    }
}