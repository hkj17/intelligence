package com.is.websocket;

import static com.is.constant.ParameterKeys.CLOCK_PHOTO_PATH;
import static com.is.constant.ParameterKeys.EMPLOYEE_TEMPLATE;
import static com.is.constant.ParameterKeys.FACE_PHOTO_PATH;
import static com.is.constant.ParameterKeys.VISITOR_TEMPLATE;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.is.map.ChannelNameToDeviceMap;
import com.is.map.DeviceService;
import com.is.map.PhotoMap;
import com.is.model.CollectionPhoto;
import com.is.service.AdminService;
import com.is.service.ClockService;
import com.is.service.EmployeeService;
import com.is.service.VisitorService;
import com.is.util.Base64Utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import net.sf.json.JSONObject;
import sun.misc.BASE64Decoder;

@SuppressWarnings("restriction")
@Component("serviceDistribution")
public class ServiceDistribution implements ApplicationContextAware {
	private static Logger logger = Logger.getLogger(ServiceDistribution.class);
	
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

	public static JSONObject handleJson1_1(JSONObject jsonObject, ChannelHandlerContext socketChannel) {
		String devcieSn = jsonObject.getString("deviceSn");
		if (DeviceService.getSocketMap(devcieSn) != null) {
			ChannelHandlerContext old=DeviceService.getSocketMap(devcieSn);
			ChannelNameToDeviceMap.removeDeviceMap(old.channel().id());
			DeviceService.removeSocketMap(devcieSn);
		}
		DeviceService.addSocketMap(devcieSn, socketChannel);
		ChannelNameToDeviceMap.addDeviceMap(socketChannel.channel().id(), devcieSn);
		
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
		String deviceId=ChannelNameToDeviceMap.getDeviceMap(socketChannel.channel().id());
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
		VisitorService visitorService = (VisitorService) ServiceDistribution.getContext().getBean("visitorService");
		String deviceId=ChannelNameToDeviceMap.getDeviceMap(socketChannel.channel().id());
		visitorService.insertVisitor(deviceId, visitorId, time,null,null);
		
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
		VisitorService visitorService = (VisitorService)getContext().getBean("visitorService");
		String deviceId=ChannelNameToDeviceMap.getDeviceMap(socketChannel.channel().id());
		visitorService.insertVisitor(deviceId, visitorId, time,employeeId,null);
		EmployeeService employeeService= (EmployeeService)getContext().getBean("employeeService");
		employeeService.sendMsg(employeeId);
		
		JSONObject responseCode = new JSONObject();
		responseCode.put("type", 5);
		responseCode.put("code", 2);
		responseCode.put("visitorId", visitorId);
		return responseCode;
		
	}
	
	public static JSONObject handleJson8_1(JSONObject jsonObject, ChannelHandlerContext socketChannel) {
		String employeeId=jsonObject.getString("employeeId");
		String templateSeqno=jsonObject.getString("templateSeqno");
		String templateId=jsonObject.getString("templateId");
		String photo=jsonObject.getString("templatePic");
		String deviceId = ChannelNameToDeviceMap.getDeviceMap(socketChannel.channel().id());
		String path=EMPLOYEE_TEMPLATE+deviceId+"/"+employeeId;
		if (!(new File(path).isDirectory())) {
			new File(path).mkdirs();
		}
		String photopath=path+"/"+templateId;
		GenerateImage(photo, photopath);
		
		if(templateSeqno.equals("1")){
			AdminService adminService = (AdminService) ServiceDistribution.getContext().getBean("adminService");
			adminService.updateEmployeeTemplatePhoto(employeeId, path);
		}
		JSONObject responseCode = new JSONObject();
		responseCode.put("type", 8);
		responseCode.put("code", 2);
		responseCode.put("employeeId", employeeId);
		responseCode.put("templateId", templateId);
		responseCode.put("templateSeqno", templateSeqno);
		return responseCode;
		
	}
	
