package com.zsoft.parallelhttpclient;

import android.os.AsyncTask;
import android.os.Build;

import com.turbomanage.httpclient.AsyncCallback;
import com.turbomanage.httpclient.AsyncHttpClient;
import com.turbomanage.httpclient.HttpRequest;
import com.turbomanage.httpclient.android.DoHttpRequestTask;

public class DoParallelHttpRequestTask extends DoHttpRequestTask {

	public DoParallelHttpRequestTask(AsyncHttpClient httpClient,
			AsyncCallback callback) {
		super(httpClient, callback);
		// TODO Auto-generated constructor stub
	}

	 public void execute(HttpRequest httpRequest) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            super.execute(httpRequest);
        } else {
            super.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, httpRequest);
        }
    }
}
