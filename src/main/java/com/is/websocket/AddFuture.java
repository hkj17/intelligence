package com.is.websocket;

import com.is.map.DeviceService;
import com.is.map.FutureMap;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;

public class AddFuture {
	
	public static SyncFuture<String> setFuture(String deviceId,String tag){
		SyncFuture<String> future=new SyncFuture<>();
		 ChannelHandlerContext ctx=DeviceService.getSocketMap(deviceId);
		 if(ctx==null){
			 return null;
		 }
		 ChannelId name=ctx.channel().id();
		 FutureMap.addFuture(name.asLongText()+tag, future);
		 System.out.println("start add!");
		 return future;
	}

}
