package com.pocketdigi.plib.volley;

import android.graphics.Bitmap;

import com.android.volley.toolbox.ImageLoader;

import java.util.LinkedList;

/**
 * Created by fhp on 14/11/3.
 */
public class ReadCacheRequest {
    public final LinkedList<ImageLoader.ImageContainer> mContainers = new LinkedList<ImageLoader.ImageContainer>();
    String cacheKey;
    private Bitmap mCacheBitmap;
    /**
     * Constructs a new BatchedImageRequest object
     * @param request The request being tracked
     * @param container The ImageContainer of the person who initiated the request.
     */
    public ReadCacheRequest(ImageLoader.ImageContainer container,String cacheKey) {
        this.cacheKey = cacheKey;
        mContainers.add(container);
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
}
