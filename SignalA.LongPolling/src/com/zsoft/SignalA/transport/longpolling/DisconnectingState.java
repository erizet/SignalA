package com.zsoft.SignalA.transport.longpolling;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.Constants;
import com.zsoft.SignalA.Connection;
import com.zsoft.SignalA.ConnectionState;
import com.zsoft.SignalA.SignalAUtils;
import com.zsoft.SignalA.Transport.StateBase;
import com.zsoft.SignalA.SendCallback;

public class DisconnectingState extends StateBase {

	protected static final String TAG = "DisconnectingState";

	public DisconnectingState(Connection connection) {
		super(connection);
	}

	@Override
	public ConnectionState getState() {
		return ConnectionState.Disconnecting;
	}

	@Override
	public void Start() {
	}

	@Override
	public void Stop() {
	}

	@Override
	public void Send(CharSequence text, SendCallback callback) {
		callback.OnError(new Exception("Not connected"));
	}
	
	@Override
	protected void OnRun() {
		AQuery aq = new AQuery(mConnection.getContext());
	    String url = SignalAUtils.EnsureEndsWith(mConnection.getUrl(), "/") +  "abort?transport=LongPolling&connectionId=" + mConnection.getConnectionId();

		AjaxCallback<String> cb = new AjaxCallback<String>() {
			@Override
			public void callback(String url, String result, AjaxStatus status) {
				if(result == null){
					Log.e(TAG, "Clean disconnect failed. " + status.getCode());
				}
				
				mConnection.SetNewState(new DisconnectedState(mConnection));
			}
		};
		
		Map<String, Object> params = new HashMap<String, Object>();
		cb.url(url).type(String.class).expire(-1).params(params).method(Constants.METHOD_POST); //.timeout(5000);
		aq.ajax(cb);
	}

}
