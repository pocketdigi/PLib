package com.pocketdigi.plib.upload;

import com.pocketdigi.plib.core.PEvent;

/**
 * Created by fhp on 14/12/22.
 */
public class UploadProgressChangeEvent extends PEvent{
    UploadProgress progress;
    public UploadProgressChangeEvent(UploadProgress progress) {
        this.progress = progress;
    }

    public UploadProgress getProgress() {
        return progress;
    }
}