	public static JSONObject handleJson8_11(JSONObject jsonObject, ChannelHandlerContext socketChannel) {
		String visitorId=jsonObject.getString("visitorId");
		String templateSeqno=jsonObject.getString("templateSeqno");
		String templateId=jsonObject.getString("templateId");
		String photo=jsonObject.getString("templatePic");
		String deviceId = ChannelNameToDeviceMap.getDeviceMap(socketChannel.channel().id());
		String path=VISITOR_TEMPLATE+deviceId+"/"+visitorId;
		if (!(new File(path).isDirectory())) {
			new File(path).mkdirs();
		}
		String photopath=path+"/"+templateId;
		GenerateImage(photo, photopath);
		
		if(templateSeqno.equals("1")){
			VisitorService visitorService = (VisitorService)getContext().getBean("visitorService");
			visitorService.updateVisitorTemplate(visitorId, path);
		}
		JSONObject responseCode = new JSONObject();
		responseCode.put("type", 8);
		responseCode.put("code", 12);
		responseCode.put("visitorId", visitorId);
		responseCode.put("templateId", templateId);
		responseCode.put("templateSeqno", templateSeqno);
		return responseCode;
		
	}
	
	public static JSONObject handleJson4_1(JSONObject jsonObject, ChannelHandlerContext socketChannel) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		String photo = jsonObject.getString("photo");
		if(photo.startsWith("data")){
			photo = photo.substring(photo.indexOf(",")+1);
		}
		String id = jsonObject.getString("strangerId");
		String deviceId = ChannelNameToDeviceMap.getDeviceMap(socketChannel.channel().id());
		String path = FACE_PHOTO_PATH + deviceId+"/"+sdf.format(new Date());
		if (!(new File(path).isDirectory())) {
			new File(path).mkdirs();
		}
		path=path+"/"+id+".jpg";
		GenerateImage(photo, path);
		
		
		String time=jsonObject.getString("time");
		String employeeId=jsonObject.getString("employeeId");
		VisitorService visitorService = (VisitorService)getContext().getBean("visitorService");
		visitorService.insertVisitor(deviceId, null, time,employeeId,path);
		EmployeeService employeeService= (EmployeeService)getContext().getBean("employeeService");
		employeeService.sendMsg(employeeId);
		
