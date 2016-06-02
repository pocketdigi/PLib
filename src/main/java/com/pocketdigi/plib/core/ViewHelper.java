package com.pocketdigi.plib.core;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.pocketdigi.plib.util.UiThreadExecutor;

/**
 * Created by fhp on 15/10/27.
 */
public class ViewHelper {
    /**
     * 设置背景图片的兼容方法
     * @param view
     * @param resId
     */
    public static void setBackgroundForView(View view,@DrawableRes int resId) {
        Drawable backgroundDrawable;
        if(Build.VERSION.SDK_INT >= 21){
            backgroundDrawable=view.getResources().getDrawable(resId, null);
        }else{
            backgroundDrawable=view.getResources().getDrawable(resId);
        }
        setBackgroundForView(view,backgroundDrawable);
    }

    public static void setBackgroundForView(View view,Drawable drawable) {
        if(Build.VERSION.SDK_INT >= 16){
            view.setBackground(drawable);
        }else{
            view.setBackgroundDrawable(drawable);
        }
    }

    public static void setBackgroundForView(final View view,String imageUrl) {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        ImageRequest imageRequest=ImageRequest.fromUri(Uri.parse(imageUrl));

        DataSource<CloseableReference<CloseableImage>>
                dataSource = imagePipeline.fetchDecodedImage(imageRequest,view);

        dataSource.subscribe(new BaseBitmapDataSubscriber() {
            @Override
            public void onNewResultImpl(@Nullable Bitmap bitmap) {
                ViewHelper.setBackgroundForView(view,new BitmapDrawable(view.getResources(),bitmap));
            }

            @Override
            public void onFailureImpl(DataSource dataSource) {
                // No cleanup required here.
            }
        }, new UiThreadExecutor());

    }

    public static void setImageUrlForImageView(final ImageView imageView, String imageUrl) {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        ImageRequest imageRequest=ImageRequest.fromUri(Uri.parse(imageUrl));

        DataSource<CloseableReference<CloseableImage>>
                dataSource = imagePipeline.fetchDecodedImage(imageRequest,imageView);

        dataSource.subscribe(new BaseBitmapDataSubscriber() {
            @Override
            public void onNewResultImpl(@Nullable Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
            }

            @Override
            public void onFailureImpl(DataSource dataSource) {
                // No cleanup required here.
            }
        }, new UiThreadExecutor());
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
