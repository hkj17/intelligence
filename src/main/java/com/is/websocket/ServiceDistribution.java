package com.is.websocket;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import static com.is.constant.ParameterKeys.FACE_PHOTO_PATH;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Encoder;
import sun.misc.BASE64Decoder;
import com.is.model.Employee;
import com.is.service.AdminService;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.socket.SocketChannel;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Component("serviceDistribution")
public class ServiceDistribution implements ApplicationContextAware {
	private static ApplicationContext context;

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
		AdminService adminService = (AdminService) ServiceDistribution.getContext().getBean("adminService");
		List<Employee> list = adminService.getEmployeeList();
		JSONArray array = JSONArray.fromObject(list);
		responseCode.put("data", array);
		responseCode.put("type", 8);
		responseCode.put("code", 2);
		return responseCode;
	}

	public static JSONObject handleJson1_1(JSONObject jsonObject, SocketChannel socketChannel) {
		String devcieSn = jsonObject.getString("deviceSn");
		if (DeviceService.getSocketMap(devcieSn) == null) {
			DeviceService.addSocketMap(devcieSn, socketChannel);
		}
		JSONObject responseCode = new JSONObject();
		responseCode.put("type", 1);
		responseCode.put("code", 2);
		responseCode.put("deviceSn", devcieSn);
		return responseCode;
	}

	public static boolean handleJson101_1(String deviceId) {
		SocketChannel channel = DeviceService.getSocketMap(deviceId);
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

	public static JSONObject handleJson3_21(JSONObject jsonObject) {
		String photo = jsonObject.getString("photo");
		String id = jsonObject.getString("strangerId");
		String deviceId = jsonObject.getString("deviceId");
		String path = FACE_PHOTO_PATH + deviceId;
		if (!(new File(path).isDirectory())) {
			new File(path).mkdirs();
		}
		path=path+"/id";
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
		SocketChannel channel = DeviceService.getSocketMap(deviceId);
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

	@SuppressWarnings("restriction")
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
