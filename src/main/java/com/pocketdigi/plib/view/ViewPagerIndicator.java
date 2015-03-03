package com.pocketdigi.plib.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.pocketdigi.plib.R;

/**
 * Created by fhp on 15/3/3.
 */
public class ViewPagerIndicator extends LinearLayout implements ViewPager.OnPageChangeListener{
    Drawable checkedDrawble,defaultDrawable;
    int spacing;
    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        checkedDrawble=getResources().getDrawable(R.drawable.plib_ic_indicator_current);
        defaultDrawable=getResources().getDrawable(R.drawable.plib_ic_indicator_notcurrent);
        setOrientation(LinearLayout.HORIZONTAL);
    }
    public void setPageNumber(int pageNumber) {
        removeAllViews();
        for(int i=0;i<pageNumber;i++) {
            ImageView imageView=new ImageView(getContext());
            LinearLayout.LayoutParams params=new LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.rightMargin=spacing;
            imageView.setImageDrawable(defaultDrawable);
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            imageView.setId(i);

            addView(imageView, params);
        }
    }

    public Drawable getCheckedDrawble() {
        return checkedDrawble;
    }

    public void setCheckedDrawble(Drawable checkedDrawble) {
        this.checkedDrawble = checkedDrawble;
    }

    public Drawable getDefaultDrawable() {
        return defaultDrawable;
    }

    public void setDefaultDrawable(Drawable defaultDrawable) {
        this.defaultDrawable = defaultDrawable;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        int childCount=getChildCount();
        for(int i=0;i<childCount;i++) {
            ImageView child = (ImageView)getChildAt(i);
            int childId = child.getId();
            if(childId==position) {
                child.setImageDrawable(checkedDrawble);
            }else{
                child.setImageDrawable(defaultDrawable);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public int getSpacing() {
        return spacing;
    }

    public void setSpacing(int spacing) {
        this.spacing = spacing;
    }
}
