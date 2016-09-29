package com.is.websocket;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.is.map.ChannelNameToDeviceMap;
import com.is.map.DeviceService;
import com.is.map.FutureMap;

import io.netty.channel.ChannelHandlerContext;

public class CheckResponse extends Thread{

	
	private String deviceId;
	private String text;
	
	public CheckResponse(String deviceId,String text){
		this.deviceId=deviceId;
		this.text=text;
	}
	
	@Override
	public void run(){
		SyncFuture<String> future=new SyncFuture<>();
		 ChannelHandlerContext ctx=DeviceService.getSocketMap(deviceId);
		 String name=ctx.name();
		 FutureMap.addFuture(name, future);
		 try {
			String node=future.get(6, TimeUnit.SECONDS);
			if(node.equals(text)){
				ctx.close();
				if (DeviceService.getSocketMap(deviceId) != null) {
					DeviceService.removeSocketMap(deviceId);
				}
				if(ChannelNameToDeviceMap.getDeviceMap(name)!=null){
					ChannelNameToDeviceMap.removeDeviceMap(name);
				}
			}
			else{
				FutureMap.removeFutureMap(name);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
