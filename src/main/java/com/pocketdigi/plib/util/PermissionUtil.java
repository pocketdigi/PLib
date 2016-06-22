package com.pocketdigi.plib.util;

import android.Manifest;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.os.Build;
import android.support.v4.content.ContextCompat;

import com.pocketdigi.plib.core.PApplication;
import com.pocketdigi.plib.core.PFragment;

import java.util.ArrayList;

/**
 * Android 6.0 权限util
 * Created by Exception on 16/6/22.
 */
public class PermissionUtil {
    private static PermissionUtil instance;
    PackageInfo packageInfo;
    public static final int REQUEST_CODE_ALL=1;
    public static PermissionUtil getInstance() {
        if (instance == null)
            instance = new PermissionUtil();
        return instance;
    }

    private PermissionUtil() {
        PackageManager pm= PApplication.getInstance().getPackageManager();
        try {
            packageInfo = pm.getPackageInfo(PApplication.getInstance().getPackageName(), PackageManager.GET_PERMISSIONS);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
    /**
     * 检查所有权限
     * @param fragment
     */
    public void checkPermissions(PFragment fragment){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] requestedPermissions = packageInfo.requestedPermissions;
            ArrayList<String> permissionsNotGranted = new ArrayList<>();
            for (String permission : requestedPermissions) {
                if (ContextCompat.checkSelfPermission(fragment.getActivity(), permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    permissionsNotGranted.add(permission);
                }
            }
            if (permissionsNotGranted.size() > 0) {
                String[] permissionsNotGrantedArray=new String[permissionsNotGranted.size()];
                permissionsNotGranted.toArray(permissionsNotGrantedArray);
                fragment.requestPermissions(permissionsNotGrantedArray,
                        REQUEST_CODE_ALL);
            }
        }
    }

}
