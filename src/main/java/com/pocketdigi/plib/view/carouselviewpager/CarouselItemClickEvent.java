package com.pocketdigi.plib.view.carouselviewpager;

import com.pocketdigi.plib.core.PEvent;
import com.pocketdigi.plib.view.IButtonData;

/**
 * 轮播Item点击 事件
 * Created by fhp on 15/11/5.
 */
public class CarouselItemClickEvent extends PEvent {
    IButtonData data;
    int position;
    CarouselPagerAdapter pagerAdapter;

    public CarouselItemClickEvent(int position, IButtonData data, CarouselPagerAdapter pagerAdapter) {
        this.data = data;
        this.position = position;
        this.pagerAdapter = pagerAdapter;
    }

    public IButtonData getData() {
        return data;
    }

    public int getPosition() {
        return position;
    }

    public CarouselPagerAdapter getPagerAdapter() {
        return pagerAdapter;
    }
}
