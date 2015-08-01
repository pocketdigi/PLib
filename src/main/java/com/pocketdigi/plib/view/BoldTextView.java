package com.pocketdigi.plib.view;

import android.content.Context;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 加粗的textView
 * Created by fhp on 15/7/31.
 */
public class BoldTextView extends TextView{
    public BoldTextView(Context context) {
        super(context);
        init();
    }

    public BoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    private void init() {
        TextPaint tp = getPaint();
        tp.setFakeBoldText(true);
    }
}
