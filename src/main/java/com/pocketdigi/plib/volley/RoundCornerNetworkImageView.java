package com.pocketdigi.plib.volley;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.pocketdigi.plib.R;
import com.pocketdigi.plib.core.PLog;
import com.pocketdigi.plib.util.ImageUtil;

/**
 * 显示网络图片，并切成圆角
 * Created by fhp on 14/11/4.
 */
public class RoundCornerNetworkImageView extends NetworkImageView {
    float radius = 10;
    //是否部分虚化
    boolean partBlur;
    boolean loadFinish=false;
    Bitmap sourceBitmap;
    //partBlur值是否已经改变
    boolean partBlurChanged;
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
        sourceBitmap=bm;
        if (bm != null) {
            if(partBlur)
            {
                PLog.d(this,"开始虚化");
                bm= ImageUtil.blurBitmapPart(bm, (int)(bm.getHeight()*0.6));
            }
            PLog.d(this,"开始切圆角");
            RoundedBitmapDrawable roundedBitmapDrawable = ImageUtil.toRoundDrawable(getResources(), bm,radius);
            PLog.d(this,"结束切圆角");
            setImageDrawable(roundedBitmapDrawable);
        }
        loadFinish=true;

    }

    public void setImageUrl(String url, ImageLoader imageLoader,boolean partBlur) {
        if(this.partBlur!=partBlur)
        {
            partBlurChanged=true;
            this.partBlur=partBlur;
        }
        super.setImageUrl(url, imageLoader);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        sourceBitmap=null;
    }

    /**
     * 重载是因为多了选中后虚化，需要重新加载图片
     * Loads the image for the view if it isn't already loaded.
     * @param isInLayoutPass True if this was invoked from a layout pass, false otherwise.
     */
    @Override
    public void loadImageIfNecessary(final boolean isInLayoutPass) {
        int width = getWidth();
        int height = getHeight();

        boolean wrapWidth = false, wrapHeight = false;
        if (getLayoutParams() != null) {
            wrapWidth = getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT;
            wrapHeight = getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT;
        }

        // if the view's bounds aren't known yet, and this is not a wrap-content/wrap-content
        // view, hold off on loading the image.
        boolean isFullyWrapContent = wrapWidth && wrapHeight;
        if (width == 0 && height == 0 && !isFullyWrapContent) {
            return;
        }

        // if the URL to be loaded in this view is empty, cancel any old requests and clear the
        // currently loaded image.
        if (TextUtils.isEmpty(mUrl)) {
            if (mImageContainer != null) {
                mImageContainer.cancelRequest();
                mImageContainer = null;
            }
            setDefaultImageOrNull();
            return;
        }

        // if there was an old request in this view, check if it needs to be canceled.
        if (mImageContainer != null && mImageContainer.getRequestUrl() != null) {
            if (mImageContainer.getRequestUrl().equals(mUrl)&&!partBlurChanged) {
                // if the request is from the same URL, return.
                return;
            } else {
                // if there is a pre-existing request, cancel it if it's fetching a different URL.
                mImageContainer.cancelRequest();
                setDefaultImageOrNull();
                partBlurChanged=false;
            }
        }

        // Calculate the max image width / height to use while ignoring WRAP_CONTENT dimens.
        int maxWidth = wrapWidth ? 0 : width;
        int maxHeight = wrapHeight ? 0 : height;

        // The pre-existing content of this view didn't match the current URL. Load the new image
        // from the network.
        ImageLoader.ImageContainer newContainer = mImageLoader.get(mUrl,
                new ImageLoader.ImageListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (mErrorImageId != 0) {
                            setImageResource(mErrorImageId);
                        }
                    }

                    @Override
                    public void onResponse(final ImageLoader.ImageContainer response, boolean isImmediate) {
                        // If this was an immediate response that was delivered inside of a layout
                        // pass do not set the image immediately as it will trigger a requestLayout
                        // inside of a layout. Instead, defer setting the image by posting back to
                        // the main thread.
                        if (isImmediate && isInLayoutPass) {
                            post(new Runnable() {
                                @Override
                                public void run() {
                                    onResponse(response, false);
                                }
                            });
                            return;
                        }

                        if (response.getBitmap() != null) {
                            setImageBitmap(response.getBitmap());
                        } else if (mDefaultImageId != 0) {
                            setImageResource(mDefaultImageId);
                        }
                    }
                }, maxWidth, maxHeight);

        // update the ImageContainer to be the new bitmap container.
        mImageContainer = newContainer;
    }
}
