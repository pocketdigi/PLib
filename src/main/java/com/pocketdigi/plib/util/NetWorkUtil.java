package com.pocketdigi.plib.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.pocketdigi.plib.core.PApplication;

public class NetWorkUtil {
    /**
     * 判断当前是否已连接网络
     * @return
     */
    public static boolean isNetConnected()
    {
        ConnectivityManager connectMgr = (ConnectivityManager) PApplication.getInstance()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectMgr.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }
    /**
     * 判断当前是否WIFI连接，如果没有连接或者非WIFI,返回false
     * @return
     */
    public static boolean isWifiConnected()
    {
        ConnectivityManager connectMgr = (ConnectivityManager) PApplication.getInstance()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectMgr.getActiveNetworkInfo();
        if(info!=null&&info.isConnected()&&info.getType()==ConnectivityManager.TYPE_WIFI)
            return true;
        return false;
    }
}
