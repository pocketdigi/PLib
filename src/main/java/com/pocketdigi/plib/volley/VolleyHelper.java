package com.pocketdigi.plib.volley;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.pocketdigi.plib.core.PApplication;
import com.pocketdigi.plib.core.PLog;
import com.pocketdigi.plib.util.NetWorkUtil;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * Volley帮助类
 * 使用方法 VolleyHelper.getInstance().addRequest(url,class,listener)
 * Created by fhp on 14-9-14.
 */
public class VolleyHelper {
    private static VolleyHelper ourInstance = new VolleyHelper();
    private RequestQueue requestQueue;
    private HashMap<String, WeakReference<ResponseListener>> listenerHashMap;

    public static VolleyHelper getInstance() {
        return ourInstance;
    }

    private VolleyHelper() {
        requestQueue = Volley.newRequestQueue(PApplication.getInstance());
        listenerHashMap = new HashMap<String, WeakReference<ResponseListener>>();
    }

    /**
     * 使用单独的队列发送请求
     * @param yourRequestQueue
     * @param url
     * @param clazz
     * @param listener
     * @return
     */
    public int addRequest(RequestQueue yourRequestQueue,final String url, final Class clazz, ResponseListener listener)
    {
        RequestQueue queue=yourRequestQueue;
        if(queue==null) {
            queue = requestQueue;
        }
        PLog.d(this,listener.getClass().getSimpleName()+" Request "+url);
        listenerHashMap.put(url, new WeakReference<ResponseListener>(listener));
        if (NetWorkUtil.isNetConnected()) {
            Request<String> request= queue.add(new StringRequest(url, new Response.Listener<String>() {
                @Override
                public void onResponse(Request request,String response,boolean isFromCache) {
                    PLog.d(this," Request "+url+" success \n "+response);
                    Gson gson = new Gson();
                    WeakReference<ResponseListener> wr = listenerHashMap.get(url);
                    if (wr != null) {
                        ResponseListener responseListener = wr.get();
                        if (responseListener != null) {
                            try {
                                Object obj = gson.fromJson(response, clazz);
                                responseListener.onSuccess(obj,isFromCache);
                            } catch (Exception e) {
                                e.printStackTrace();
                                responseListener.onServerError();
                            }
                        }
                    }
                    listenerHashMap.remove(url);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(Request request,VolleyError error) {
                    PLog.d(this," Request "+url+" error \n "+error.toString());
                    WeakReference<ResponseListener> wr = listenerHashMap.get(url);
                    if (wr != null) {
                        ResponseListener responseListener = wr.get();
                        if (responseListener != null)
                            responseListener.onNetWorkError();
                    }
                    listenerHashMap.remove(url);
                }
            }));
            return request.getSequence();
        }
        listenerHashMap.remove(url);
        return 0;
    }

    /**
     * 使用默认队列发送请求
     * @param url
     * @param clazz
     * @param listener
     * @return
     */
    public int addRequest(final String url, final Class clazz, ResponseListener listener) {
        return addRequest(null,url,clazz,listener);
    }
}
