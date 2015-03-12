package com.pocketdigi.plib.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.pocketdigi.plib.core.PApplication;

/**
 * 设备信息
 * Created by fhp on 14/12/30.
 */
public class DeviceUtils {
    /**
     * 屏幕屏幕尺寸数组，第一个是宽，第二个是高
     *
     * @return
     */
    public static int[] getScreenSize() {
        WindowManager windowManager = (WindowManager) PApplication.getInstance().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int[] size = new int[2];
        size[0] = displayMetrics.widthPixels;
        size[1] = displayMetrics.heightPixels;
        return size;
    }

    /**
     * 获取状态栏高度
     * 原理，取可见区域的top值
     *
     * @param activity
     * @return
     */
    public static int getStatusBarHeight(Activity activity) {
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return rect.top;
    }

    /**
     * 获取设备ID
     *
     * @param context
     * @return
     */
    public static String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * 判断SIM卡是更换，只能与上次调用此方法时对比
     * 如果是平板等没有sim的设备，每次返回都是false
     * @param context
     * @return
     */
    public static boolean isSIMChanged(Context context) {
        TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imsiStr = telManager.getSubscriberId();
        int imsi = 0;
        if (imsiStr != null) {
            imsi = imsiStr.hashCode();
        }
        if (imsi == 0)
            return false;
        int oldIMSI = PreferenceManager.getDefaultManager().getInt("IMSI", 0);
        if (imsi != oldIMSI) {
            PreferenceManager.getDefaultManager().putInt("IMSI", imsi).commit();
            return oldIMSI != 0;
        }
        return false;
    }
}
