package com.pocketdigi.plib.view;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.pocketdigi.plib.core.PApplication;

/**
 * 增加几个直接设置路径的方法
 * Created by fhp on 15/12/25.
 */
public class CustomDraweeView extends SimpleDraweeView {
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
        setImageURI(Uri.parse("file://" + path));
    }

    public void setImageResourceId(@DrawableRes int resId) {
        setImageURI(Uri.parse("res://" + PApplication.getInstance().getPackageName() + "/" + resId));
    }

    public void setScaleType(ScalingUtils.ScaleType scaleType) {
        this.getHierarchy().setActualImageScaleType(scaleType);
    }

    /**
     * 使用setScaleType(ScalingUtils.ScaleType scaleType)
     *
     * @param scaleType
     */
    @Deprecated
    @Override
    public void setScaleType(ScaleType scaleType) {
        ScalingUtils.ScaleType scaleType1;
        switch (scaleType) {
            case CENTER:
                scaleType1 = ScalingUtils.ScaleType.CENTER;
                break;
            case CENTER_CROP:
                scaleType1 = ScalingUtils.ScaleType.CENTER_CROP;
                break;
            case CENTER_INSIDE:
                scaleType1 = ScalingUtils.ScaleType.CENTER_INSIDE;
                break;
            case FIT_CENTER:
                scaleType1 = ScalingUtils.ScaleType.FIT_CENTER;
                break;
            case FIT_START:
                scaleType1 = ScalingUtils.ScaleType.FIT_START;
                break;
            case FIT_END:
                scaleType1 = ScalingUtils.ScaleType.FIT_END;
                break;
            case FIT_XY:
                scaleType1 = ScalingUtils.ScaleType.FIT_XY;
                break;
            default:
                scaleType1 = ScalingUtils.ScaleType.FIT_XY;
                break;
        }
        setScaleType(scaleType1);
    }
}
