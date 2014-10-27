package com.pocketdigi.plib.core;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.os.Bundle;
import de.greenrobot.event.EventBus;

import java.util.ArrayList;
import java.util.concurrent.*;

/**
 * Application基类
 * Created by fhp on 14-8-28.
 */
public abstract class PApplication extends Application{
    private static PApplication instance;
    private ArrayList<Activity> activities;
    private ArrayList<Service> services;
    /**
     * 在隐藏后台时保存参数
     */
    Bundle instanceState;

    public static PApplication getInstance()
    {
        return instance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        activities=new ArrayList<Activity>();
        services=new ArrayList<Service>();
        instance=this;
        instanceState=new Bundle();
    }

    void activityCreate(Activity activity)
    {
        activities.add(activity);
    }
    void serviceCreate(Service service)
    {
        services.add(service);
    }
    void activityDestory(Activity activity)
    {
        activities.remove(activity);
    }
    void serviceDestory(Service service)
    {
        services.remove(service);
    }

    /**
     * 退出所有Activity
     */
    public void destoryAllActivity()
    {
        for (Activity activity:activities)
        {
            activity.finish();
        }
        activities.clear();
    }

    /**
     * 退出整个应用,会关闭Activity和Service
     */
    public void exit()
    {
        destoryAllActivity();
        destoryAllService();
        System.exit(0);
    }

    public void destoryAllService()
    {
        for (Service service:services)
        {
            service.stopSelf();
        }
        services.clear();
    }

    /**
     * 注册事件接收者，封装是为了以后使用其他事件总线
     * @param obj
     */
    public void registerEventSubscriber(Object obj)
    {
        EventBus.getDefault().register(obj);
    }
    public void unregisterEventSubscriber(Object obj)
    {
        EventBus.getDefault().unregister(obj);
    }
    public void postEvent(PEvent event)
    {
        EventBus.getDefault().post(event);
    }

    public void putState(String key,String value)
    {
        instanceState.putString(key,value);
    }
    public void putState(String key,int value)
    {
        instanceState.putInt(key,value);
    }
    public void putState(String key,boolean value)
    {
        instanceState.putBoolean(key,value);
    }
    public void putState(String key,Bundle value)
    {
        instanceState.putBundle(key,value);
    }
    public String getStringState(String key)
    {
        return instanceState.getString(key);
    }
    public int getIntState(String key)
    {
        return instanceState.getInt(key);
    }
    public boolean getBooleanState(String key)
    {
        return instanceState.getBoolean(key);
    }
}