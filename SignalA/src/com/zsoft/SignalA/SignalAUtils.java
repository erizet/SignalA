package com.zsoft.SignalA;

public class SignalAUtils {
	public static String EnsureEndsWith(String text, String end)
	{
		if(!text.endsWith(end))
			text += end;
		
		return text;
	}
}
