package com.pocketdigi.plib.volley;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;
import com.jakewharton.disklrucache.DiskLruCache;
import com.pocketdigi.plib.core.PApplication;
import com.pocketdigi.plib.util.MD5Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 二级图片缓存,如果存储卡不可用，只有一级
 * Created by fhp on 14/11/3.
 */
public class L2LRUImageCache implements ImageLoader.ImageCache{
    LruCache<String, Bitmap> lruCache;
    DiskLruCache diskLruCache;
    final int RAM_CACHE_SIZE = 10 * 1024 * 1024;
    String DISK_CACHE_DIR = "imageCache";
    final long DISK_MAX_SIZE = 20 * 1024 * 1024;

    public L2LRUImageCache(Context context) {
        this.lruCache = new LruCache<String, Bitmap>(RAM_CACHE_SIZE) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };

        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {

            File cacheDir = context.getExternalFilesDir(DISK_CACHE_DIR);;
            if(!cacheDir.exists())
            {
                cacheDir.mkdir();
            }
            try {
                diskLruCache = DiskLruCache.open(cacheDir, 1, 1, DISK_MAX_SIZE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Bitmap getBitmap(String url) {
        String key=generateKey(url);
        Bitmap bmp = lruCache.get(key);
        if (bmp == null) {
            bmp = getBitmapFromDiskLruCache(key);
            //从磁盘读出后，放入内存
            if(bmp!=null)
            {
                lruCache.put(key,bmp);
            }
        }
        return bmp;
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        String key=generateKey(url);
        lruCache.put(key, bitmap);
        putBitmapToDiskLruCache(key,bitmap);
    }

    private void putBitmapToDiskLruCache(String key, Bitmap bitmap) {
        if(diskLruCache!=null) {
            try {
                DiskLruCache.Editor editor = diskLruCache.edit(key);
                if (editor != null) {
                    OutputStream outputStream = editor.newOutputStream(0);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
                    editor.commit();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap getBitmapFromDiskLruCache(String key) {
        if(diskLruCache!=null) {
            try {
                DiskLruCache.Snapshot snapshot = diskLruCache.get(key);
                if (snapshot != null) {
                    InputStream inputStream = snapshot.getInputStream(0);
                    if (inputStream != null) {
                        Bitmap bmp = BitmapFactory.decodeStream(inputStream);
                        inputStream.close();
                        return bmp;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 因为DiskLruCache对key有限制，只能是[a-z0-9_-]{1,64},所以用md5生成key
     * @param url
     * @return
     */
    private String generateKey(String url)
    {
        return MD5Utils.getMD532(url);
    }
}
