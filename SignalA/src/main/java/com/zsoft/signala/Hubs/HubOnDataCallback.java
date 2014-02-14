package com.zsoft.signala.hubs;

import org.json.JSONArray;

public abstract class HubOnDataCallback {
	public abstract void OnReceived(JSONArray args);
}
