package com.pocketdigi.plib.util;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.pocketdigi.plib.core.PAsyncTask;
import com.pocketdigi.plib.volley.L2LRUImageCache;

/**
 * Created by fhp on 15/9/6.
 */
public class LocalImageLoader {

    public LocalImageLoader() {
    }

    public void load(final ImageView imageView, final String imagePath) {
        int w = imageView.getWidth();
        int h = imageView.getHeight();
        int[] screenSize = DeviceUtils.getScreenSize();
        if (w == 0) {
            w = screenSize[0];
        }
        if (h == 0) {
            h = screenSize[1];
        }
        final int finalW = w;
        final int finalH = h;

        new PAsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                String tag = imageView.getTag().toString();
                if (tag == null || !tag.equals(imagePath))
                    return null;
                Bitmap bitmap = ImageUtil.decodeFromFile(imagePath, finalW, finalH);
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                if (bitmap != null) {
                    String tag = imageView.getTag().toString();
                    if (tag != null && tag.equals(imagePath)) {
                        imageView.setImageBitmap(bitmap);
                    }

                }
            }
        }.execute();

    }
}
