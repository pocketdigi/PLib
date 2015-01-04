package com.pocketdigi.plib.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.pocketdigi.plib.core.PApplication;

/**
 * 获取运行时信息，如是否第一次运行，当前版本号等
 * Created by fhp on 14-9-8.
 */
public class RuntimeUtil {
    public static final String PREKEY_FIRSTRUN="firstrun";
    private static boolean isFirstRun=true;
    private static boolean isCurrentVersionFirstRun=true;

    /**
     * 读入配置，只能在启动时调用一次(MainActiviry)
     * 不要在Application中调用，因为退出应用，Application还在，再次启动不会重新读取
     */
    public static void readSetting()
    {
        PreferenceManager manager=PreferenceManager.getDefaultManager();
        isFirstRun=manager.getBoolean(PREKEY_FIRSTRUN,true);
        isCurrentVersionFirstRun=manager.getBoolean(PREKEY_FIRSTRUN+getCurrentVersionCode(),true);
        if(isFirstRun)
            setFirstRun();
        if(isCurrentVersionFirstRun)
            setCurrentVersionFirstRun();
    }
    /**
     * 是否第一次运行
     * @return
     */
    public static boolean isFirstRun()
    {
        return isFirstRun;
    }
    /**
     * 是否当前版本的第一次运行
     * @return
     */
    public static boolean isCurrentVersionFirstRun()
    {
        return isCurrentVersionFirstRun;
    }

    /**
     * 设置非第一次运行
     */
    public static void setFirstRun()
    {
        PreferenceManager manager=PreferenceManager.getDefaultManager();
        manager.putBoolean(PREKEY_FIRSTRUN,false).commit();
    }

    /**
     * 设置当前版本为非第一次运行
     */
    private static void setCurrentVersionFirstRun()
    {
        PreferenceManager manager=PreferenceManager.getDefaultManager();
        manager.putBoolean(PREKEY_FIRSTRUN+getCurrentVersionCode(),false).commit();
    }
    /**
     * 获取当前客户端版本信息
     */
    public static String getCurrentVersionName(){
        String curVersionName = null;
        try {
            PackageInfo info = PApplication.getInstance().getPackageManager().getPackageInfo(PApplication.getInstance().getPackageName(), 0);
            curVersionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        return curVersionName;
    }
    /**
     * 获取版本号
     * @return
     */
    public static int getCurrentVersionCode()
    {
        int versionCode = 0;
        try {
            PackageInfo info = PApplication.getInstance().getPackageManager().getPackageInfo(PApplication.getInstance().getPackageName(), 0);
            versionCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        return versionCode;
    }

}