package com.is.websocket;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.is.map.DeviceService;
import com.is.map.FutureMap;

import io.netty.channel.ChannelHandlerContext;

public class CheckResponse extends Thread {

	private String deviceId;
	private String text;
	private SyncFuture<String> future;

	public CheckResponse(String deviceId, String text, SyncFuture<String> future) {
		this.deviceId = deviceId;
		this.text = text;
		this.future = future;
	}

	@Override
	public void run() {
		ChannelHandlerContext ctx = DeviceService.getSocketMap(deviceId);
		if(ctx==null){
			return;
		}
		String name = ctx.name();
		try {
			if (future == null) {
				return;
			}
			String node = future.get(6, TimeUnit.SECONDS);
			if (!text.equals(node) || node == null) {
				System.out.println("connection break!");
				ctx.close();
			}

			System.out.println("node:" + node);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			FutureMap.removeFutureMap(name);
		}
	}
	
	public void roll(String methodName,Object[] params){
		try {
			Class<?> c = Class.forName("com.is.websocket.ServiceDistribution");
			Object obj = c.newInstance();
			Class[] cla = new Class[params.length];
			Arrays.fill(cla, String.class);
			Method method = c.getMethod(methodName, cla);
			method.invoke(obj , params);
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
