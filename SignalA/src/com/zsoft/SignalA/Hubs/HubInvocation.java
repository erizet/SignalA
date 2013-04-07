package com.zsoft.SignalA.Hubs;

import java.util.Collection;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HubInvocation {

	private String mHubName;
	private String mMethod;
	private String mCallbackId;
	private Collection<?> mArgs;

	public HubInvocation(String hubName, String method, Collection<?> args, String callbackId) {
		mHubName = hubName;
		mMethod = method;
		mArgs = args;
		mCallbackId = callbackId;

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
		JSONArray arr = new JSONArray(mArgs);
		JSONObject json = new JSONObject();
		try {
			json.put("I", mCallbackId);
			json.put("M", mMethod);
			json.put("A", arr); 
			json.put("H", mHubName);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return json.toString();
	}

}
