package com.zsoft.parallelhttpclient;

import java.net.HttpURLConnection;

import android.os.Build;

import com.turbomanage.httpclient.AsyncHttpClient;
import com.turbomanage.httpclient.RequestHandler;
import com.turbomanage.httpclient.RequestLogger;

public class ParallelHttpClient extends AsyncHttpClient {


    static {
        disableConnectionReuseIfNecessary();
        // See http://code.google.com/p/basic-http-client/issues/detail?id=8
        if (Build.VERSION.SDK_INT > 8)
        		ensureCookieManager();
    }

    /**
     * Constructs a new client with empty baseUrl. When used this way, the path
     * passed to a request method must be the complete URL.
     */
    public ParallelHttpClient() {
        this("");
    }

    /**
     * Constructs a new client using the default {@link RequestHandler} and
     * {@link RequestLogger}.
     */
    public ParallelHttpClient(String baseUrl) {
        super(new ParallelAsyncTaskFactory(), baseUrl);
    }

    /**
     * Constructs a client with baseUrl and custom {@link RequestHandler}.
     *
     * @param baseUrl
     * @param requestHandler
     */
    public ParallelHttpClient(String baseUrl, RequestHandler requestHandler) {
        super(new ParallelAsyncTaskFactory(), baseUrl, requestHandler);
    }

    /**
     * Work around bug in {@link HttpURLConnection} on older versions of
     * Android.
     * http://android-developers.blogspot.com/2011/09/androids-http-clients.html
     */
    private static void disableConnectionReuseIfNecessary() {
        // HTTP connection reuse which was buggy pre-froyo
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
            System.setProperty("http.keepAlive", "false");
        }
    }
}
