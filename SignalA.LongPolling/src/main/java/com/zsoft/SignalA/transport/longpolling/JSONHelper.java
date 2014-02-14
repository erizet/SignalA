package com.zsoft.signala.transport.longpolling;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONHelper {

	public static JSONObject ToJSONObject(String text)
	{
		JSONObject json;
		try {
			json = new JSONObject(text);
		} catch (JSONException e) {
			json = null;
		}
		return json;
	}
}
