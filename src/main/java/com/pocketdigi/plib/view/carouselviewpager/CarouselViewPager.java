package com.pocketdigi.plib.view.carouselviewpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import com.pocketdigi.plib.core.PLog;

import java.lang.reflect.Field;

/**
 * 轮播ViewPager
 * Created by fhp on 15/10/26.
 */
public class CarouselViewPager extends ViewPager {
    int realCount;
    IPagerIndicator pagerIndicator;
    OnPageChangeListener onPageChangeListener;
    CarouselHandler carouselHandler;
    int realPosition=-1;
    public CarouselViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CarouselViewPager(Context context) {
        super(context);
        init();
    }

    private void init() {
        try {
            Field mScroller;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(getContext(), new AccelerateDecelerateInterpolator());
            mScroller.set(this, scroller);

            Field mFirstLayout = ViewPager.class.getDeclaredField("mFirstLayout");
            mFirstLayout.setAccessible(true);
            mFirstLayout.set(this, true);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
        }
        super.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (realCount == 0)
                    return;
                realPosition = position % realCount;
                if (pagerIndicator != null)
                    pagerIndicator.onPageScrolled(realPosition, positionOffset, positionOffsetPixels);
                if (onPageChangeListener != null)
                    onPageChangeListener.onPageScrolled(realPosition, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                if (realCount == 0)
                    return;
                int realPosition = position % realCount;
                if (pagerIndicator != null)
                    pagerIndicator.onPageSelected(realPosition);
                if (onPageChangeListener != null)
                    onPageChangeListener.onPageSelected(realPosition);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (realCount == 0)
                    return;
                if (pagerIndicator != null)
                    pagerIndicator.onPageScrollStateChanged(state);
                if (onPageChangeListener != null)
                    onPageChangeListener.onPageScrollStateChanged(state);
            }
        });
        setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                PLog.e(this,"action"+action);
                switch (action) {
                    case MotionEvent.ACTION_UP:
                        if (carouselHandler != null)
                            carouselHandler.start();
                        break;
                    default:
                        if (carouselHandler != null)
                            carouselHandler.stop();
                        break;
                }
                return false;
            }
        });

    }

    public void setPagerIndicator(IPagerIndicator pagerIndicator) {
        this.pagerIndicator = pagerIndicator;
    }

    public int getRealCount() {
        return realCount;
    }

    @Override
    public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener;

    }

    /**
     * 最后一步调用，设置真正的item数量
     *
     * @param realCount
     */
    public void setRealCount(final int realCount) {
        this.realCount = realCount;
        if(pagerIndicator!=null) {
            pagerIndicator.setPageCount(realCount);
            pagerIndicator.onPageSelected(0);
        }
        setCurrentItem(10 * realCount,false);

    }

    public void stopCarousel() {
        if (carouselHandler != null && carouselHandler.isRunning())
            carouselHandler.stop();
    }

    public void startCarousel() {
        if (carouselHandler == null)
            carouselHandler = new CarouselHandler(this);
        if (realCount > 0) {
            carouselHandler.start();
        }
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        super.setAdapter(adapter);
    }

    public class FixedSpeedScroller extends Scroller {

        private int mDuration = 800;

        public FixedSpeedScroller(Context context) {
            super(context);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator, boolean flywheel) {
            super(context, interpolator, flywheel);
        }


        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(carouselHandler!=null)
            carouselHandler.destory();
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);

//        if (carouselHandler != null) {
//            switch (visibility) {
//                case View.VISIBLE:
//                    carouselHandler.start();
//                    break;
//                default:
//                    carouselHandler.stop();
//                    break;
//            }
//        }
    }

}
