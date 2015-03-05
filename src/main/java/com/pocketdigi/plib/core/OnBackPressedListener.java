package com.pocketdigi.plib.core;

/**
 * 返回键监听，给Fragment用
 * Created by fhp on 15/3/5.
 */
public interface OnBackPressedListener {
    /**
     * 按back键触发
     * @return 如果在此已经处理完，不需要继续传播事件，true，否则false
     */
    public boolean onBackPressed();
}
