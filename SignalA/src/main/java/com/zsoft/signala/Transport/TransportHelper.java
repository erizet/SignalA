package com.zsoft.SignalA.Transport;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.zsoft.SignalA.ConnectionBase;

public class TransportHelper {
    private static final String TAG = "TransportHelper";

    public static ProcessResult ProcessResponse(ConnectionBase connection, JSONObject response)
    {
    	ProcessResult result = new ProcessResult();
    	
        if (connection == null)
        {
            //throw new Exception("connection");
        }

        if (response == null)
        {
            return result;
        }

    	String newMessageId = null;
    	JSONArray messagesArray = null;
    	String groupsToken = null;
    	//JSONObject transportData = null;
    	String info = null;

		result.timedOut = response.optInt("T") == 1;	
		result.disconnected = response.optInt("D") == 1;
		newMessageId = response.optString("C");
		messagesArray = response.optJSONArray("M");
		groupsToken = response.optString("G", null);
		info = response.optString("I", null);

		if(info != null)
		{
			connection.setMessage(response);
			return result;
		}
		
		if(result.disconnected)
		{
			return result;
		}			
		
		if(groupsToken!=null)
		{
			connection.setGroupsToken(groupsToken);
		}

        if (messagesArray != null)
        {
			for (int i = 0; i < messagesArray.length(); i++) {
				try {
					connection.setMessage(messagesArray.getJSONObject(i));
				} catch (JSONException e) {
					Log.e(TAG, "Error when calling setMessage on connection.",e);
				}
			}

            connection.setMessageId(newMessageId);
        }
        
        return result;
    }

    
    
	public static String GetReceiveQueryString(ConnectionBase connection, String data, String transport)
    {
        if (connection == null)
        {
            throw new IllegalArgumentException("connection");
        }
        if (transport == null)
        {
            throw new IllegalArgumentException("transport");
        }

    	
        // ?transport={0}&connectionToken={1}&messageId={2}&groupsToken={3}&connectionData={4}{5}
		String qs = "?transport=";
		qs += transport;
		try {
			qs += "&connectionToken=" + URLEncoder.encode(connection.getConnectionToken(), "utf-8");
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, "Unsupported message encoding error, when encoding connectionToken.");
		}
		if(connection.getMessageId()!=null)
		{
			try {
				qs += "&messageId=" + URLEncoder.encode(connection.getMessageId(), "utf-8");
			} catch (UnsupportedEncodingException e) {
				Log.e(TAG, "Unsupported message encoding error, when encoding messageid.");
			}
		}

        if (connection.getGroupsToken() != null && connection.getGroupsToken().length() > 0)
        {
            try {
				qs += "&groupsToken=" + URLEncoder.encode(connection.getGroupsToken(), "utf-8");
			} catch (UnsupportedEncodingException e) {
				Log.e(TAG, "Unsupported message encoding error, when encoding groupsToken.");
			}
        }

        if (data != null)
        {
            try {
				qs += "&connectionData=" + URLEncoder.encode(data, "utf-8");
			} catch (UnsupportedEncodingException e) {
				Log.e(TAG, "Unsupported message encoding error, when encoding connectionData.");
			}
        }

        if(connection.getQueryString() != null && connection.getQueryString().length() > 0)
        {
            try {
				qs += "&" + URLEncoder.encode(connection.getQueryString(), "utf-8");
			} catch (UnsupportedEncodingException e) {
				Log.e(TAG, "Unsupported message encoding error, when encoding querystring.");
			}
        }

        return qs;
    }

	
	public static ArrayList<String> ToArrayList(JSONArray jsonArray)
	{
		ArrayList<String> list = null;
		if (jsonArray != null) { 
			int len = jsonArray.length();
			list = new ArrayList<String>(len);     
			for (int i=0;i<len;i++){ 
				Object o = jsonArray.opt(i);
				if(o!=null)
					list.add(o.toString());
			} 
		}
		
		return list;
	}

    public static String AppendCustomQueryString(ConnectionBase connection, String baseUrl)
    {
        if (connection == null)
        {
            throw new IllegalArgumentException("connection");
        }

        if (baseUrl == null)
        {
            baseUrl = "";
        }

        String appender = "",
               customQuery = connection.getQueryString(),
               qs = "";

        if (customQuery!=null && customQuery.length()>0)
        {
            char firstChar = customQuery.charAt(0);

            // If the custom query string already starts with an ampersand or question mark
            // then we dont have to use any appender, it can be empty.
            if (firstChar != '?' && firstChar != '&')
            {
                appender = "?";

                if (baseUrl.contains(appender))
                {
                    appender = "&";
                }
            }

            qs += appender + customQuery;
        }

        return qs;
    }

}
