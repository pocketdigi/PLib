package com.pocketdigi.plib.upload;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;

import android.text.TextUtils;

import com.pocketdigi.plib.core.PLog;


/**
 * 上传管理
 * 添加上传任务：addTask
 * 删除上传任务 UploadTask.setCancel
 */
public class UploadManager {
	final String TAG = "ImageUploadManager";
	// 避免与其他asynctask共用线程池
	LinkedBlockingQueue<Runnable> blockingQueue = new LinkedBlockingQueue<>();
	ThreadPoolExecutor threadPoolExecutor;
	private static final int CONNECT_TIME_OUT = 2 * 1000;
	private static final int SO_TIME_OUT = 10 * 1000;
	boolean isClosed = false;
	boolean isAllFinish = true;
	OnUploadListener uploadListener;
	Set<UploadTask> uploadingSet = new HashSet<>();
	Set<UploadTask> waitingSet = new HashSet<>();
    WeakHashMap<UploadTask,UploadProgress> uploadProgressMap;
    private static UploadManager instance;
	private UploadManager() {
        uploadProgressMap=new WeakHashMap<>();
	}
    public static UploadManager getInstance() {
        if (instance == null) {
            instance = new UploadManager();
        }
        return instance;
    }

	/**
	 * 添加上传任务
	 */
	public void addTask(UploadTask uploadTask) {
		if (threadPoolExecutor == null)
			threadPoolExecutor = new ThreadPoolExecutor(1, 3, 10L, TimeUnit.SECONDS, blockingQueue);
		if (isAllFinish && uploadListener != null) {
			uploadListener.onUploadStart();
		}
        isAllFinish = false;
		isClosed = false;
		// 添加到等待集合
		waitingSet.add(uploadTask);
        uploadProgressMap.put(uploadTask,new UploadProgress(uploadTask));
		new Uploader().executeOnExecutor(threadPoolExecutor, uploadTask);
	}


	class Uploader extends AsyncTask<UploadTask, UploadProgress, UploadProgress> {

        UploadTask uploadTask;
		@Override
		protected UploadProgress doInBackground(UploadTask... params) {
            uploadTask = params[0];
            UploadProgress uploadProgress=uploadProgressMap.get(uploadTask);
			// 从等待中取出，放入uploading
			waitingSet.remove(uploadTask);
			uploadingSet.add(uploadTask);

			// 如果是被取消的任务，不执行
			if (uploadTask.isCancel()) {
				return uploadProgress;
			}
			if (isClosed) {
			    uploadProgress.setState(UploadProgress.STATE_UPLOAD_FAILURE);
				return uploadProgress;
			}
			uploadProgress.setState(UploadProgress.STATE_UPLOADING);
			String urlStr = uploadTask.getApiUrl();
			if (!TextUtils.isEmpty(urlStr)) {

				BasicHttpParams httpParameters = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECT_TIME_OUT);
				HttpConnectionParams.setSoTimeout(httpParameters, SO_TIME_OUT);

				HttpClient client = new DefaultHttpClient(httpParameters);
				client.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, Charset.forName("UTF-8"));
				HttpPost httpPost = new HttpPost(urlStr);
				MultipartEntity multipartEntity = new UploadMultipartEntity(uploadProgress);
                //加文件或二进制
                if(!TextUtils.isEmpty(uploadTask.getFilePath())) {
                    //文件
                    FileBody cbFileBody = new FileBody(new File(uploadTask.getFilePath()));
                    multipartEntity.addPart(uploadTask.getFileArgName(), cbFileBody);
                }else if(uploadTask.getFileBytes()!=null){
                    //文件名服务端一般会重命名，固定file.jpg
                    ByteArrayBody byteArrayBody=new ByteArrayBody(uploadTask.getFileBytes(),"file.jpg");
                    multipartEntity.addPart(uploadTask.getFileArgName(), byteArrayBody);
                }else{
                    throw new IllegalArgumentException("文件路径和byte数组都是空，无法上传，请检查传入的UploadTask");
                }

                //添加其他参数
                Map<String, String> otherParams = uploadTask.getOtherParams();
                if(otherParams!=null) {
                    Set<Map.Entry<String, String>> entrySet = otherParams.entrySet();
                    for (Map.Entry<String, String> entry : entrySet) {
                        try {
                            multipartEntity.addPart(entry.getKey(), new StringBody(entry.getValue(), Charset.forName("UTF-8")));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                //设置Header
                Map<String, String> headers = uploadTask.getHeaders();
                if(headers!=null) {
                    ArrayList<Header> headerArrayList=new ArrayList<>();
                    Set<Map.Entry<String, String>> entrySet = headers.entrySet();
                    for (Map.Entry<String, String> entry : entrySet) {
                        headerArrayList.add(new BasicHeader(entry.getKey(),entry.getValue()));
                    }
                    httpPost.setHeaders(headerArrayList.toArray(new Header[headers.size()]));
                }

                uploadProgress.setFileSize(multipartEntity.getContentLength());
				httpPost.setEntity(multipartEntity);

				HttpResponse response;
				String content;
				try {
					response = client.execute(httpPost);
					PLog.d(TAG, "上传结束 ");
					content = EntityUtils.toString(response.getEntity());
                    PLog.d(TAG, content);
                    uploadProgress.setResponse(content);
                    uploadProgress.setState(UploadProgress.STATE_UPLOADED);
					return uploadProgress;
				} catch (HttpHostConnectException e) {
					e.printStackTrace();
					uploadProgress.setState(UploadProgress.STATE_UPLOAD_FAILURE);
					PLog.d(TAG, "网络错误");
					return uploadProgress;
				} catch (Exception e) {
					e.printStackTrace();
					uploadProgress.setState(UploadProgress.STATE_UPLOAD_FAILURE);
                    PLog.d(TAG, "其他错误");
					return uploadProgress;
				} finally {
					client.getConnectionManager().shutdown();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(UploadProgress result) {
			super.onPostExecute(result);
			// 正常流程后执行，比如上传成功，上传异常
			uploadingSet.remove(uploadTask);
            uploadProgressMap.remove(uploadTask);
			if (getNotCancelSize(uploadingSet) == 0 && getNotCancelSize(waitingSet) == 0) {
                if(uploadListener!=null)
				    uploadListener.onAllFinish();
                uploadingSet.clear();
                waitingSet.clear();
                uploadProgressMap.clear();
				isAllFinish = true;
			}
		}
	}

	/**
	 * 清除未上传的任务
	 */
	public void clear() {
		isClosed = true;
		blockingQueue.clear();
		isAllFinish = true;
		if (threadPoolExecutor != null) {
			threadPoolExecutor.shutdownNow();
			threadPoolExecutor = null;
		}
		uploadingSet.clear();
		waitingSet.clear();
	}

	public OnUploadListener getUploadListener() {
		return uploadListener;
	}

	/**
	 * 设置上传回调监听器
	 * 
	 * @param uploadListener
	 */
	public void setUploadListener(OnUploadListener uploadListener) {
		this.uploadListener = uploadListener;
	}

	public interface OnUploadListener {
		public void onUploadStart();

		public void onAllFinish();
	}

	/**
	 * 返回set中的有效数据
	 * 
	 * @return
	 */
	public int getNotCancelSize(Set<UploadTask> taskSet) {
		int count = 0;
		for (UploadTask task : taskSet) {
			if (!task.isCancel())
				count++;
		}
		return count;
	}

}
