package com.pocketdigi.plib.util;

import android.graphics.Bitmap;

import com.jakewharton.disklrucache.DiskLruCache;
import com.pocketdigi.plib.core.PApplication;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * DiskLruCache辅助类
 * Created by fhp on 15/3/11.
 */
public class DiskLruCacheHelper {
    private DiskLruCache diskLruCache;
    public DiskLruCacheHelper() {
        try {
            diskLruCache=DiskLruCache.open(PApplication.getInstance().getCacheDir(),RuntimeUtil.getCurrentVersionCode(),1000,10*1024*1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void putObject(String key,Object obj) {
        try {
            DiskLruCache.Editor editor = diskLruCache.edit(key);
            OutputStream outputStream = editor.newOutputStream(0);
            ObjectOutputStream objectOutputStream=new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(obj);
            editor.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Object getObject(String key) {
        try {
            DiskLruCache.Snapshot snapshot = diskLruCache.get(key);
            if(snapshot!=null) {
                InputStream inputStream = snapshot.getInputStream(0);
                ObjectInputStream objectInputStream=new ObjectInputStream(inputStream);
                return objectInputStream.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void delete(String key) {
        try {
            diskLruCache.remove(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void close() {
        try {
            diskLruCache.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
