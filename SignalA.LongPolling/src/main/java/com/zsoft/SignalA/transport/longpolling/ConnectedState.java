package com.zsoft.signala.transport.longpolling;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import org.json.JSONObject;

import android.util.Log;

import com.turbomanage.httpclient.AsyncCallback;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;
import com.zsoft.signala.ConnectionBase;
import com.zsoft.signala.ConnectionState;
import com.zsoft.signala.SignalAUtils;
import com.zsoft.signala.SendCallback;
import com.zsoft.signala.transport.ProcessResult;
import com.zsoft.signala.transport.TransportHelper;
import com.zsoft.parallelhttpclient.ParallelHttpClient;

public class ConnectedState extends StopableStateWithCallback {
	protected static final String TAG = "ConnectedState";
	private Object mCallbackLock = new Object();
	//@SuppressWarnings("unused")
	//private AjaxCallback<JSONObject> mCurrentCallback = null;
	
	public ConnectedState(ConnectionBase connection) {
		super(connection);
	}

	@Override
	public ConnectionState getState() {
		return ConnectionState.Connected;
	}

	@Override
	public void Start() {
	}

	@Override
	public void Stop() {
		mConnection.SetNewState(new DisconnectingState(mConnection));
		super.Stop();
	}

	@Override
	public void Send(final CharSequence text, final SendCallback sendCb) {
		if(DoStop())
		{
			sendCb.OnError(new Exception("Connection is about to close"));
			return; 
		}

        String url = SignalAUtils.EnsureEndsWith(mConnection.getUrl(), "/") + "send";
        String connectionData = mConnection.OnSending();
        url += TransportHelper.GetSendQueryString(mConnection, connectionData, TRANSPORT_NAME);
        TransportHelper.AppendCustomQueryString(mConnection, url);

		AsyncCallback cb = new AsyncCallback() 
		{
            @Override
            public void onComplete(HttpResponse httpResponse) 
            {
            	if(httpResponse.getStatus() == 200)
            	{
					Log.v(TAG, "Message sent: " + text);
					sendCb.OnSent(text);
					
					JSONObject json = JSONHelper.ToJSONObject(httpResponse.getBodyAsString());
					
					if(json!=null && json.length()>0)
            		{
            			mConnection.setMessage(json);
            		}
            	}
				else
				{
					Exception ex = new Exception("Error sending message");
					mConnection.setError(ex);
					sendCb.OnError(ex);
				}
            }
            @Override
            public void onError(Exception ex) {
				mConnection.setError(ex);
				sendCb.OnError(ex);
            }
		};

		ParallelHttpClient httpClient = new ParallelHttpClient();
        for (Map.Entry<String, String> entry : mConnection.getHeaders().entrySet())
        {
            httpClient.addHeader(entry.getKey(), entry.getValue());
        }
		ParameterMap params = httpClient.newParams()
		        						.add("data", text.toString());
        httpClient.setMaxRetries(1);
        httpClient.post(url, params, cb);
	}

	@Override
	protected void OnRun() {

		if(DoStop()) return; 

	    String url = SignalAUtils.EnsureEndsWith(mConnection.getUrl(), "/") + "poll";
		String connectionData = mConnection.OnSending();
	    url += TransportHelper.GetReceiveQueryString(mConnection, connectionData, TRANSPORT_NAME);

	    AsyncCallback cb = new AsyncCallback() {
            @Override
            public void onComplete(HttpResponse httpResponse) {
    			if(DoStop()) return; 

            	try
            	{
        			if(httpResponse != null && httpResponse.getStatus() == 200)
        			{
                		JSONObject json = JSONHelper.ToJSONObject(httpResponse.getBodyAsString());
                    	if (json!=null)
                   		{
                			ProcessResult result = TransportHelper.ProcessResponse(mConnection, json);

                			if(result.processingFailed)
                			{
                				mConnection.setError(new Exception("Error while processing response."));
                				mConnection.SetNewState(new ReconnectingState(mConnection));
                			}
                			else if(result.disconnected)
                			{
	      						mConnection.SetNewState(new DisconnectedState(mConnection));
	    						return;
                			}
                		}
                		else
                		{
						    mConnection.setError(new Exception("Error when calling endpoint. Returncode: " + httpResponse.getStatus()));
							mConnection.SetNewState(new ReconnectingState(mConnection));
                   		}
        			}
            		else
            		{
					    mConnection.setError(new Exception("Error when calling endpoint."));
						mConnection.SetNewState(new ReconnectingState(mConnection));
               		}
            	}
            	finally
            	{
					mIsRunning.set(false);
	
					// Loop if we are still connected
					if(mConnection.getCurrentState() == ConnectedState.this)
						Run();
            	}
            }
            @Override
            public void onError(Exception ex) {
			    mConnection.setError(ex);
				mConnection.SetNewState(new ReconnectingState(mConnection));
            }
	    };

		
		synchronized (mCallbackLock) {
			//mCurrentCallback = cb;
		}

		ParallelHttpClient httpClient = new ParallelHttpClient();
		httpClient.setMaxRetries(1);
		httpClient.setConnectionTimeout(5000);
		httpClient.setReadTimeout(115000);
        for (Map.Entry<String, String> entry : mConnection.getHeaders().entrySet())
        {
            httpClient.addHeader(entry.getKey(), entry.getValue());
        }
		ParameterMap params = httpClient.newParams();
		httpClient.post(url, params, cb);
	}

}
