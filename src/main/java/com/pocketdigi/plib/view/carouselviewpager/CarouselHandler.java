package com.pocketdigi.plib.view.carouselviewpager;

import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.ViewPager;

import com.pocketdigi.plib.core.PLog;

/**
 * 处理轮播
 * Created by fhp on 15/10/26.
 */
public class CarouselHandler extends Handler{
    ViewPager viewPager;
    boolean isRunning;
    long delay=7000;
    public CarouselHandler(ViewPager viewPager) {
        super(Looper.getMainLooper());
        this.viewPager=viewPager;
    }
    public void start() {
        PLog.e("CarouselHandler","开始滚动");
        if(isRunning)
            return;
        isRunning=true;
        postDelayed(runnable,delay);

    }

    public boolean isRunning() {
        return isRunning;
    }

    public void stop() {
        PLog.e("CarouselHandler","停止滚动");
        isRunning=false;
        this.removeCallbacks(runnable);
    }

    public void destory() {
        stop();
        viewPager=null;
    }

    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            if(isRunning) {
                PLog.e("CarouselHandler", "滚动");
                int currentItem = viewPager.getCurrentItem();
                int count = viewPager.getAdapter().getCount();
                int targetItem=currentItem + 1;
                if(currentItem>=(count-1)){
                    targetItem=count/2;
                }
                viewPager.setCurrentItem(targetItem, true);
                postDelayed(this, delay);
            }
        }
    };

}
