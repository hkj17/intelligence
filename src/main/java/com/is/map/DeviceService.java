package com.is.map;

import java.util.HashMap;
import java.util.Map;

import io.netty.channel.ChannelHandlerContext;

public class DeviceService {
	
	private static Map<String, ChannelHandlerContext> socketMap = new HashMap<>();

	public static void addSocketMap(String id, ChannelHandlerContext device_channel) {
		socketMap.put(id, device_channel);
	}
	
	public static ChannelHandlerContext getSocketMap(String id){
        return socketMap.get(id);
    }
	
	public static void removeSocketMap(String id){
        socketMap.remove(id);
    }
	
	public static Map<String, ChannelHandlerContext> getAllMap(){
		return socketMap;
	}
	
}
