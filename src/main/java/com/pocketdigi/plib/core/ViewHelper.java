package com.pocketdigi.plib.core;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by fhp on 15/10/27.
 */
public class ViewHelper {
    /**
     * 设置背景图片的兼容方法
     * @param view
     * @param resId
     */
    public static final void setBackgroundForView(View view,@DrawableRes int resId) {
        Drawable backgroundDrawable;
        if(Build.VERSION.SDK_INT >= 21){
            backgroundDrawable=view.getResources().getDrawable(resId, null);
        }else{
            backgroundDrawable=view.getResources().getDrawable(resId);
        }
        setBackgroundForView(view,backgroundDrawable);
    }

    public static final void setBackgroundForView(View view,Drawable drawable) {
        if(Build.VERSION.SDK_INT >= 16){
            view.setBackground(drawable);
        }else{
            view.setBackgroundDrawable(drawable);
        }
    }




    /**
     * 移除view的 OnGlobalLayoutListener
     * @param v
     * @param onGlobalLayoutListener
     */
    public static final void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener) {
        if(Build.VERSION.SDK_INT >= 16){
            v.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
        }else{
            v.getViewTreeObserver().removeGlobalOnLayoutListener(onGlobalLayoutListener);
        }
    }
}
