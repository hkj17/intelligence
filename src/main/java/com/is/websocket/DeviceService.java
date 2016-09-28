package com.is.websocket;

import java.util.HashMap;
import java.util.Map;

import io.netty.channel.socket.SocketChannel;

public class DeviceService {

	private static Map<String, SocketChannel> socketMap = new HashMap<>();

	public static void addSocketMap(String id, SocketChannel device_channel) {
		socketMap.put(id, device_channel);
	}
	
	public static SocketChannel getSocketMap(String id){
        return socketMap.get(id);
    }
	
	public static void removeSocketMap(String id){
        socketMap.remove(id);
    }
	
	public static Map<String, SocketChannel> getAllMap(){
		return socketMap;
	}
	
}
