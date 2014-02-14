package com.zsoft.signala.hubs;

import org.json.JSONObject;

public abstract class Subscription {

	public abstract void OnReceived(JSONObject args);

}
