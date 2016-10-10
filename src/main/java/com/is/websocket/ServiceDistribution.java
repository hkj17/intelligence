package com.is.websocket;

import static com.is.constant.ParameterKeys.FACE_PHOTO_PATH;
import static com.is.constant.ParameterKeys.CLOCK_PHOTO_PATH;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.is.map.ChannelNameToDeviceMap;
import com.is.map.DeviceService;
import com.is.map.PhotoMap;
import com.is.model.Employee;
import com.is.service.AdminService;
import com.is.service.ClockService;
import com.is.service.EmployeeService;
import com.is.service.VisitorService;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import sun.misc.BASE64Decoder;

@SuppressWarnings("restriction")
@Component("serviceDistribution")
public class ServiceDistribution implements ApplicationContextAware {
	private static ApplicationContext context;
	
	private static VisitorService visitorService= (VisitorService)getContext().getBean("visitorService");

	private static AdminService adminService= (AdminService)getContext().getBean("adminService");
	
	private static EmployeeService employeeService= (EmployeeService)getContext().getBean("employeeService");

	@SuppressWarnings("static-access")
	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		// TODO Auto-generated method stub
		this.context = context;

	}

	public static ApplicationContext getContext() {
		return context;
	}

	public static JSONObject handleJson8_1(JSONObject jsonObject) {
		JSONObject responseCode = new JSONObject();
		//AdminService adminService = (AdminService) ServiceDistribution.getContext().getBean("adminService");
		List<Employee> list = adminService.getEmployeeList();
		JSONArray array = JSONArray.fromObject(list);
		responseCode.put("data", array);
		responseCode.put("type", 8);
		responseCode.put("code", 2);
		return responseCode;
	}

	public static JSONObject handleJson1_1(JSONObject jsonObject, ChannelHandlerContext socketChannel) {
		String devcieSn = jsonObject.getString("deviceSn");
		if (DeviceService.getSocketMap(devcieSn) == null) {
			DeviceService.addSocketMap(devcieSn, socketChannel);
		}
		if(ChannelNameToDeviceMap.getDeviceMap(socketChannel.name())==null){
			ChannelNameToDeviceMap.addDeviceMap(socketChannel.name(), devcieSn);
		}
		JSONObject responseCode = new JSONObject();
		responseCode.put("type", 1);
		responseCode.put("code", 2);
		responseCode.put("deviceSn", devcieSn);
		return responseCode;
	}
	
	public static JSONObject handleJson3_1(JSONObject jsonObject, ChannelHandlerContext socketChannel) {
		String employeeId=jsonObject.getString("employeeId");
		String time=jsonObject.getString("time");
		ClockService clockService = (ClockService) ServiceDistribution.getContext().getBean("clockService");
		String deviceId=ChannelNameToDeviceMap.getDeviceMap(socketChannel.name());
		clockService.addClocknormal(deviceId, employeeId, time);
		
		JSONObject responseCode = new JSONObject();
		responseCode.put("type", 3);
		responseCode.put("code", 2);
		responseCode.put("employeeId", employeeId);
		return responseCode;
		
	}
	
	public static JSONObject handleJson3_11(JSONObject jsonObject, ChannelHandlerContext socketChannel) {
		String visitorId=jsonObject.getString("visitorId");
		String time=jsonObject.getString("time");
		//VisitorService visitorService = (VisitorService) ServiceDistribution.getContext().getBean("visitorService");
		String deviceId=ChannelNameToDeviceMap.getDeviceMap(socketChannel.name());
		visitorService.insertVisitor(deviceId, visitorId, time,null);
		
		JSONObject responseCode = new JSONObject();
		responseCode.put("type", 3);
		responseCode.put("code", 12);
		responseCode.put("visitorId", visitorId);
		return responseCode;
		
	}
	
	public static JSONObject handleJson5_1(JSONObject jsonObject, ChannelHandlerContext socketChannel) {
		String visitorId=jsonObject.getString("visitorId");
		String time=jsonObject.getString("time");
		String employeeId=jsonObject.getString("employeeId");
		//VisitorService visitorService = (VisitorService)getContext().getBean("visitorService");
		String deviceId=ChannelNameToDeviceMap.getDeviceMap(socketChannel.name());
		visitorService.insertVisitor(deviceId, visitorId, time,employeeId);
		employeeService.sendMsg(employeeId);
		
		JSONObject responseCode = new JSONObject();
		responseCode.put("type", 5);
		responseCode.put("code", 2);
		responseCode.put("visitorId", visitorId);
		return responseCode;
		
	}

	public static boolean handleJson101_1(String deviceId) {
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("type", "101");
		jsonObject.put("code", "1");
		byte[] result = SocketService.responseByte(jsonObject, "101", "1");
		if (null != channel) {
			ByteBuf encoded = Unpooled.buffer();
			encoded.writeBytes(result);
			channel.write(encoded);
			channel.flush();
			return true;
		} else
			return false;
	}

	public static JSONObject handleJson3_21(JSONObject jsonObject,ChannelHandlerContext ctx) {
		String photo = jsonObject.getString("photo").substring(23);
		String id = jsonObject.getString("strangerId");
		String deviceId = ChannelNameToDeviceMap.getDeviceMap(ctx.name());
		String path = FACE_PHOTO_PATH + deviceId;
		if (!(new File(path).isDirectory())) {
			new File(path).mkdirs();
		}
		path=path+"/"+id+".jpg";
		boolean state=GenerateImage(photo, path);
		if(state && PhotoMap.getMap(deviceId)==null){
			PhotoMap.addMap(deviceId, path);
		}
		JSONObject response = new JSONObject();
		response.put("type", "3");
		response.put("code", "22");
		response.put("strangerId", id);
		return response;
	}
	
	public static void handleJson102_1(String deviceId) {
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("type", "102");
		jsonObject.put("code", "1");
		byte[] result = SocketService.responseByte(jsonObject, "102", "1");
		if (null != channel) {
			ByteBuf encoded = Unpooled.buffer();
			encoded.writeBytes(result);
			channel.write(encoded);
			channel.flush();
		} 
	}

	public static Boolean handleJson103_1(String employeeId,String strangerId,String employeeName,String birth,String deviceId) {
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("type", "103");
		jsonObject.put("code", "1");
		jsonObject.put("employeeId", employeeId);
		jsonObject.put("strangerId", strangerId);
		jsonObject.put("employeeName", employeeName);
		jsonObject.put("birth", birth);
		byte[] result = SocketService.responseByte(jsonObject, "103", "1");
		if (null != channel) {
			ByteBuf encoded = Unpooled.buffer();
			encoded.writeBytes(result);
			channel.write(encoded);
			channel.flush();
			return true;
		} 
		else {
			return false;
		}
	}
	
	public static Boolean handleJson103_11(String visitorId,String strangerId,String visitorName,String company,String position,String birth,String deviceId) {
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("type", "103");
		jsonObject.put("code", "11");
		jsonObject.put("visitorId", visitorId);
		jsonObject.put("strangerId", strangerId);
		jsonObject.put("visitorName", visitorName);
		jsonObject.put("company", company);
		jsonObject.put("position", position);
		jsonObject.put("birth", birth);
		byte[] result = SocketService.responseByte(jsonObject, "103", "11");
		if (null != channel) {
			ByteBuf encoded = Unpooled.buffer();
			encoded.writeBytes(result);
			channel.write(encoded);
			channel.flush();
			return true;
		} 
		else {
			return false;
		}
	}
	
	public static boolean handleJson111_1(String deviceId,String employeeId,String time) {
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("type", "111");
		jsonObject.put("code", "1");
		jsonObject.put("employeeId", employeeId);
		jsonObject.put("time", time);
		byte[] result = SocketService.responseByte(jsonObject, "111", "1");
		if (null != channel) {
			ByteBuf encoded = Unpooled.buffer();
			encoded.writeBytes(result);
			channel.write(encoded);
			channel.flush();
			return true;
		} 
		else {
			return false;
		}
	}
	
	public static void handleJson111_2(JSONObject jsonObject,ChannelHandlerContext ctx){
		String employeeId=jsonObject.getString("employeeId");
		String photoId=jsonObject.getString("photoId");
		String photo=jsonObject.getString("photo");
		String time=jsonObject.getString("time");
		String deviceId = ChannelNameToDeviceMap.getDeviceMap(ctx.name());
		
		String path = CLOCK_PHOTO_PATH + deviceId+"/"+employeeId;
		if (!(new File(path).isDirectory())) {
			new File(path).mkdirs();
		}
		path=path+"/"+photoId+".jpg";
		GenerateImage(photo, path);
		
		ClockService clockService = (ClockService) ServiceDistribution.getContext().getBean("clockService");
		clockService.insertAbonormalClockPhoto(employeeId, time, path,deviceId);
		
	}
	
	public static Boolean handleJson104_11(String deviceId,String id,String name,String company,String position,
			String telphone,String email,String importance,String birth){
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("type", "104");
		jsonObject.put("code", "11");
		jsonObject.put("visitorId", id);
		jsonObject.put("visitorName", name);
		jsonObject.put("birth", birth);
		jsonObject.put("company", company);
		jsonObject.put("importance", importance);
		jsonObject.put("position", position);
		jsonObject.put("telephone", telphone);
		jsonObject.put("email", email);
		byte[] result = SocketService.responseByte(jsonObject, "104", "11");
		if (null != channel) {
			ByteBuf encoded = Unpooled.buffer();
			encoded.writeBytes(result);
			channel.write(encoded);
			channel.flush();
			return true;
		} 
		else {
			return false;
		}
	}
	
	public static Boolean handleJson104_1(String deviceId,String employeeId, String name, String birth){
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("type", "104");
		jsonObject.put("code", "1");
		jsonObject.put("employeeId", employeeId);
		jsonObject.put("employeeName", name);
		jsonObject.put("birth", birth);
		byte[] result = SocketService.responseByte(jsonObject, "104", "1");
		if (null != channel) {
			ByteBuf encoded = Unpooled.buffer();
			encoded.writeBytes(result);
			channel.write(encoded);
			channel.flush();
			return true;
		} 
		else {
			return false;
		}
	}
	
	public static Boolean handleJson105_1(String deviceId,String employeeId){
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("type", "105");
		jsonObject.put("code", "1");
		jsonObject.put("employeeId", employeeId);
		byte[] result = SocketService.responseByte(jsonObject, "105", "1");
		if (null != channel) {
			ByteBuf encoded = Unpooled.buffer();
			encoded.writeBytes(result);
			channel.write(encoded);
			channel.flush();
			return true;
		} 
		else {
			return false;
		}
	}
	
	public static Boolean handleJson105_11(String deviceId,String visitorId){
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("type", "105");
		jsonObject.put("code", "11");
		jsonObject.put("visitorId", visitorId);
		byte[] result = SocketService.responseByte(jsonObject, "105", "11");
		if (null != channel) {
			ByteBuf encoded = Unpooled.buffer();
			encoded.writeBytes(result);
			channel.write(encoded);
			channel.flush();
			return true;
		} 
		else {
			return false;
		}
	}
	
	public static boolean GenerateImage(String imgStr,String path) { // 对字节数组字符串进行Base64解码并生成图片
		if (imgStr == null) // 图像数据为空
			return false;
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			// Base64解码
			byte[] b = decoder.decodeBuffer(imgStr);
			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {// 调整异常数据
					b[i] += 256;
				}
			}
			OutputStream out = new FileOutputStream(path);
			out.write(b);
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
