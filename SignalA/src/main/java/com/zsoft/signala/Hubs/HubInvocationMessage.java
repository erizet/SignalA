package com.zsoft.SignalA.Hubs;

import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

public class HubInvocationMessage {

	private String mHubName;
	private String mMethod;
	private JSONArray mArgs;
	
	public HubInvocationMessage(JSONObject message) {
		
		mHubName = message.optString("H");
		mHubName = mHubName.toLowerCase(Locale.US);
		mMethod = message.optString("M");
		mMethod = mMethod.toLowerCase(Locale.US);
		mArgs = message.optJSONArray("A");
		//[{"H":"CalculatorHub","M":"newCalculation","A":["4/7/2013 12:42:23 PM : 10 + 5 = 15"]}]}" +
	}

	public String getHubName() {
		return mHubName;
	}

	public JSONArray getArgs() {
		return mArgs;
	}

	public String getMethod() {
		return mMethod;
	}

}
