package com.zsoft.SignalA.Hubs;

import org.json.JSONObject;

public abstract class HubOnDataCallback {
	public abstract void OnReceived(JSONObject data);
}
