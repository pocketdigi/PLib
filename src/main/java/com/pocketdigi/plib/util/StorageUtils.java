package com.pocketdigi.plib.util;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;

/**
 * 存储util
 * Created by fhp on 14-9-19.
 */
public class StorageUtils {
    /**
     * 获取外部存储可用空间大小，如果未挂载，返回0
     * @return
     */
    public static long getExternalStorageAvailableSize()
    {
        long blockSize=0;
        long availableBlocks=0;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                File path = Environment.getExternalStorageDirectory();
                StatFs stat = new StatFs(path.getPath());
                if (Build.VERSION.SDK_INT >= 18) {
                    blockSize = stat.getBlockSizeLong();
                    availableBlocks = stat.getAvailableBlocksLong();
                } else {
                    blockSize = stat.getBlockSize();
                    availableBlocks = stat.getAvailableBlocks();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return availableBlocks * blockSize;
    }

    public static long getInternalStorageAvailableSize(Context context) {
        long blockSize=0;
        long availableBlocks=0;
        try {
            File path = context.getCacheDir();
            StatFs stat = new StatFs(path.getPath());
            if (Build.VERSION.SDK_INT >= 18) {
                blockSize = stat.getBlockSizeLong();
                availableBlocks = stat.getAvailableBlocksLong();
            } else {
                blockSize = stat.getBlockSize();
                availableBlocks = stat.getAvailableBlocks();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return availableBlocks * blockSize;
    }

}