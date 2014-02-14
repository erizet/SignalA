package com.zsoft.signala.hubs;


public abstract class HubInvokeCallback {
	public abstract void OnResult(boolean succeeded, String response);
	public abstract void OnError(Exception ex);
}
