package com.pocketdigi.plib.volley;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.pocketdigi.plib.core.PLog;

import java.util.LinkedList;

/**
 * 读取图片的请求，可能从缓存或网络读取
 * Created by fhp on 14/11/3.
 */
public class ReadImageRequest {
    public final LinkedList<ImageLoader.ImageContainer> mContainers = new LinkedList<ImageLoader.ImageContainer>();
    String cacheKey;
    private Bitmap mCacheBitmap;

    /**
     *
     * @param container 返回给调用者的ImageContainer，包含Bitmap
     * @param cacheKey 缓存的key
     */
    public ReadImageRequest(ImageLoader.ImageContainer container, String cacheKey) {
        this.cacheKey = cacheKey;
        mContainers.add(container);
        PLog.d(this,"开始读图"+cacheKey);
    }
    /**
     *
     * Adds another ImageContainer to the list of those interested in the results of
     * the request.
     */
    public void addContainer(ImageLoader.ImageContainer container) {
        mContainers.add(container);
    }
    /**
     * 删除
     * @param container The container to remove from the list
     * @return True if the request was canceled, false otherwise.
     */
    public boolean removeContainer(ImageLoader.ImageContainer container) {
        return mContainers.remove(container);
    }

    public Bitmap getCacheBitmap() {
        return mCacheBitmap;
    }
    public void setCacheBitmap(Bitmap mCacheBitmap) {
        this.mCacheBitmap = mCacheBitmap;
    }

    /**
     * 请求结束后在UI线程分发结果
     */
    public void deliver()
    {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                PLog.d(this,"读图结束 开始分发"+cacheKey);
                for (ImageLoader.ImageContainer container : mContainers) {
                    if (container.mListener == null) {
                        continue;
                    }
                    container.mBitmap = mCacheBitmap;
                    container.mListener.onResponse(container, false);
                }
                PLog.d(this,"读图结束 分发结束"+cacheKey);
            }
        });
    }
    public void deliverError(final VolleyError error) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                PLog.d(this,"读图结束 开始分发"+cacheKey);
                for (ImageLoader.ImageContainer container : mContainers) {
                    if (container.mListener == null) {
                        continue;
                    }
                    container.mBitmap = mCacheBitmap;
                    container.mListener.onErrorResponse(null, error);
                }
                PLog.d(this,"读图结束 分发结束"+cacheKey);
            }
        });
    }
}
