package com.pocketdigi.plib.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.pocketdigi.plib.R;

/**
 * ViewPager指示器 选中为白点，未选中为空心
 * Created by fhp on 15/3/3.
 */
public class ViewPagerIndicator extends LinearLayout implements ViewPager.OnPageChangeListener{
    int checkedDrawableId, unCheckedDrawableId;
    int spacing;
    int iconSize=LayoutParams.WRAP_CONTENT;
    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        checkedDrawableId =R.drawable.plib_ic_indicator_current;
        unCheckedDrawableId =R.drawable.plib_ic_indicator_notcurrent;
        setOrientation(LinearLayout.HORIZONTAL);
    }
    public void setIconSize(int pixelSize) {
        iconSize=pixelSize;
    }
    public void setPageNumber(int pageNumber) {
        removeAllViews();
        for(int i=0;i<pageNumber;i++) {
            ImageView imageView=new ImageView(getContext());
            LinearLayout.LayoutParams params=new LayoutParams(iconSize,iconSize);
            params.rightMargin=spacing;
            imageView.setImageResource(unCheckedDrawableId);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setId(i);

            addView(imageView, params);
        }
    }


    public void setCheckedDrawableId(int checkedDrawableId) {
        this.checkedDrawableId = checkedDrawableId;
    }


    public void setUnCheckedDrawableId(int unCheckedDrawableId) {
        this.unCheckedDrawableId = unCheckedDrawableId;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        int childCount=getChildCount();
        int realPosition = position % childCount;
        for(int i=0;i<childCount;i++) {
            ImageView child = (ImageView)getChildAt(i);
            int childId = child.getId();
            if(childId==realPosition) {
                child.setImageResource(checkedDrawableId);
            }else{
                child.setImageResource(unCheckedDrawableId);
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
