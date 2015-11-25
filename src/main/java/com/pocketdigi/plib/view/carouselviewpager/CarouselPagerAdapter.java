package com.pocketdigi.plib.view.carouselviewpager;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;

import com.android.volley.toolbox.NetworkImageView;
import com.pocketdigi.plib.core.PApplication;
import com.pocketdigi.plib.view.IButtonData;

import java.util.ArrayList;
import java.util.List;

/**
 * 轮播Pager的Adapter
 * Created by fhp on 15/10/26.
 */
public class CarouselPagerAdapter extends PagerAdapter {
    private SparseArray<NetworkImageView> viewSparseArray;
    List<IButtonData> data;
    int realCount = 0;

    public CarouselPagerAdapter() {
        this.viewSparseArray = new SparseArray<>();
        this.data = new ArrayList<>();
    }

    @Override
    public int getCount() {
        //设置一个很大的值，使用户看不到边界
        //数值越大，越容易anr,绝对 不能用Integer.MAX_VALUE

        return realCount==1?1:data.size() == 0 ? 0 : 1000;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position,
                            Object object) {
        //Warning：不要在这里调用removeView
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //对ViewPager页号求模取出View列表中要显示的项
        final int realPosition = position % data.size();
        NetworkImageView imageView = viewSparseArray.get(realPosition);
        final IButtonData buttonData = data.get(realPosition);
        if (imageView == null) {
            imageView = new NetworkImageView(container.getContext());
            imageView.setImageUrl(buttonData.getImageUrl());
            ViewPager.LayoutParams params = new ViewPager.LayoutParams();
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int dataPosition = realPosition;
                    if (realCount == 1) {
                        //如果真实条数少于3条，是自动填充到3条的，
                        dataPosition = 0;
                    }
                    if (realCount == 2) {
                        dataPosition = (realPosition == 2) ? 0 : realPosition;
                    }
                    PApplication.getInstance().postEvent(new CarouselItemClickEvent(dataPosition, buttonData,CarouselPagerAdapter.this));
                }
            });
            viewSparseArray.put(realPosition, imageView);
        }

        //如果View已经在之前添加到了一个父组件，则必须先remove，否则会抛出IllegalStateException。
        ViewParent vp = imageView.getParent();
        if (vp != null) {
            ViewGroup parent = (ViewGroup) vp;
            parent.removeView(imageView);
        }


        container.addView(imageView);
        return imageView;
    }

    public void setData(List<? extends IButtonData> banners) {
        this.data.clear();
        this.data.addAll(banners);
        notifyDataSetChanged();
    }

    /**
     * 数据改变调用(如果数据少于三条，为了效果会加到3条)
     * 该方法会清除所有view,重新生成，没事别调
     */
    @Override
    public void notifyDataSetChanged() {
        int size = data.size();
        realCount = size;
        if (size == 2) {
            IButtonData firstData = data.get(0);
            data.add(firstData);
        }
        viewSparseArray.clear();
        super.notifyDataSetChanged();
    }
}
