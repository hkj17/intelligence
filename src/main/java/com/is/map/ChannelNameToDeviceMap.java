package com.is.map;

import java.util.HashMap;
import java.util.Map;

import io.netty.channel.ChannelId;

public class ChannelNameToDeviceMap {

	private static Map<ChannelId, String> deviceMap = new HashMap<>();

	public static void addDeviceMap(ChannelId id, String deviceId) {
		deviceMap.put(id, deviceId);
	}
	
	public static String getDeviceMap(ChannelId id){
        return deviceMap.get(id);
    }
	
	public static void removeDeviceMap(ChannelId id){
		deviceMap.remove(id);
    }
	
	public static Map<ChannelId, String> getAllMap(){
		return deviceMap;
	}
}
