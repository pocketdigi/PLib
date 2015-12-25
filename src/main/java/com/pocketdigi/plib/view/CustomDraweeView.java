package com.pocketdigi.plib.view;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * 增加几个直接设置路径的方法
 * Created by fhp on 15/12/25.
 */
public class CustomDraweeView extends SimpleDraweeView{
    public CustomDraweeView(Context context) {
        super(context);
    }

    public CustomDraweeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setImageUrl(String url) {
        setImageURI(Uri.parse(url));
    }

    public void setImagePath(String path) {
        setImageURI(Uri.parse("file://"+path));
    }

    public void setImageResourceId(@DrawableRes int resId) {
        setImageURI(Uri.parse("res://" +resId));
    }

    public void setScaleType(ScalingUtils.ScaleType scaleType) {
        this.getHierarchy().setActualImageScaleType(scaleType);
    }
}
