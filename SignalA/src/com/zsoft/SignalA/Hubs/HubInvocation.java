package com.zsoft.SignalA.Hubs;

import org.json.JSONObject;

public class HubInvocation {

	private String mHubName;
	private String mMethod;

	public HubInvocation(String hubName, String method, JSONObject args, String callbackId) {
		mHubName = hubName;
		mMethod = method;

	}


	public String getHubName() {
		return mHubName;
	}

	public String getMethod() {
		// TODO Auto-generated method stub
		return null;
	}

	public JSONObject getArgs() {
		// TODO Auto-generated method stub
		return null;
	}

	public String Serialize() {
		// TODO Auto-generated method stub
		return null;
	}

}
