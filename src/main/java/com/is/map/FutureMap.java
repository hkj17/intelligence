package com.is.map;

import java.util.HashMap;
import java.util.Map;

import com.is.websocket.SyncFuture;

import io.netty.channel.ChannelId;

public class FutureMap {

	private static Map<ChannelId, SyncFuture<String>> futureMap = new HashMap<>();

	public static void addFuture(ChannelId id, SyncFuture<String> future) {
		futureMap.put(id, future);
	}
	
	public static SyncFuture<String> getFutureMap(ChannelId id){
        return futureMap.get(id);
    }
	
	public static void removeFutureMap(ChannelId id){
		futureMap.remove(id);
    }
	
	public static Map<ChannelId,  SyncFuture<String>> getAllMap(){
		return futureMap;
	}
}
