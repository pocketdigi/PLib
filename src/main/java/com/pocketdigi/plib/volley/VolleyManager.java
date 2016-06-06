package com.pocketdigi.plib.volley;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.pocketdigi.plib.core.PApplication;

/**
 * Created by Exception on 16/6/3.
 */
public class VolleyManager {
    private static VolleyManager instance;
    RequestQueue requestQueue;
    public static VolleyManager getInstance() {
        if (instance == null)
            instance = new VolleyManager();
        return instance;
    }

    private VolleyManager() {
        requestQueue= Volley.newRequestQueue(PApplication.getInstance());
        requestQueue.start();
    }

    public <T> void addRequest(Request<T> request) {
        requestQueue.add(request);
    }



}
