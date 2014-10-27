package com.pocketdigi.plib.core;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 *
 * Created by fhp on 14-9-1.
 */
public abstract class PFragmentActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PApplication.getInstance().activityCreate(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerListenerOrReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterListerOrReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PApplication.getInstance().activityDestory(this);
    }
    /**
     * 注册监听器以及接收器(包括Event),在Fragment被隐藏或销毁时，会调用unregisterListerOrReceiver
     */
    protected void registerListenerOrReceiver(){};
    /**解注册监听器及接收器(包括Event)**/
    protected void unregisterListerOrReceiver(){};

}