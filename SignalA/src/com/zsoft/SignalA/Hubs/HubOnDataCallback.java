package com.zsoft.SignalA.Hubs;

import org.json.JSONArray;

public abstract class HubOnDataCallback {
	public abstract void OnReceived(JSONArray args);
}
