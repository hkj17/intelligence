package com.is.gson;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;



/** 
 * @author zhuolin(zl@nbicc.com) 
 * @date 2015年10月20日
 * 类说明 
 */
public class CloudJsonObject {
	
	private Map<String,Object> mMap = new HashMap<String,Object>();
	
	public Map<String, Object> getmMap() {
		return mMap;
	}

	public CloudJsonObject(){}
	
	public CloudJsonObject(Map<String,Object> map){
		mMap = map;
	}
	
	/**
	 * get.
	 * 
	 * @param key key.
	 * @return boolean or long or double or String or JSONArray or JSONObject or null.
	 */
	public Object get(String key)
	{
		return mMap.get(key);
	}

	/**
	 * get boolean value.
	 * 
	 * @param key key.
	 * @param def default value.
	 * @return value or default value.
	 */
	public boolean getBoolean(String key, boolean def)
	{
		Object tmp = mMap.get(key);
		return tmp != null && tmp instanceof Boolean ? (Boolean)tmp : def;
	}

	/**
	 * get int value.
	 * 
	 * @param key key.
	 * @param def default value.
	 * @return value or default value.
	 */
	public int getInt(String key, int def)
	{
		Object tmp = mMap.get(key);
		return tmp != null && tmp instanceof Number ? ((Number)tmp).intValue() : def;
	}

	/**
	 * get long value.
	 * 
	 * @param key key.
	 * @param def default value.
	 * @return value or default value.
	 */
	public long getLong(String key, long def)
	{
		Object tmp = mMap.get(key);
		return tmp != null && tmp instanceof Number ? ((Number)tmp).longValue() : def;
	}

	/**
	 * get float value.
	 * 
	 * @param key key.
	 * @param def default value.
	 * @return value or default value.
	 */
	public float getFloat(String key, float def)
	{
		Object tmp = mMap.get(key);
		return tmp != null && tmp instanceof Number ? ((Number)tmp).floatValue() : def;
	}

	/**
	 * get double value.
	 * 
	 * @param key key.
	 * @param def default value.
	 * @return value or default value.
	 */
	public double getDouble(String key, double def)
	{
		Object tmp = mMap.get(key);
		return tmp != null && tmp instanceof Number ? ((Number)tmp).doubleValue() : def;
	}

	/**
	 * get string value.
	 * 
	 * @param key key.
	 * @return value or default value.
	 */
	public String getString(String key)
	{
		Object tmp = mMap.get(key);
		return tmp == null ? null : tmp.toString();
	}

	/**
	 * get JSONArray value.
	 * @param <T>
	 * 
	 * @param key key.
	 * @return value or default value.
	 */
//	public JSONArray getArray(String key)
//	{
//		Object tmp = mMap.get(key);
//		return tmp == null ? null : tmp instanceof JSONArray ? (JSONArray)tmp : null;
//	}

	public <T> List getArray(String key,Class<T> clazz){
		Object tmp = mMap.get(key);
		return tmp == null ? null : (List)tmp;
	}

	/**
	 * get key iterator.
	 * 
	 * @return key iterator.
	 */
	public Iterator<String> keys()
	{
		return mMap.keySet().iterator();
	}

	/**
	 * contains key.
	 * 
	 * @param key key.
	 * @return contains or not.
	 */
	public boolean contains(String key)
	{
		return mMap.containsKey(key);
	}

	/**
	 * put value.
	 * 
	 * @param name name.
	 * @param value value.
	 */
	public void put(String name, Object value)
	{
		mMap.put(name, value);
	}

	/**
	 * put all.
	 * 
	 * @param names name array.
	 * @param values value array.
	 */
	public void putAll(String[] names, Object[] values)
	{
		for(int i=0,len=Math.min(names.length, values.length);i<len;i++)
			mMap.put(names[i], values[i]);
	}

	/**
	 * put all.
	 * 
	 * @param map map.
	 */
	public void putAll(Map<String, Object> map)
	{
		for( Map.Entry<String, Object> entry : map.entrySet() )
			mMap.put(entry.getKey(), entry.getValue());
	}
	
	public static CloudJsonObject buildCloudJsonObject(Object obj){
		Gson gson = GsonFactory.createGson();
		String bodyStr = gson.toJson(obj);
		Map<String, Object> map = gson.fromJson(bodyStr, new TypeToken<Map<String, Object>>() {}.getType());
		CloudJsonObject jsonObject = new CloudJsonObject(map);
		return jsonObject;
	}
}
