package com.pocketdigi.plib.core;

import android.app.Service;

/**
 * Service基类
 * Created by fhp on 14-9-5.
 */
public abstract class PService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
        PApplication.getInstance().serviceCreate(this);
        registerListenerOrReceiver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PApplication.getInstance().serviceDestory(this);
        unregisterListerOrReceiver();
    }
    /**
     * 注册监听器以及接收器(包括Event),在Fragment被隐藏或销毁时，会调用unregisterListerOrReceiver
     */
    protected void registerListenerOrReceiver(){};
    /**解注册监听器及接收器(包括Event)**/
    protected void unregisterListerOrReceiver(){};

}