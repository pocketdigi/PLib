package com.pocketdigi.plib.volley;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.util.AttributeSet;

import com.android.volley.toolbox.NetworkImageView;
import com.pocketdigi.plib.R;
import com.pocketdigi.plib.util.ImageUtil;

/**
 * 显示网络图片，并切成圆角
 * Created by fhp on 14/11/4.
 */
public class RoundCornerNetworkImageView extends NetworkImageView {
    float radius = 10;
    public RoundCornerNetworkImageView(Context context) {
        super(context);
    }

    public RoundCornerNetworkImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.RoundCornerNetworkImageView);
        radius = a.getDimension(R.styleable.RoundCornerNetworkImageView_radius, radius);
        a.recycle();
    }

    public RoundCornerNetworkImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        if (bm != null) {
            RoundedBitmapDrawable roundedBitmapDrawable = ImageUtil.toRoundDrawable(getResources(), bm,radius);
            setImageDrawable(roundedBitmapDrawable);
        }
    }
}
