package com.pocketdigi.plib.core;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.view.View;

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
        if(Build.VERSION.SDK_INT >= 16){
            view.setBackground(backgroundDrawable);
        }else{
            view.setBackgroundDrawable(backgroundDrawable);
        }
    }
}
