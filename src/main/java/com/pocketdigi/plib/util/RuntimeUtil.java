package com.pocketdigi.plib.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;

import com.pocketdigi.plib.R;
import com.pocketdigi.plib.core.PApplication;

/**
 * 获取运行时信息，如是否第一次运行，当前版本号等
 * Created by fhp on 14-9-8.
 */
public class RuntimeUtil {
    public static final String PREKEY_FIRSTRUN = "firstrun";
    public static final String PREKEY_LASTRUNTIME = "last_run_time";
    private static boolean isFirstRun = true;
    private static boolean isCurrentVersionFirstRun = true;
    //是否今天第一次启动
    private static boolean isTodayFirstRun = false;

    /**
     * 读入配置，只能在启动时调用一次(MainActiviry)
     * 不要在Application中调用，因为退出应用，Application还在，再次启动不会重新读取
     */
    public static void readSetting() {
        PreferenceManager manager = PreferenceManager.getDefaultManager();
        isFirstRun = manager.getBoolean(PREKEY_FIRSTRUN, true);
        isCurrentVersionFirstRun = manager.getBoolean(PREKEY_FIRSTRUN + getCurrentVersionCode(), true);
        if (isFirstRun)
            setFirstRun();
        if (isCurrentVersionFirstRun)
            setCurrentVersionFirstRun();

        long lastRunTime = manager.getLong(PREKEY_LASTRUNTIME, 0);
        isTodayFirstRun = !DateUtils.isToday(lastRunTime);
        manager.putLong(PREKEY_LASTRUNTIME,System.currentTimeMillis()).commit();
    }

    /**
     * 是否第一次运行
     *
     * @return
     */
    public static boolean isFirstRun() {
        return isFirstRun;
    }

    /**
     * 是否当前版本的第一次运行
     *
     * @return
     */
    public static boolean isCurrentVersionFirstRun() {
        return isCurrentVersionFirstRun;
    }

    /**
     * 设置非第一次运行
     */
    public static void setFirstRun() {
        PreferenceManager manager = PreferenceManager.getDefaultManager();
        manager.putBoolean(PREKEY_FIRSTRUN, false).commit();
    }

    /**
     * 设置当前版本为非第一次运行
     */
    private static void setCurrentVersionFirstRun() {
        PreferenceManager manager = PreferenceManager.getDefaultManager();
        manager.putBoolean(PREKEY_FIRSTRUN + getCurrentVersionCode(), false).commit();
    }

    /**
     * 获取当前客户端版本信息
     */
    public static String getCurrentVersionName() {
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
     *
     * @return
     */
    public static int getCurrentVersionCode() {
        int versionCode = 0;
        try {
            PackageInfo info = PApplication.getInstance().getPackageManager().getPackageInfo(PApplication.getInstance().getPackageName(), 0);
            versionCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        return versionCode;
    }

    /**
     * 是否今天第一次运行
     */
    public static boolean isTodayFirstRun() {
        return isTodayFirstRun;
    }

    /**
     * 为程序创建桌面快捷方式
     * @param mainActivity 主Activity
     */
    public static void addShortcut(Activity mainActivity,String appName,int iconResId){
        boolean isInstallShortcut = false;
        final ContentResolver cr = mainActivity.getContentResolver();
        final String AUTHORITY ="com.android.launcher.settings";
        final Uri CONTENT_URI = Uri.parse("content://" +AUTHORITY + "/favorites?notify=true");
        Cursor c = cr.query(CONTENT_URI,new String[] {"title","iconResource" },"title=?",
                new String[] {appName.trim()}, null);
        if(c!=null && c.getCount()>0){
            isInstallShortcut = true ;
        }
        if (c != null) {
            c.close();
        }
        if(!isInstallShortcut) {
            Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
            //快捷方式的名称
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, appName);
            shortcut.putExtra("duplicate", false); //不允许重复创建
            Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
            shortcutIntent.setClassName(mainActivity, mainActivity.getClass().getName());
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
            //快捷方式的图标
            Intent.ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(mainActivity, iconResId);
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
            mainActivity.sendBroadcast(shortcut);
        }
    }


}