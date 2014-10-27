package com.pocketdigi.plib.download;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import com.pocketdigi.plib.core.PApplication;
import com.pocketdigi.plib.core.PLog;
import com.pocketdigi.plib.util.NetWorkUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 下载管理器，需要使用Service来统一管理
 * 不会存储下载状态，暂停需要重新addTask
 * 请自行注册监听器保存状态
 * 使用方法，DownloadManager.getInstance().addTask(task),在下载状态和进度改变时，DownloadListener会接收到回调，但没改变不会发通知
 * 所以，需要调用者在Service中，getTaskList(),addListener(),再根据listener的回调，修改下载数据库
 * Created by fhp on 14-9-16.
 */
public class DownloadManager implements DownloadListener {
    private static DownloadManager instance;
    BlockingQueue<Runnable> mDownWorkQueue;
    int corePoolSize = 1;
    int maximumPoolSize = 2;
    AtomicInteger atomicInteger = new AtomicInteger();
    ThreadPoolExecutor executor;
    HashSet<DownloadListener> listeners;
    ArrayList<DownTask> taskList;
    public static int CONNECT_TIMEOUT=15000;
    public static int READ_TIMEOUT=25000;

    boolean isFirstFailure = true;
    /**
     * 仅在wifi时下载
     */
    public boolean wifiOnly=false;
    WifiStateReceiver wifiReceiver;
    public static DownloadManager getInstance() {
        if (instance == null)
            instance = new DownloadManager();
        return instance;
    }

    private DownloadManager() {
        listeners = new HashSet<DownloadListener>();
        taskList = new ArrayList<DownTask>();
        mDownWorkQueue = new LinkedBlockingQueue<Runnable>();
        executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 60L, TimeUnit.SECONDS, mDownWorkQueue);

        wifiReceiver=new WifiStateReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        PApplication.getInstance().registerReceiver(wifiReceiver, filter);
    }

    public DownTask addTask(DownTask task) {
        PLog.d(this,"添加下载任务"+task.getUrl());
        //判断任务是否存在
        int taskIndex;
        if ((taskIndex = taskList.indexOf(task)) > -1) {
            //存在
            return taskList.get(taskIndex);
        }
        //如果没有wifi,但仅能wifi下载，且任务是阻塞的，不下载
        if(wifiOnly&&!NetWorkUtil.isWifiConnected()&&task.isBlock())
        {
            task.setState(DownTask.STATE_PAUSED);
            return task;
        }
        task.setState(DownTask.STATE_WAITING);
        task.setId(atomicInteger.incrementAndGet());
        DownRunnable runnable = new DownRunnable(task, this);
        if (task.isBlock()) {
            executor.submit(runnable);
        } else {
            new Thread(runnable).start();
        }
        taskList.add(task);
        return task;
    }

    public ArrayList<DownTask> getTaskList() {
        return taskList;
    }

    /**
     * 注册监听器
     *
     * @param listener
     */
    public void addListener(DownloadListener listener) {
        listeners.add(listener);
    }

    /**
     * 移除监听器
     *
     * @param listener
     */
    public void removeListener(DownloadListener listener) {
        listeners.remove(listener);
    }

    public void removeAllListener() {
        listeners.clear();
    }

    @Override
    public void onStart(DownTask task) {
        for (DownloadListener listener : listeners) {
            listener.onStart(task);
        }
    }

    @Override
    public void onProgressChanged(DownTask task) {
        for (DownloadListener listener : listeners) {
            listener.onProgressChanged(task);
        }
    }

    @Override
    public void onFail(DownTask task, int errorCode) {
        taskList.remove(task);
        for (DownloadListener listener : listeners) {
            listener.onFail(task, errorCode);
        }
    }

    @Override
    public void onComplete(DownTask task) {
        taskList.remove(task);
        isFirstFailure = false;
        for (DownloadListener listener : listeners) {
            listener.onComplete(task);
        }
    }

    @Override
    public void onCancel(DownTask task) {
        taskList.remove(task);
        for (DownloadListener listener : listeners) {
            listener.onCancel(task);
        }
    }

    @Override
    public void onWifiDisconnect() {
        //wifi断开
        for (DownTask task : taskList) {
            //不取消不阻塞的下载任务
            if(task.isBlock()) {
                task.cancel(false);
            }
        }
        for (DownloadListener listener : listeners) {
            listener.onWifiDisconnect();
        }
    }

    public void cancelAllTasks() {
        for (DownTask task : taskList) {
            task.cancel(false);
        }
    }

    class WifiStateReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION))
            {
                NetworkInfo info=intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if(info!=null&&info.getDetailedState().equals(NetworkInfo.DetailedState.DISCONNECTED)&&info.getType()== ConnectivityManager.TYPE_WIFI)
                {
                    PLog.d(this,"WIFI断开");
                    onWifiDisconnect();
                }
            }
        }
    }


    public void destory() {
        cancelAllTasks();
        executor.shutdownNow();
        taskList.clear();
        removeAllListener();
        PApplication.getInstance().unregisterReceiver(wifiReceiver);
        instance = null;


    }
    /**
     * 是否仅在wifi下下载
     * @param wifiOnly
     */
    public void setWifiOnly(boolean wifiOnly) {
        this.wifiOnly = wifiOnly;
        if(wifiOnly&&!NetWorkUtil.isWifiConnected())
        {
            onWifiDisconnect();
        }
    }

}