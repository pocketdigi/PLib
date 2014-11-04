package com.pocketdigi.plib.volley;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.pocketdigi.plib.core.PApplication;
import com.pocketdigi.plib.util.ImageUtil;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 异步的ImageLoader
 * Created by fhp on 14/11/3.
 */
public class AsyncImageLoader extends ImageLoader{
    /**
     * 在取的，可能没取到
     */
    HashMap<String, ReadCacheRequest> cacheRequestMap = new HashMap<String, ReadCacheRequest>();
    /** 已经取到的bmp缓存 **/
    HashMap<String, ReadCacheRequest> cacheMap = new HashMap<String, ReadCacheRequest>();
    // 读数据线程池，限制两个线程
    private ExecutorService readExecutorService = new ThreadPoolExecutor(0, 2, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    //UI线程的Handler
    Handler mainHandler;
    //读完后分发
    private Runnable deliveryRunnable;

    private static AsyncImageLoader instance;
    private static RequestQueue requestQueue;
    private AsyncImageLoader(RequestQueue queue, ImageCache imageCache) {
        super(queue, imageCache);
        mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 返回默认的ImageLoader,使用两级缓存，单独的请求队列
     * @return
     */
    public static AsyncImageLoader getDefaultImageLoader()
    {
        if(instance==null) {
            requestQueue=Volley.newRequestQueue(PApplication.getInstance());
            requestQueue.start();
            instance = new AsyncImageLoader(requestQueue, new L2LRUImageCache(PApplication.getInstance()));
        }
        return instance;
    }

    /**
     * 销毁，停止所有未处理请求
     */
    public void destory()
    {
        requestQueue.stop();
        instance=null;
    }

    @Override
    public ImageContainer get(String requestUrl, ImageListener imageListener, int maxWidth, int maxHeight) {
        // TODO Auto-generated method stub
        throwIfNotOnMainThread();
        final String cacheKey = getCacheKey(requestUrl, maxWidth, maxHeight);
        // The bitmap did not exist in the cache, fetch it!
        ImageContainer imageContainer = new ImageContainer(null, requestUrl, cacheKey, imageListener);
        ReadCacheRequest readCacheRequest=cacheRequestMap.get(cacheKey);
        if(readCacheRequest==null){
            readCacheRequest=new ReadCacheRequest(imageContainer, cacheKey);
            cacheRequestMap.put(cacheKey, readCacheRequest);
        }else{
            readCacheRequest.addContainer(imageContainer);
        }

        imageListener.onResponse(imageContainer, true);
        readExecutorService.execute(new ReadCache(imageContainer, cacheKey,maxWidth,maxHeight));
        return imageContainer;
    }

    private void throwIfNotOnMainThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("ImageLoader must be invoked from the main thread.");
        }
    }

    /**
     * 创建缓存的key
     *
     * @param url
     * @param maxWidth
     * @param maxHeight
     * @return
     */
    private static String getCacheKey(String url, int maxWidth, int maxHeight) {
        return new StringBuilder(url.length() + 12).append("#W").append(maxWidth).append("#H").append(maxHeight)
                .append(url).toString();
    }

    /**
     * 读取缓存
     */
    class ReadCache implements Runnable {
        ImageContainer container;
        String cacheKey;
        int maxWidth, maxHeight;
        public ReadCache(ImageContainer container, String cacheKey,int maxWidth,int maxHeight) {
            this.container = container;
            this.cacheKey = cacheKey;
            this.maxWidth=maxWidth;
            this.maxHeight=maxHeight;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            Bitmap cachedBitmap = mCache.getBitmap(cacheKey);
            if (cachedBitmap != null) {
                ReadCacheRequest cacheRequest = cacheRequestMap.get(cacheKey);
                if (cacheRequest != null) {
                    cacheRequest.setCacheBitmap(cachedBitmap);
                    mainHandler.post(new ReadCacheSuccess(cacheKey));
                }
            } else {
                // 读不到缓存，去下载
                cacheRequestMap.remove(cacheKey);
                mainHandler.post(new GetImageUseNetWork(container,cacheKey,maxWidth,maxHeight));
            }

        }
    }

    /**
     * 成功读取缓存，分发
     *
     */
    class ReadCacheSuccess implements Runnable {
        String cacheKey;

        public ReadCacheSuccess(String cacheKey) {
            this.cacheKey = cacheKey;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            ReadCacheRequest successedCacheRequest = cacheRequestMap.remove(cacheKey);
            cacheMap.put(cacheKey, successedCacheRequest);
            if (deliveryRunnable == null) {
                deliveryRunnable = new Runnable() {
                    @Override
                    public void run() {
                        for (ReadCacheRequest cacheRequest : cacheMap.values()) {
                            if (cacheRequest != null) {
                                for (ImageContainer container : cacheRequest.mContainers) {
                                    if (container.mListener == null) {
                                        continue;
                                    }
                                    container.mBitmap = cacheRequest.getCacheBitmap();
                                    container.mListener.onResponse(container, false);
                                }
                            }
                        }
                        cacheMap.clear();
                        deliveryRunnable = null;
                    }
                };
                mainHandler.postDelayed(deliveryRunnable, 100);
            }
        }

    }

    class GetImageUseNetWork implements Runnable {
        ImageContainer imageContainer;
        String cacheKey;
        int maxWidth,maxHeight;
        public GetImageUseNetWork(ImageContainer imageContainer, String cacheKey,int maxWidth,int maxHeight) {
            this.imageContainer = imageContainer;
            this.cacheKey = cacheKey;
            this.maxWidth=maxWidth;
            this.maxHeight=maxHeight;
        }

        @Override
        public void run() {
            BatchedImageRequest request = mInFlightRequests.get(cacheKey);
            if (request != null) {
                // If it is, add this request to the list of listeners.
                request.addContainer(imageContainer);
            }

            // The request is not already in flight. Send the new request to the network and
            // track it.
            Request<?> newRequest = new ImageRequest(imageContainer.getRequestUrl(), new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response,boolean isFromCache) {
                    Bitmap bmpCompressed=ImageUtil.compressBitmap(response,maxWidth,maxHeight);
                    onGetImageSuccess(cacheKey, bmpCompressed);
                }
            }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    onGetImageError(cacheKey, error);
                }
            });
            mInFlightRequests.put(cacheKey, new BatchedImageRequest(newRequest, imageContainer));
            mRequestQueue.add(newRequest);
        }

    }
}
