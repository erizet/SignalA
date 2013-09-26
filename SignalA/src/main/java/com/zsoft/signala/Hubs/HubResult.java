package com.zsoft.SignalA.Hubs;

import org.json.JSONObject;

public class HubResult {

	private String mId;
	private String mResult;

	public HubResult(JSONObject message) {
		mId = message.optString("I");
		mResult = message.optString("R");
	}

	public String getId() { return mId; }
	public String getResult() { return mResult; }
}
