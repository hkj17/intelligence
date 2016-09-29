package com.is.map;

import java.util.HashMap;
import java.util.Map;

public class ChannelNameToDeviceMap {

	private static Map<String, String> deviceMap = new HashMap<>();

	public static void addDeviceMap(String name, String deviceId) {
		deviceMap.put(name, deviceId);
	}
	
	public static String getDeviceMap(String name){
        return deviceMap.get(name);
    }
	
	public static void removeDeviceMap(String name){
		deviceMap.remove(name);
    }
	
	public static Map<String, String> getAllMap(){
		return deviceMap;
	}
}
