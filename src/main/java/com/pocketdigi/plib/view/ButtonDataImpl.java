package com.pocketdigi.plib.view;

/**
 * IButtonText的默认实现
 * Created by fhp on 15/7/15.
 */
public class ButtonDataImpl implements IButtonData {
    String text;

    public ButtonDataImpl(String text) {
        this.text = text;
    }

    @Override
    public String getButtonText() {
        return text;
    }

    @Override
    public String getImageUrl() {
        return null;
    }
}
