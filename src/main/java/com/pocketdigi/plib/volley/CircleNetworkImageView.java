package com.pocketdigi.plib.volley;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.util.AttributeSet;

import com.android.volley.toolbox.NetworkImageView;
import com.pocketdigi.plib.util.ImageUtil;

/**
 * 显示网络图片，并切成圆形
 * Created by fhp on 14/11/4.
 */
public class CircleNetworkImageView extends NetworkImageView{
    public CircleNetworkImageView(Context context) {
        super(context);
    }

    public CircleNetworkImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleNetworkImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        if(bm!=null) {
            RoundedBitmapDrawable roundedBitmapDrawable = ImageUtil.toCircularDrawable(getResources(), bm);
            setImageDrawable(roundedBitmapDrawable);
        }
    }
}
