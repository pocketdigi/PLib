package com.pocketdigi.plib.util;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;

/**
 * Created by fhp on 16/5/12.
 */
public class UiThreadExecutor implements Executor {
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    @Override
    public void execute(Runnable command) {
        mHandler.post(command);
    }
}
