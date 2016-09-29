package com.is.map;

import java.util.HashMap;
import java.util.Map;

public class PhotoMap {

	private static Map<String, String> map = new HashMap<>();
	
	public static void addMap(String id, String path) {
		map.put(id, path);
	}
	
	public static String getMap(String id){
        return map.get(id);
    }
	
	public static void removeMap(String id){
        map.remove(id);
    }
}
