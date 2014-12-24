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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 异步读本地缓存的ImageLoader
 * Created by fhp on 14/11/3.
 */
public class AsyncImageLoader extends ImageLoader{
    /**
     * 在取的请求，可能没取到
     */
    ConcurrentHashMap<String, ReadImageRequest> readImageRequestConcurrentHashMap = new ConcurrentHashMap<String, ReadImageRequest>();
    // 读数据线程池，限制两个线程
    private ExecutorService readExecutorService = new ThreadPoolExecutor(0, 2, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    //UI线程的Handler
    Handler mainHandler;
    private static AsyncImageLoader instance;
    //独立请求列队
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

        ImageContainer imageContainer = new ImageContainer(null, requestUrl, cacheKey, imageListener);
        ReadImageRequest readImageRequest =readImageRequestConcurrentHashMap.get(cacheKey);

        if(readImageRequest ==null){
            readImageRequest =new ReadImageRequest(imageContainer, cacheKey);
            readImageRequestConcurrentHashMap.put(cacheKey, readImageRequest);
            //去读缓存，读不到会自动转到请求网络
            readExecutorService.execute(new ReadCache(imageContainer, cacheKey,maxWidth,maxHeight));
        }else{
            //如果该请求已经存在，添加ImageContainer，不再发请求
            readImageRequest.addContainer(imageContainer);
        }
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
     * 读取缓存，读不到会转发给网络
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
                ReadImageRequest cacheRequest = readImageRequestConcurrentHashMap.get(cacheKey);
                if (cacheRequest != null) {
                    cacheRequest.setCacheBitmap(cachedBitmap);
                    readSuccess(cacheKey);
                }
            } else {
                // 读不到缓存，去下载
                mainHandler.post(new GetImageUseNetWork(container,cacheKey,maxWidth,maxHeight));
            }

        }
    }

    /**
     * 读取缓存或下载图片成功，分发结果
     * @param cacheKey
     */
    private void readSuccess(String cacheKey)
    {
        ReadImageRequest successedCacheRequest = readImageRequestConcurrentHashMap.remove(cacheKey);
        if(successedCacheRequest!=null) {
            successedCacheRequest.deliver();
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
                public void onResponse(Request request,Bitmap response,boolean isFromCache) {
                    Bitmap bmpCompressed=ImageUtil.scaleBitmap(response, maxWidth, maxHeight);

                    ReadImageRequest cacheRequest = readImageRequestConcurrentHashMap.get(cacheKey);
                    if (cacheRequest != null) {
                        cacheRequest.setCacheBitmap(bmpCompressed);
                        //放到缓存里
                        mCache.putBitmap(cacheKey, bmpCompressed);
                        readSuccess(cacheKey);
                    }

                }
            }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(Request request,VolleyError error) {
                    onGetImageError(cacheKey, error);
                }
            });
            mInFlightRequests.put(cacheKey, new BatchedImageRequest(newRequest, imageContainer));
            mRequestQueue.add(newRequest);
        }

    }

    /**
     * 清除缓存
     */
    public void clearCache()
    {
        mCache.clear();
    }
}
