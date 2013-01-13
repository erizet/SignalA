package com.zsoft.SignalA.transport.longpolling;

import org.json.JSONException;
import org.json.JSONObject;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.zsoft.SignalA.Connection;
import com.zsoft.SignalA.ConnectionState;
import com.zsoft.SignalA.SignalAUtils;
import com.zsoft.SignalA.SendCallback;

public class ConnectingState extends StopableStateWithCallback {
	public ConnectingState(Connection connection) {
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
		AQuery aq = new AQuery(mConnection.getContext());
		
		if(DoStop()) return; 

        // negotiate
		String url = SignalAUtils.EnsureEndsWith(mConnection.getUrl(), "/") + "negotiate";
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>() {
			@Override
			public void callback(String url, JSONObject json, AjaxStatus status) {
				try
				{
					if(DoStop()) return; 

					if(json != null){
						String connectionId="";
						String protocolVersion = "";
						try {
							connectionId = json.getString("ConnectionId");
							protocolVersion = json.getString("ProtocolVersion");
	
							if(mConnection.VerifyProtocolVersion(protocolVersion))
							{
								mConnection.setConnectionId(connectionId);
								mConnection.SetNewState(new ConnectedState(mConnection));
								return;
							}
							else
							{
								mConnection.OnError(new Exception("Not supported protocol version."));
								mConnection.SetNewState(new DisconnectedState(mConnection));
								return;
							}
						
						} catch (JSONException e) {
							mConnection.OnError(new Exception("Unable to parse negotiation response."));
							return;
						}
					}
					else
					{
						mConnection.SetNewState(new DisconnectedState(mConnection));
					}
				}
				finally
				{
					mIsRunning.set(false);
				}
			}
		};
		
		synchronized (mCallbackLock) {
			mCurrentCallback = cb;
		}
		//cb.timeout(10000);
		aq.ajax(url, JSONObject.class, cb);

	}

}
