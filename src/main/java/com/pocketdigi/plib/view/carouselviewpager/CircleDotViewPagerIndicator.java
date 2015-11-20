package com.pocketdigi.plib.view.carouselviewpager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.pocketdigi.plib.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 圆点ViewPager指示器 选中为白点，未选中为空心
 * Created by fhp on 15/3/3.
 */
public class CircleDotViewPagerIndicator extends LinearLayout implements IPagerIndicator{
    int checkedDrawableId, unCheckedDrawableId;
    int spacing;
    int iconSize=LayoutParams.WRAP_CONTENT;
    List<ViewPager.OnPageChangeListener> pageChangeListeners;
    public CircleDotViewPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleDotViewPagerIndicator(Context context) {
        super(context);
        init();
    }

    private void init() {
        checkedDrawableId =R.drawable.plib_ic_indicator_current;
        unCheckedDrawableId =R.drawable.plib_ic_indicator_notcurrent;
        setOrientation(LinearLayout.HORIZONTAL);
        pageChangeListeners=new ArrayList<>();
    }

    public void setIconSize(int pixelSize) {
        iconSize=pixelSize;
    }

    /**
     * 设置总页数
     * @param pageCount
     */
    public void setPageCount(int pageCount) {
        removeAllViews();
        for(int i=0;i<pageCount;i++) {
            ImageView imageView=new ImageView(getContext());
            LinearLayout.LayoutParams params=new LayoutParams(iconSize,iconSize);
            params.rightMargin=spacing;
            imageView.setImageResource(unCheckedDrawableId);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setId(i);

            addView(imageView, params);
        }
    }

    public void addOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        pageChangeListeners.add(listener);
    }
    public void removeOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        pageChangeListeners.remove(listener);
    }



    public void setCheckedDrawableId(int checkedDrawableId) {
        this.checkedDrawableId = checkedDrawableId;
    }


    public void setUnCheckedDrawableId(int unCheckedDrawableId) {
        this.unCheckedDrawableId = unCheckedDrawableId;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if(pageChangeListeners.size()>0) {
            for(ViewPager.OnPageChangeListener listener:pageChangeListeners) {
                listener.onPageScrolled(position,positionOffset,positionOffsetPixels);
            }
        }
    }

    @Override
    public void onPageSelected(int position) {
        int childCount=getChildCount();
        if(childCount>0) {
            int realPosition = position % childCount;
            for (int i = 0; i < childCount; i++) {
                ImageView child = (ImageView) getChildAt(i);
                int childId = child.getId();
                if (childId == realPosition) {
                    child.setImageResource(checkedDrawableId);
                } else {
                    child.setImageResource(unCheckedDrawableId);
                }
            }
            if (pageChangeListeners.size() > 0) {
                for (ViewPager.OnPageChangeListener listener : pageChangeListeners) {
                    listener.onPageSelected(position);
                }
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if(pageChangeListeners.size()>0) {
            for(ViewPager.OnPageChangeListener listener:pageChangeListeners) {
                listener.onPageScrollStateChanged(state);
            }
        }
    }

    public int getSpacing() {
        return spacing;
    }

    public void setSpacing(int spacing) {
        this.spacing = spacing;
    }
}
