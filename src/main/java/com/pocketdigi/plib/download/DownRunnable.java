package com.pocketdigi.plib.download;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.pocketdigi.plib.core.PApplication;
import com.pocketdigi.plib.core.PLog;
import com.pocketdigi.plib.util.DateUtils;
import com.pocketdigi.plib.util.FileUtils;
import com.pocketdigi.plib.util.StorageUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Locale;

/**
 * 下载Runnable
 * Created by fhp on 14-9-17.
 */
public class DownRunnable implements Runnable {
    DownTask task;
    DownloadListener listener;
    static Handler handler;
    String tmpFilePath;

    public DownRunnable(DownTask task, DownloadListener listener) {
        this.task = task;
        this.listener = listener;
        tmpFilePath = task.getSavePath() + ".tmp";
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void run() {
        if (task.isCancel()) {
            task.setState(DownTask.STATE_CANCELED);
            listener.onCancel(task);
            return;
        }
        /**
         *  当用户下载一条时，因为多线程，可能会有这样的情况：
         *  start回调的修改状态先于add的修改状态，导致状态被从默认的暂停改成下载后，又被改成等待
         *  结果就是下载图片一直显示等待，直到下载完成，所以，这里延迟
         */
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        taskStart();
        if (task.getSavePath().startsWith("/data/data")) {
            if (StorageUtils.getInternalStorageAvailableSize(PApplication.getInstance()) < 10 * 1024 * 1024) {
                taskFailure(DownloadListener.ERROR_CODE_DISK_FULL);
                return;
            }
        } else {
            if (StorageUtils.getExternalStorageAvailableSize() < 50 * 1024 * 1024) {
                taskFailure(DownloadListener.ERROR_CODE_DISK_FULL);
                return;
            }
        }

        PLog.d(this, "下载文件" + task.getUrl());
        PLog.d(this, "保存到" + tmpFilePath);


        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(task.getUrl());
            connection = openConnection(url);

            long saveFileLastModified;
            long remoteLastModifiedTimestamp = 0;
            if (FileUtils.isFileExist(task.getSavePath())) {
                //若文件存在，读lastmodify
                File saveFile = new File(task.getSavePath());
                saveFileLastModified = saveFile.lastModified();
                String remoteLastModified = connection.getHeaderField("Last-Modified");
                if (!TextUtils.isEmpty(remoteLastModified)) {
                    Date date = DateUtils.str2Date("EEE, dd MMM yyyy HH:mm:ss zzz", remoteLastModified, Locale.ENGLISH);
                    if (date != null) {
                        remoteLastModifiedTimestamp = date.getTime();
                    }
                }
                //如果时间相同，说明文件一样，不用下载
                if (remoteLastModifiedTimestamp == saveFileLastModified) {
                    PLog.d(this,"文件LastModify相同,不再下载");
                    taskSuccess();
                    connection.disconnect();
                    return;
                } else {
                    PLog.d(this,"文件LastModify不同,重新下载:本地"+saveFileLastModified+"远程："+remoteLastModifiedTimestamp);
                    //文件不一样，删除
                    FileUtils.deleteFile(task.getSavePath());
                }
            }



//            RandomAccessFile tmpFile = new RandomAccessFile(tmpFilePath, "rw");
            long totalFileSize, downloadedSize = 0;
            //某些服务器不支持断点续传
//            //断点续传 connection.setRequestProperty("RANGE","bytes="+100);tomcat会返回404,暂时去掉
//            if (FileUtils.isFileExist(tmpFilePath)) {
//                totalFileSize = getFullFileSize();
//                long tmpFileSize = new File(tmpFilePath).length();
//                tmpFile.seek(tmpFileSize);
//                connection.setRequestProperty("RANGE", "bytes=" + tmpFileSize + "-");
//                downloadedSize = tmpFileSize;
//                PLog.d(this, "文件已存在，断点续传，从" + tmpFileSize + "开始");
//            } else {
//                totalFileSize = connection.getContentLength();
//            }

            totalFileSize=connection.getContentLength();
            FileUtils.deleteFile(tmpFilePath);
            FileOutputStream fos=new FileOutputStream(tmpFilePath);


            task.setFileSize(totalFileSize);
            inputStream = connection.getInputStream();
            int bufferSize = 4096;
            byte[] buffer = new byte[bufferSize];
            int readedLength = 0;
            long t1 = System.currentTimeMillis();
            long speedCount = 0;
            while ((readedLength = inputStream.read(buffer, 0, bufferSize)) > 0 && !task.isCancel()) {
                downloadedSize += readedLength;
                fos.write(buffer, 0, readedLength);
//                tmpFile.write(buffer, 0, readedLength);
                task.setDownloadedSize(downloadedSize);
                speedCount += readedLength;
                long t2 = System.currentTimeMillis();
                //每秒发送一次进度改变事件
                if (t2 - t1 > 1000) {
                    t1 = t2;
                    taskProgressChanged();
                    PLog.d(this, "下载速度: " + speedCount / 1024 + " KB 进度: " + downloadedSize * 100 / totalFileSize);
                    speedCount = 0;
                }
            }
            fos.close();

            if (task.isCancel()) {
                PLog.d(this, "下载取消" + task.getId() + " " + task.getUrl());
                taskCancel();
            } else {
                PLog.d(this,"下载成功,修改lastModified");
                File tmpDownloadFile=new File(tmpFilePath);
                tmpDownloadFile.setLastModified(remoteLastModifiedTimestamp);
                FileUtils.rename(tmpFilePath, task.getSavePath());
                taskSuccess();
            }

        } catch (Exception e) {
            e.printStackTrace();
            task.setState(DownTask.STATE_FAIL);
            taskFailure(DownloadListener.ERROR_CODE_IO);
        } finally {
            if (connection != null)
                connection.disconnect();
            if (inputStream != null)
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

    }

    private void taskFailure(final int errorCode) {
        if (task.isDeleteFile()) {
            FileUtils.deleteFile(tmpFilePath);
        }
        PLog.d(this, "下载失败" + task.getId() + task.getUrl() + " 错误代码 " + errorCode);
        handler.post(new Runnable() {
            @Override
            public void run() {
                task.setState(DownTask.STATE_FAIL);
                listener.onFail(task, errorCode);
            }
        });
    }

    private void taskSuccess() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                PLog.d(this, "下载成功 " + task.getId() + " " + task.getUrl());
                task.setState(DownTask.STATE_SUCCESS);
                listener.onComplete(task);
            }
        });
    }

    private void taskCancel() {
        //删除临时文件
        if (task.isDeleteFile()) {
            FileUtils.deleteFile(tmpFilePath);
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                task.setState(DownTask.STATE_CANCELED);
                listener.onCancel(task);
            }
        });
    }

    private void taskProgressChanged() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.onProgressChanged(task);
            }
        });
    }

    private void taskStart() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                task.setState(DownTask.STATE_DOWNING);
                listener.onStart(task);
            }
        });
    }

    private HttpURLConnection createConnection(URL url) throws IOException {
        return (HttpURLConnection) url.openConnection();
    }


    private HttpURLConnection openConnection(URL url) throws IOException {
        HttpURLConnection connection = createConnection(url);
        connection.setConnectTimeout(DownloadManager.CONNECT_TIMEOUT);
        connection.setReadTimeout(DownloadManager.READ_TIMEOUT);
        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Referer", "http://www.fwvga.com/");
        return connection;
    }

    /**
     * 获取完整的文件大小
     *
     * @return
     */
    private long getFullFileSize() {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(task.getUrl());
            connection = openConnection(url);
            long remoteFileSize = connection.getContentLength();
            return remoteFileSize;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null)
                connection.disconnect();
        }
        return 0;
    }
}