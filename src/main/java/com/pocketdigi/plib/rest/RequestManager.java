package com.pocketdigi.plib.rest;

import android.os.Handler;
import android.os.Looper;

import com.pocketdigi.plib.core.PLog;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by fhp on 16/5/31.
 */
public class RequestManager implements ResponseErrorHandler {
    private static RequestManager ourInstance;
    //参数处理器
    private AbstractParameterProcessor parameterProcessor;
    private AbstractHeaderProcessor headerProcessor;

    public static RequestManager getInstance() {
        if (ourInstance == null)
            ourInstance = new RequestManager();
        return ourInstance;
    }

    private RestTemplate restTemplate;

    private static Handler handler;

    private RequestManager() {
        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().clear();
        restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
        restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());
        restTemplate.setInterceptors(new ArrayList<ClientHttpRequestInterceptor>());
        restTemplate.getInterceptors().add(new RESTHttpInterceptor());
//        restTemplate.setErrorHandler(this);

        handler = new Handler(Looper.getMainLooper());
        parameterProcessor = new DefaultParameterProcessor();
        headerProcessor = new DefaultHeaderProcessor();
    }


    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT + 1;
    private static final int KEEP_ALIVE = 1;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "Request #" + mCount.getAndIncrement());
        }
    };

    private static final BlockingQueue<Runnable> sPoolWorkQueue =
            new LinkedBlockingQueue<>(128);

    /**
     * An {@link java.util.concurrent.Executor} that can be used to execute tasks in parallel.
     */
    public static final Executor THREAD_POOL_EXECUTOR
            = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE,
            TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);
    ExecutorService executorService = Executors.newCachedThreadPool();


    /**
     * 添加请求
     *
     * @param request
     * @param <T>
     */
    public <T> void addRequest(final RESTRequest<T> request) {
        THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                parameterProcessor.processParameter(request);
                headerProcessor.processHeader(request);
                PLog.d("http", "Request: url:" + request.getUrl());
                HttpEntity<MultiValueMap<String, Object>> requestEntity;
                if (request.getMethod() == HttpMethod.GET) {
                    requestEntity = new HttpEntity<>(request.getHeaders());
                } else {
                    requestEntity = new HttpEntity<>(request.getParams(), request.getHeaders());
                }
                try {
                    final T responseObject = restTemplate.exchange(request.getUrl(), request.getMethod(), requestEntity, request.getResponseClazz()).getBody();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            RESTResponseListener<T> restResponseListener = request.getResponseListener();
                            if(restResponseListener !=null)
                                restResponseListener.onResponse(responseObject);
                        }
                    });

                } catch (RestClientException e) {
                    RESTResponseListener<T> RESTResponseListener = request.getResponseListener();

                    if (e instanceof HttpStatusCodeException) {
                        HttpStatusCodeException httpStatusCodeException = (HttpStatusCodeException) e;
                        String responseText = httpStatusCodeException.getResponseBodyAsString();
                        PLog.e(this, "http错误:"+httpStatusCodeException.getStatusCode());
                        PLog.e(this, responseText);
                        if(RESTResponseListener !=null)
                            RESTResponseListener.onError(httpStatusCodeException.getStatusCode(), responseText);
                    }else{
                        PLog.e(this, "IO错误");
                        if(RESTResponseListener !=null)
                            RESTResponseListener.onError(null,null);
                    }
                    e.printStackTrace();
                }

            }
        });
    }

    public AbstractParameterProcessor getParameterProcessor() {
        return parameterProcessor;
    }

    /**
     * 设置参数处理器
     *
     * @param parameterProcessor
     */
    public void setParameterProcessor(AbstractParameterProcessor parameterProcessor) {
        this.parameterProcessor = parameterProcessor;
    }

    public AbstractHeaderProcessor getHeaderProcessor() {
        return headerProcessor;
    }

    /**
     * 设置Header处理器
     *
     * @param headerProcessor
     */
    public void setHeaderProcessor(AbstractHeaderProcessor headerProcessor) {
        this.headerProcessor = headerProcessor;
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return false;
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {

    }
}
