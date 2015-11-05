package com.pocketdigi.plib.view.carouselviewpager;

import android.support.v4.view.ViewPager;

/**
 * Created by fhp on 15/11/5.
 */
public interface IPagerIndicator extends ViewPager.OnPageChangeListener{
    void setPageCount(int pageCount);
}
