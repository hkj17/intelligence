package com.is.map;

import java.util.HashMap;
import java.util.Map;

public class DeviceToVersionMap {

	private static Map<String, String> versionMap = new HashMap<>();
	
	public static void addVersionMap(String id, String version){
		versionMap.put(id, version);
	}
	
	public static String getVersionMap(String id){
		return versionMap.get(id);
	}
}
