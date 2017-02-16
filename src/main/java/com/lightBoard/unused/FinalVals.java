package com.lightBoard.unused;

import java.util.HashMap;

/**
 * Created by Moham on 2/16/2017.
 */
public class FinalVals
{
	private static final HashMap<String, Double> valueMap = new HashMap();

	public static Double get(String key)
	{
		return valueMap.get(key);
	}

	public static boolean set(String key, Double value)
	{
		System.out.println(valueMap.get(key) == null);
		if (key != null && value != null && valueMap.get(key) == null)
		{
			valueMap.put(key, value);
			return true;
		}
		else return false;
	}

}
