package com.pocketdigi.plib.core;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;

/**
 * Created by fhp on 14-9-1.
 */
@EFragment
public abstract class PFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PLog.d(this, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        PLog.d(this, "onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        PLog.d(this, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PLog.d(this, "onDestroyView");
    }

    @Override
    public void onDestroy() {
        PLog.d(this, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        PLog.d(this, "onStart");
    }

    @Override
    public void onPause() {
        super.onPause();
        PLog.d(this, "onPause");
        unregisterListerOrReceiver();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerListenerOrReceiver();
    }

    @Override
    public void onStop() {
        super.onStop();
        PLog.d(this, "onStop");
    }

    /**
     * 注册监听器以及接收器(包括Event),在Fragment被隐藏或销毁时，会调用unregisterListerOrReceiver
     */
    protected void registerListenerOrReceiver() {
    }

    /**
     * 解注册监听器及接收器(包括Event)*
     */
    protected void unregisterListerOrReceiver() {
    }
}