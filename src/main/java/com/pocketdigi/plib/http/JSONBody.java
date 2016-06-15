package com.pocketdigi.plib.http;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;

/**
 * Post json
 * Created by Exception on 16/6/12.
 */
public class JSONBody extends RequestBody {
    private static final MediaType CONTENT_TYPE =
            MediaType.parse("application/json");
    Object object;

    public JSONBody(Object object) {
        this.object = object;
    }

    @Override
    public MediaType contentType() {
        return CONTENT_TYPE;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        Buffer buffer = sink.buffer();
        Gson gson=new Gson();
        String json = gson.toJson(object);
        buffer.writeString(json, Charset.forName("UTF-8"));
    }
}
