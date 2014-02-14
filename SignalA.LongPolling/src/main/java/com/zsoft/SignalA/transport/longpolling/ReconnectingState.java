package com.zsoft.signala.transport.longpolling;

import org.json.JSONObject;

import android.os.Handler;

import com.turbomanage.httpclient.AsyncCallback;
import com.turbomanage.httpclient.HttpResponse;
import com.zsoft.signala.ConnectionBase;
import com.zsoft.signala.ConnectionState;
import com.zsoft.signala.SignalAUtils;
import com.zsoft.signala.SendCallback;
import com.zsoft.signala.transport.ProcessResult;
import com.zsoft.signala.transport.TransportHelper;
import com.zsoft.parallelhttpclient.ParallelHttpClient;

import java.util.Map;

public class ReconnectingState extends StopableStateWithCallback {

	public ReconnectingState(ConnectionBase connection) {
		super(connection);
	}

	@Override
	public ConnectionState getState() {
		return ConnectionState.Reconnecting;
	}

	@Override
	public void Start() {
	}

	@Override
	public void Send(CharSequence text, SendCallback callback) {
		callback.OnError(new Exception("Not connected"));
	}

	@Override
	protected void OnRun() {
		if(DoStop()) return;

	    if (mConnection.getMessageId() == null)
		{
	    	// No message received yet....connect instead of reconnect
			mConnection.SetNewState(new ConnectingState(mConnection));
			return;
		}
	    
	    String url = SignalAUtils.EnsureEndsWith(mConnection.getUrl(), "/");
		url += "reconnect";
	    url += TransportHelper.GetReceiveQueryString(mConnection, null, TRANSPORT_NAME);

		AsyncCallback cb = new AsyncCallback() {
			
			@Override
			public void onComplete(HttpResponse httpResponse) {
				if(DoStop()) return; 

                try
                {
                	if(httpResponse.getStatus()==200)
                	{
                		JSONObject json = JSONHelper.ToJSONObject(httpResponse.getBodyAsString());
	                    if (json!=null)
	                    {
	                		ProcessResult result = TransportHelper.ProcessResponse(mConnection, json);
	
	                		if(result.processingFailed)
	                		{
	                    		mConnection.setError(new Exception("Error while proccessing response."));
	                		}
	                		else if(result.disconnected)
	                		{
	      						mConnection.SetNewState(new DisconnectedState(mConnection));
	    						return;
	                		}
	                    }
	                    else
	                    {
						    mConnection.setError(new Exception("Error when parsing response to JSONObject."));
	                    }
                    }
                    else
                    {
					    mConnection.setError(new Exception("Error when calling endpoint. Returncode: " + httpResponse.getStatus()));
                    }
                }
                finally
                {
					if(mConnection.getCurrentState() == ReconnectingState.this)
					{
						// Delay before reconnecting
						Delay(2000, new DelayCallback() {
							
							@Override
							public void OnStopedBeforeElapsed() {
								mIsRunning.set(false);
								mConnection.SetNewState(new DisconnectedState(mConnection));
							}
							
							@Override
							public void OnDelayElapsed() {
								mIsRunning.set(false);
								// Loop if we are still reconnecting
								Run();
							}
						});
					}
                }
			}
           @Override
            public void onError(Exception ex) {
				mConnection.setError(ex);
				
			}
		};

		
		synchronized (mCallbackLock) {
			//mCurrentCallback = cb;
		}

		ParallelHttpClient httpClient = new ParallelHttpClient();
        httpClient.setMaxRetries(1);
        httpClient.setConnectionTimeout(15000);
	    httpClient.setReadTimeout(15000);
        for (Map.Entry<String, String> entry : mConnection.getHeaders().entrySet())
        {
            httpClient.addHeader(entry.getKey(), entry.getValue());
        }
        httpClient.post(url, null, cb);
	}

	protected void Delay(final long milliSeconds, final DelayCallback cb) {
		final long startTime = System.currentTimeMillis();
		final Handler handler = new Handler();
		final Runnable runnable = new Runnable() {
		  @Override
		  public void run() {
			  if(DoStop()) {
				  cb.OnStopedBeforeElapsed();
			  }
			  else {
				  long difference = System.currentTimeMillis() - startTime;
				  if(difference < milliSeconds) {
					  handler.postDelayed(this, 500);
				  }
				  else {
					  	cb.OnDelayElapsed();
				  }
			  }
				  
		  }
		};
		
		handler.postDelayed(runnable, 500);
	}

	private abstract class DelayCallback {
		public abstract void OnDelayElapsed();
		public abstract void OnStopedBeforeElapsed();
	}
	
}
