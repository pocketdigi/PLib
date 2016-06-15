package com.pocketdigi.plib.http;

/**
 * Created by Exception on 16/6/12.
 */
public interface DownProgressListener {
    void update(long bytesRead, long contentLength, boolean done);
}
