package com.pocketdigi.plib.upload;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;

import java.nio.charset.Charset;

/**
 * Created by fhp on 14/12/22.
 */
public class HttpUploader {
    public static int CONNECT_TIME_OUT = 10000;
    public static int SO_TIME_OUT = 30000;
    public void upload(String filePath,String apiUrl) {
        BasicHttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECT_TIME_OUT);
        HttpConnectionParams.setSoTimeout(httpParameters, SO_TIME_OUT);
        HttpClient client = new DefaultHttpClient(httpParameters);
        client.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, Charset.forName("UTF-8"));
        HttpPost httpPost = new HttpPost(apiUrl);

        MultipartEntity multipartEntity = new MultipartEntity();
    }
}
