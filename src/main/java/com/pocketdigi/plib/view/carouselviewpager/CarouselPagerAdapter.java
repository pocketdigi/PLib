package com.pocketdigi.plib.view.carouselviewpager;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.pocketdigi.plib.core.PApplication;
import com.pocketdigi.plib.view.CustomDraweeView;
import com.pocketdigi.plib.view.IButtonData;

import java.util.ArrayList;
import java.util.List;

/**
 * 轮播Pager的Adapter
 * Created by fhp on 15/10/26.
 */
public class CarouselPagerAdapter extends PagerAdapter {
    private SparseArray<CustomDraweeView> viewSparseArray;
    List<IButtonData> data;
    int realCount = 0;
    @DrawableRes
    int placeHolderImage=0;
    ScalingUtils.ScaleType placeHolderImageScaleType=ScalingUtils.ScaleType.FIT_XY,scaleType=ScalingUtils.ScaleType.FIT_XY;
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
    public int getItemPosition(Object object) {
        return POSITION_NONE;
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
        CustomDraweeView draweeView = viewSparseArray.get(realPosition);
        final IButtonData buttonData = data.get(realPosition);
        if (draweeView == null) {
            draweeView = new CustomDraweeView(container.getContext());
            ViewPager.LayoutParams params = new ViewPager.LayoutParams();
            draweeView.setLayoutParams(params);
            draweeView.getHierarchy().setActualImageScaleType(scaleType);
            if(placeHolderImage!=0) {
                draweeView.getHierarchy().setPlaceholderImage(container.getResources().getDrawable(placeHolderImage),placeHolderImageScaleType);
            }
            draweeView.setOnClickListener(new View.OnClickListener() {
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
            viewSparseArray.put(realPosition, draweeView);
        }
        draweeView.setImageUrl(buttonData.getImageUrl());
        //如果View已经在之前添加到了一个父组件，则必须先remove，否则会抛出IllegalStateException。
        ViewParent vp = draweeView.getParent();
        if (vp != null) {
            ViewGroup parent = (ViewGroup) vp;
            parent.removeView(draweeView);
        }
        container.addView(draweeView);
        return draweeView;
    }

    public void setData(List<? extends IButtonData> banners) {
        this.data.clear();
        this.data.addAll(banners);
        notifyDataSetChanged();
    }

    public int getPlaceHolderImage() {
        return placeHolderImage;
    }

    public void setPlaceHolderImage(@DrawableRes int placeHolderImage) {
        this.placeHolderImage = placeHolderImage;
    }

    public ScalingUtils.ScaleType getScaleType() {
        return scaleType;
    }

    public void setScaleType(ScalingUtils.ScaleType scaleType) {
        this.scaleType = scaleType;
    }

    public ScalingUtils.ScaleType getPlaceHolderImageScaleType() {
        return placeHolderImageScaleType;
    }

    public void setPlaceHolderImageScaleType(ScalingUtils.ScaleType placeHolderImageScaleType) {
        this.placeHolderImageScaleType = placeHolderImageScaleType;
    }

    /**
     * 数据改变调用，如果数据只有两条，为了效果会加到4条
     * 三条就可以，但如果将第1条复制为第3条，循环滚动时会，3后面就是1，就是发现两张一样的图连着滚
     * 该方法会清除所有view,重新生成，没事别调
     */
    @Override
    public void notifyDataSetChanged() {
        int size = data.size();
        realCount = size;
        if (size == 2) {
            data.add(data.get(0));
            data.add(data.get(1));
        }
        viewSparseArray.clear();
        super.notifyDataSetChanged();
    }
}
