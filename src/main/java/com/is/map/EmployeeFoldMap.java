package com.is.map;

import java.util.HashMap;
import java.util.Map;

public class EmployeeFoldMap {

	private static Map<String, String> map = new HashMap<>();
	
	public static void setData(String key,String value){
		map.put(key, value);
	}
	
	public static void delData(String key){
		if(map.containsKey(key)){
			map.remove(key);
		}
	}
	
	public static String getData(String key){
		if(map.containsKey(key)){
			return map.get(key);
		}
		else{
			return null;
		}
	}
}
