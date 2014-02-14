package com.zsoft.signala.hubs;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;

import android.util.Log;

import com.zsoft.signala.SendCallback;

public class HubProxy implements IHubProxy {
    protected static final String TAG = "HubProxy";
	private String mHubName;
    private HubConnection mConnection;
    //private Map<string, JSONObject> _state = new Dictionary<string, JToken>(StringComparer.OrdinalIgnoreCase);
    private Map<String, HubOnDataCallback> mSubscriptions = new HashMap<String, HubOnDataCallback>();

	public HubProxy(HubConnection hubConnection, String hubName) {
		mConnection = hubConnection;
		mHubName = hubName;
	}

	
	public void Invoke(final String method, Collection<?> args,
			HubInvokeCallback callback) {

		Invoke(method, new JSONArray(args), callback);
	}

	// Executes a method on the server asynchronously
	@Override
	public void Invoke(final String method, JSONArray args,
			HubInvokeCallback callback) {

		if (method == null)
        {
            throw new IllegalArgumentException("method");
        }

        if (args == null)
        {
            throw new IllegalArgumentException("args");
        }

        final String callbackId = mConnection.RegisterCallback(callback);

        HubInvocation hubData = new HubInvocation(mHubName, method, args, callbackId);

        String value = hubData.Serialize();

        mConnection.Send(value, new SendCallback() 
        {
			@Override
			public void OnSent(CharSequence messageSent) {
				Log.v(TAG, "Invoke of " + method + "sent to " + mHubName);
				// callback.OnSent() ??!?!?
			}

			@Override
			public void OnError(Exception ex) {
				// TODO Cancel the callback
				Log.e(TAG, "Failed to invoke " + method + "on " + mHubName);
				mConnection.RemoveCallback(callbackId);
				// callback.OnError() ?!?!?
			}
        });
    }

	

	public void On(String eventName, HubOnDataCallback callback) 
	{
		Subscribe(eventName, callback);
	}
	
	
	public void Subscribe(String eventName, HubOnDataCallback callback)
	{
		if(eventName==null) throw new IllegalArgumentException("eventName can not be null");
		if(callback==null) throw new IllegalArgumentException("callback can not be null");

		eventName = eventName.toLowerCase(Locale.US);
		
		if(!mSubscriptions.containsKey(eventName))
		{
			mSubscriptions.put(eventName, callback);
		}
	}
	
	// K�r event lokalt som anropas fr�n servern och som registrerats i ON-metod.
    public void InvokeEvent(String eventName, JSONArray args)
    {
        HubOnDataCallback subscription;
        if(mSubscriptions.containsKey(eventName))
        {
        	subscription = mSubscriptions.get(eventName);
            subscription.OnReceived(args);
        }
    }


}
