package com.pocketdigi.plib.viewhelper;

import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.ViewPager;

import com.pocketdigi.plib.core.PToast;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewPager自动轮播,Adapter的getCount必须返回Integer.MAX_VALUE
 * Created by fhp on 15/7/16.
 */
public class ViewPagerAutoScroller implements ViewPager.OnPageChangeListener {
    List<ViewPager.OnPageChangeListener> onPageChangeListenerList;
    //动画周期
    int period = 5000;
    ViewPager viewPager;
    Handler handler;

    public ViewPagerAutoScroller(ViewPager viewPager) {
        this.viewPager = viewPager;
        onPageChangeListenerList = new ArrayList<>();
        handler = new Handler(Looper.getMainLooper());
    }

    /**
     * 设置播放周期，
     *
     * @param period ms
     */
    public void setPeriod(int period) {
        this.period = period;
    }

    /**
     * 自动轮播
     */
    public void autoScroll() {
        viewPager.setOnPageChangeListener(this);
        handler.postDelayed(autoScroll, period);
    }

    public void stopScroll() {
        handler.removeCallbacks(autoScroll);
    }

    public void destory() {
        stopScroll();
        removeAllPageChangeListener();
        viewPager = null;
    }

    public void addOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        onPageChangeListenerList.add(onPageChangeListener);
    }

    public void removeOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        onPageChangeListenerList.remove(onPageChangeListener);
    }

    /**
     * 清除所有监听器
     */
    public void removeAllPageChangeListener() {
        onPageChangeListenerList.clear();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        for (ViewPager.OnPageChangeListener onPageChangeListener : onPageChangeListenerList) {
            onPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        for (ViewPager.OnPageChangeListener onPageChangeListener : onPageChangeListenerList) {
            onPageChangeListener.onPageSelected(position);
        }
        handler.removeCallbacks(autoScroll);
        handler.postDelayed(autoScroll, period);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        for (ViewPager.OnPageChangeListener onPageChangeListener : onPageChangeListenerList) {
            onPageChangeListener.onPageScrollStateChanged(state);
        }
    }

    Runnable autoScroll = new Runnable() {
        @Override
        public void run() {
            int count = viewPager.getAdapter().getCount();
            int currentItem = viewPager.getCurrentItem();
            int target=0;
            if(currentItem<count-1) {
                target=currentItem+1;
            }
            viewPager.setCurrentItem(target);
        }
    };

}
