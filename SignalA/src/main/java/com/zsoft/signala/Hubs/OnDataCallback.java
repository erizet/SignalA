package com.zsoft.signala.hubs;

import org.json.JSONObject;

public abstract class OnDataCallback {
	public abstract JSONObject Callback(JSONObject result);

}
