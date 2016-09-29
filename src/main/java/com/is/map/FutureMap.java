package com.is.map;

import java.util.HashMap;
import java.util.Map;

import com.is.websocket.SyncFuture;

public class FutureMap {

	private static Map<String, SyncFuture<String>> futureMap = new HashMap<>();

	public static void addFuture(String id, SyncFuture<String> future) {
		futureMap.put(id, future);
	}
	
	public static SyncFuture<String> getFutureMap(String id){
        return futureMap.get(id);
    }
	
	public static void removeFutureMap(String id){
		futureMap.remove(id);
    }
	
	public static Map<String,  SyncFuture<String>> getAllMap(){
		return futureMap;
	}
}
