package com.zsoft.SignalA.Hubs;

import java.util.Collection;

/// <summary>
///  A client side proxy for a server side hub.
/// </summary>
public interface IHubProxy {
	
	//void On(String eventName, OnDataCallback callback);
	public void Invoke(String method, Collection<?> args, HubInvokeCallback callback);
	
        //JToken this[String name] { get; set; }
}
