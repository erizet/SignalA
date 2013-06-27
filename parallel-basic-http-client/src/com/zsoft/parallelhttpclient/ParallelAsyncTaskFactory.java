package com.zsoft.parallelhttpclient;

import android.os.AsyncTask;

import com.turbomanage.httpclient.AsyncCallback;
import com.turbomanage.httpclient.AsyncHttpClient;
import com.turbomanage.httpclient.AsyncRequestExecutor;
import com.turbomanage.httpclient.AsyncRequestExecutorFactory;

/**
 * Android-specific factory produces an {@link AsyncTask} that can 
 * execute an HTTP request in parallel with other AsyncTasks. 
 * 
 * @author Erik Zetterqvist
 */
public class ParallelAsyncTaskFactory implements AsyncRequestExecutorFactory {


    @Override
    public AsyncRequestExecutor getAsyncRequestExecutor(AsyncHttpClient client,
            AsyncCallback callback) {
        return new DoParallelHttpRequestTask(client, callback);
    }

}