		JSONObject responseCode = new JSONObject();
		responseCode.put("type", 4);
		responseCode.put("code", 2);
		responseCode.put("strangerId", id);
		return responseCode;
		
	}
	
	public static JSONObject handleJson100_100() {	
		JSONObject responseCode = new JSONObject();
		responseCode.put("type", 100);
		responseCode.put("code", 100);
		return responseCode;
	}

	public static boolean handleJson101_1(String deviceId) {
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("type", 101);
		jsonObject.put("code", 1);
		byte[] result = SocketService.responseByte(jsonObject, "101", "1");
		if (null != channel) {
			excuteWrite(result, channel);
			return true;
		} else
			return false;
	}

	public static JSONObject handleJson3_21(JSONObject jsonObject,ChannelHandlerContext ctx) {

		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		String photo = jsonObject.getString("photo");
		String time = jsonObject.getString("time");
		if(photo.startsWith("data")){
			photo = photo.substring(photo.indexOf(",")+1);
		}
		String id = jsonObject.getString("strangerId");
		String deviceId = ChannelNameToDeviceMap.getDeviceMap(ctx.channel().id());
		VisitorService visitorService = (VisitorService)getContext().getBean("visitorService");
		CollectionPhoto collectionPhoto=visitorService.getCollectByStrangerId(id);
		if(collectionPhoto==null){
			String path = FACE_PHOTO_PATH + deviceId+"/"+sdf.format(new Date());
			if (!(new File(path).isDirectory())) {
				new File(path).mkdirs();
			}
			path=path+"/"+id+".jpg";
			GenerateImage(photo, path);
			
			visitorService.collectionPhoto(deviceId, time, path, id);
		}
		
		JSONObject response = new JSONObject();
		response.put("type", 3);
		response.put("code", 22);
		response.put("strangerId", id);
		return response;
	}
	
	public static JSONObject handleJson3_23(JSONObject jsonObject,ChannelHandlerContext ctx) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		String photo = jsonObject.getString("photo");
		if(photo.startsWith("data")){
			photo = photo.substring(photo.indexOf(",")+1);
		}
		String id =jsonObject.getString("strangerId");
		String deviceId = ChannelNameToDeviceMap.getDeviceMap(ctx.channel().id());
		String path = FACE_PHOTO_PATH + deviceId+"/"+sdf.format(new Date());;
		if (!(new File(path).isDirectory())) {
			new File(path).mkdirs();
		}
		path=path+"/"+id+".jpg";
		boolean state=GenerateImage(photo, path);
		if(state){
			PhotoMap.addMap(deviceId, path);
		}
		JSONObject response = new JSONObject();
		response.put("type", 3);
		response.put("code", 24);
		response.put("strangerId",id);
		return response;
		
	}
	
	public static void handleJson102_1(String deviceId) {
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("type", 102);
		jsonObject.put("code", 1);
		byte[] result = SocketService.responseByte(jsonObject, "102", "1");
		if (null != channel) {
			excuteWrite(result, channel);
		} 
	}

	public static Boolean handleJson103_1(String employeeId,String strangerId,String employeeName,String birth,String deviceId) {
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("type", 103);
		jsonObject.put("code", 1);
		jsonObject.put("employeeId", employeeId);
		jsonObject.put("strangerId", strangerId);
		jsonObject.put("employeeName", employeeName);
		jsonObject.put("birth", birth);
		byte[] result = SocketService.responseByte(jsonObject, "103", "1");
		if (null != channel) {
			excuteWrite(result, channel);
			return true;
		} 
		else {
			return false;
		}
	}
	
	public static Boolean handleJson106_1(String deviceId,String employeeId,String startTime,
			String endTime,String id,String visitorId,String message){
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("type", 106);
		jsonObject.put("code", 1);
		jsonObject.put("employeeId", employeeId);
		jsonObject.put("beginTime", startTime);
		jsonObject.put("endTime", endTime);
		jsonObject.put("appointmentId", id);
		jsonObject.put("visitorId", visitorId);
		jsonObject.put("message", message);
		byte[] result = SocketService.responseByte(jsonObject, "106", "1");
		if (null != channel) {
			excuteWrite(result, channel);
			return true;
		} 
		else {
			return false;
		}
	}
	
	public static Boolean handleJson106_11(String deviceId,String employeeId,String startTime,
			String endTime,String id,String visitorId,String message){
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("type", 106);
		jsonObject.put("code", 11);
		jsonObject.put("employeeId", employeeId);
		jsonObject.put("beginTime", startTime);
		jsonObject.put("endTime", endTime);
		jsonObject.put("id", id);
		jsonObject.put("visitorId", visitorId);
		jsonObject.put("message", message);
		byte[] result = SocketService.responseByte(jsonObject, "106", "11");
		if (null != channel) {
			excuteWrite(result, channel);
			return true;
		} 
		else {
			return false;
		}
	}
	
	public static Boolean handleJson106_21(String deviceId,String id,String employeeId,String visitorId){
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("type", 106);
		jsonObject.put("code", 21);
		jsonObject.put("id", id);
		jsonObject.put("employeeId", employeeId);
		jsonObject.put("visitorId", visitorId);
		byte[] result = SocketService.responseByte(jsonObject, "106", "21");
		if (null != channel) {
			excuteWrite(result, channel);
			return true;
		} 
		else {
			return false;
		}
	}
	
	public static Boolean handleJson103_11(String visitorId,String strangerId,String visitorName,String company,String position,String birth,String deviceId,String path) {
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("type", 103);
		jsonObject.put("code", 11);
		jsonObject.put("visitorId", visitorId);
		jsonObject.put("strangerId", strangerId);
		jsonObject.put("visitorName", visitorName);
		jsonObject.put("company", company);
		jsonObject.put("position", position);
		try {
			if(path!=null){
				String base64=Base64Utils.GetImageStr(path);
				jsonObject.put("photo", base64);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(birth!=null){
			jsonObject.put("birth", birth);
		}
		byte[] result = SocketService.responseByte(jsonObject, "103", "11");
		if (null != channel) {
			excuteWrite(result, channel);
			return true;
		} 
		else {
			return false;
		}
	}
	
	public static boolean handleJson110_1(String deviceId,String employeeId,String time) {
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("type", 110);
		jsonObject.put("code", 1);
		jsonObject.put("employeeId", employeeId);
		jsonObject.put("time", time);
		byte[] result = SocketService.responseByte(jsonObject, "110", "1");
		if (null != channel) {
			excuteWrite(result, channel);
			return true;
		} 
		else {
			return false;
		}
	}
	
	public static void handleJson110_2(JSONObject jsonObject,ChannelHandlerContext ctx){
		String employeeId=jsonObject.getString("employeeId");
		String shootId=jsonObject.getString("shootId");
		String photo=jsonObject.getString("photo");
		String deviceId = ChannelNameToDeviceMap.getDeviceMap(ctx.channel().id());
		
		String path = CLOCK_PHOTO_PATH + deviceId+"/"+employeeId;
		if (!(new File(path).isDirectory())) {
			new File(path).mkdirs();
		}
		path=path+"/"+shootId+".jpg";
		GenerateImage(photo, path);
		logger.info(path);
		
		ClockService clockService = (ClockService) ServiceDistribution.getContext().getBean("clockService");
		clockService.insertAbonormalClockPhoto(employeeId, path,deviceId);
		
	}
	
	public static Boolean handleJson104_11(String deviceId,String id,String name,String company,String position,
			String telphone,String email,String importance,String birth){
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("type", 104);
		jsonObject.put("code", 11);
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
			excuteWrite(result, channel);
			return true;
		} 
		else {
			return false;
		}
	}
	
	public static Boolean handleJson104_1(String deviceId,String employeeId, String name, String birth,String strangerId){
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("type", 104);
		jsonObject.put("code", 1);
		jsonObject.put("employeeId", employeeId);
		jsonObject.put("employeeName", name);
		jsonObject.put("strangerId", strangerId);
		jsonObject.put("birth", birth);
		byte[] result = SocketService.responseByte(jsonObject, "104", "1");
		if (null != channel) {
			excuteWrite(result, channel);
			return true;
		} 
		else {
			return false;
		}
	}
	
	public static Boolean handleJson105_1(String deviceId,String employeeId){
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("type", 105);
		jsonObject.put("code", 1);
		jsonObject.put("employeeId", employeeId);
		byte[] result = SocketService.responseByte(jsonObject, "105", "1");
		if (null != channel) {
			excuteWrite(result, channel);
			return true;
		} 
		else {
			return false;
		}
	}
	
	public static Boolean handleJson105_11(String deviceId,String visitorId){
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("type", 105);
		jsonObject.put("code", 11);
		jsonObject.put("visitorId", visitorId);
		byte[] result = SocketService.responseByte(jsonObject, "105", "11");
		if (null != channel) {
			excuteWrite(result, channel);
			return true;
		} 
		else {
			return false;
		}
	}
	
	public static Boolean handleJson105_21(String strangerId,String deviceId){
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("type", 105);
		jsonObject.put("code", 21);
		jsonObject.put("strangerId", strangerId);
		byte[] result = SocketService.responseByte(jsonObject, "105", "21");
		if (null != channel) {
			excuteWrite(result, channel);
			return true;
		} 
		else {
			return false;
		}
	}
	
	public static Boolean handleJson109_1(String employeeId,ChannelHandlerContext channel) {
		//ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("type", 109);
		jsonObject.put("code", 1);
		jsonObject.put("employeeId", employeeId);
		byte[] result = SocketService.responseByte(jsonObject, "109", "1");
		if (null != channel) {
			excuteWrite(result, channel);
			return true;
		} 
		else {
			return false;
		}
	}
	
	public static Boolean handleJson109_11(String visitorId,ChannelHandlerContext channel) {
		//ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("type", 109);
		jsonObject.put("code", 11);
		jsonObject.put("VisitorId", visitorId);
		byte[] result = SocketService.responseByte(jsonObject, "109", "11");
		if (null != channel) {
			excuteWrite(result, channel);
			return true;
		} 
		else {
			return false;
		}
	}
	
	/** 
	 * @author lish
	 * @date 2016年10月11日
	 * 类说明 前端设备回应员工模板请求，暂无业务逻辑处理
	 */
	public static void handleJson109_2(JSONObject jsonObject,ChannelHandlerContext ctx) {
		String employeeId=jsonObject.getString("employeeId");
		String templateId=jsonObject.getString("templateId");
		String templateName=jsonObject.getString("templateName");
		String templateCount=jsonObject.getString("templateCount");
		String templateTime=jsonObject.getString("templateTime");
		String deviceId=ChannelNameToDeviceMap.getDeviceMap(ctx.channel().id());
		String path=EMPLOYEE_TEMPLATE+deviceId+"/"+employeeId;
		if (!(new File(path).isDirectory())) {
			new File(path).mkdirs();
		}
		AdminService adminService = (AdminService) ServiceDistribution.getContext().getBean("adminService");
		adminService.updateTemplatePath(employeeId, path);
		
	}
	
	/** 
	 * @author lish
	 * @date 2016年10月11日
	 * 类说明 前端设备回应访客模板请求，暂无业务逻辑处理
	 */
	public static void handleJson109_12(JSONObject jsonObject,ChannelHandlerContext ctx) {
		String visitorId=jsonObject.getString("visitorId");
		String templateId=jsonObject.getString("templateId");
		String templateName=jsonObject.getString("templateName");
		String templateCount=jsonObject.getString("templateCount");
		String templateTime=jsonObject.getString("templateTime");
		String deviceId=ChannelNameToDeviceMap.getDeviceMap(ctx.channel().id());
		String path=VISITOR_TEMPLATE+deviceId+"/"+visitorId;
		if (!(new File(path).isDirectory())) {
			new File(path).mkdirs();
		}
		VisitorService visitorService = (VisitorService) ServiceDistribution.getContext().getBean("visitorService");
		visitorService.updateVisitorTemplate(visitorId, path);
		
	}
	
	public static Boolean handleJson114_1(String deviceId,String version,String path){
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("type", 114);
		jsonObject.put("code", 1);
		jsonObject.put("url", path);
		jsonObject.put("version", version);
		
		String absolute=path.replace("120.26.60.164:5555", "/cloudweb/server/tomcat_intel/webapps");
		File file=new File(absolute);
		long size=file.length();
		jsonObject.put("length", size);	
		byte[] result = SocketService.responseByte(jsonObject, "114", "1");
		if (null != channel) {
			excuteWrite(result, channel);
			return true;
		} 
		else {
			return false;
		}
		
	}
	
	public static boolean handleJson115_1(String deviceId,String company) {
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("type", 115);
		jsonObject.put("code", 1);
		jsonObject.put("company", company);
		byte[] result = SocketService.responseByte(jsonObject, "115", "1");
		if (null != channel) {
			excuteWrite(result, channel);
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
			OutputStream out = new FileOutputStream(new File(path));
			out.write(b);
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static void excuteWrite(byte[] responseMsg,ChannelHandlerContext socketChannel){
		ByteBuf encoded = Unpooled.buffer();
		encoded.writeBytes(responseMsg);
		//System.out.println(encoded.getByte(0));
		socketChannel.write(encoded);
		socketChannel.flush();
	}
}
