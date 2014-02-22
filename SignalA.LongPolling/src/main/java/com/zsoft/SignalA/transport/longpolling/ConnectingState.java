package com.zsoft.signala.transport.longpolling;

import org.json.JSONException;
import org.json.JSONObject;

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

public class ConnectingState extends StopableStateWithCallback {
	public ConnectingState(ConnectionBase connection) {
		super(connection);
	}

	@Override
	public ConnectionState getState() {
		return ConnectionState.Connecting;
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

        // connect
        String url = SignalAUtils.EnsureEndsWith(mConnection.getUrl(), "/") + "connect";
        String connectionData = mConnection.OnSending();
        url += TransportHelper.GetReceiveQueryString(mConnection, connectionData, TRANSPORT_NAME);
        TransportHelper.AppendCustomQueryString(mConnection, url);

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
                                mConnection.setError(new Exception("Disconnected by server."));
                                mConnection.SetNewState(new DisconnectedState(mConnection));
                            }
                            else if(result.initialized)
                            {
                                mConnection.SetNewState(new ConnectedState(mConnection));
                            }
                            else
                            {
                                mConnection.SetNewState(new DisconnectedState(mConnection));
                            }
                        }
                        else
                        {
                            mConnection.setError(new Exception("Error when calling endpoint. Return code: " + httpResponse.getStatus()));
                            mConnection.SetNewState(new DisconnectedState(mConnection));
                        }
                    }
                    else
                    {
                        mConnection.setError(new Exception("Error when calling endpoint."));
                        mConnection.SetNewState(new DisconnectedState(mConnection));
                    }
                }
                finally
                {
                    mIsRunning.set(false);
                }
			}
            @Override
            public void onError(Exception ex) {
				mConnection.setError(ex);
				mConnection.SetNewState(new DisconnectedState(mConnection));
            }
		};
		
		synchronized (mCallbackLock) {
			//mCurrentCallback = cb;
		}
		
		ParallelHttpClient httpClient = new ParallelHttpClient();
        httpClient.setMaxRetries(1);
        for (Map.Entry<String, String> entry : mConnection.getHeaders().entrySet())
        {
            httpClient.addHeader(entry.getKey(), entry.getValue());
        }
        httpClient.get(url, null, cb);
	}

}
