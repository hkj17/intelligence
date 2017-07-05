package com.is.websocket;

import static com.is.constant.ParameterKeys.CLOCK_PHOTO_PATH;
import static com.is.constant.ParameterKeys.EMPLOYEE_TEMPLATE;
import static com.is.constant.ParameterKeys.FACE_PHOTO_PATH;
import static com.is.constant.ParameterKeys.VISITOR_TEMPLATE;
import static com.is.constant.ParameterKeys.XML;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.is.constant.ParameterKeys;
import com.is.map.ChannelNameToDeviceMap;
import com.is.map.DeviceService;
import com.is.map.DeviceToVersionMap;
import com.is.map.PhotoMap;
import com.is.model.CollectionPhoto;
import com.is.model.Company;
import com.is.model.Employee;
import com.is.model.Template;
import com.is.model.VisitorInfo;
import com.is.service.AdminService;
import com.is.service.ClockService;
import com.is.service.CompanyService;
import com.is.service.EmployeeService;
import com.is.service.VisitorService;
import com.is.util.Base64Utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.StringUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import sun.misc.BASE64Decoder;

@SuppressWarnings("restriction")
@Component("serviceDistribution")
public class ServiceDistribution implements ApplicationContextAware {
	private static Logger logger = Logger.getLogger(ServiceDistribution.class);

	private static Map<String, Long> messageMap = new HashMap<>();

	private static ApplicationContext context;

	@SuppressWarnings("static-access")
	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		this.context = context;

	}

	public static ApplicationContext getContext() {
		return context;
	}

	public static JSONObject handleJson1_1(JSONObject jsonObject, ChannelHandlerContext socketChannel) {
		String deviceSn = jsonObject.optString(ParameterKeys.DEVICE_SN);
		String versionCode = jsonObject.optString("version");
		if(StringUtil.isNullOrEmpty(deviceSn)||StringUtil.isNullOrEmpty(versionCode)){
			logger.error("设备号或版本号为空, <deviceId: "+deviceSn+", version: "+versionCode+">");
		}
		
		if (DeviceService.getSocketMap(deviceSn) != null) {
			ChannelHandlerContext old = DeviceService.getSocketMap(deviceSn);
			ChannelNameToDeviceMap.removeDeviceMap(old.channel().id());
			DeviceService.removeSocketMap(deviceSn);
		}
		
		DeviceService.addSocketMap(deviceSn, socketChannel);
		ChannelNameToDeviceMap.addDeviceMap(socketChannel.channel().id(), deviceSn);
		DeviceToVersionMap.addVersionMap(deviceSn, versionCode);
		
		CompanyService companyService = (CompanyService) ServiceDistribution.getContext().getBean("companyService");
		Company company = companyService.getCompanyInfo(deviceSn);
		JSONObject responseCode = new JSONObject();
		responseCode.put(ParameterKeys.TYPE, 1);
		responseCode.put(ParameterKeys.CODE, 2);
		responseCode.put(ParameterKeys.DEVICE_SN, deviceSn);
		if (company != null) {
			responseCode.put(ParameterKeys.COMPANY, company.getCompanyName());
			responseCode.put(ParameterKeys.ADDRESS, company.getAddress());
			responseCode.put(ParameterKeys.PHONE, company.getContact());
			responseCode.put("Sign_in_time_A", company.getMorningTimeStart());
			responseCode.put("Sign_in_time_B", company.getMorningTimeEnd());
			responseCode.put("Sign_out_time_A", company.getNightTimeStart());
			responseCode.put("Sign_out_time_B", company.getNightTimeEnd());
		} else {
			responseCode.put(ParameterKeys.COMPANY, "");
			responseCode.put(ParameterKeys.ADDRESS, "");
			responseCode.put(ParameterKeys.PHONE, "");
			responseCode.put("Sign_in_time_A", "");
			responseCode.put("Sign_in_time_B", "");
			responseCode.put("Sign_out_time_A", "");
			responseCode.put("Sign_out_time_B", "");
		}
		
		logger.info("1-2 respose code: " + responseCode);
		return responseCode;
	}

	public static JSONObject handleJson3_1(JSONObject jsonObject, ChannelHandlerContext socketChannel) {
		String employeeId = jsonObject.getString(ParameterKeys.EMPLOYEE_ID);
		String time = jsonObject.getString(ParameterKeys.TIME);
		ClockService clockService = (ClockService) ServiceDistribution.getContext().getBean("clockService");
		String deviceId = ChannelNameToDeviceMap.getDeviceMap(socketChannel.channel().id());
		try {
			clockService.addClocknormal(deviceId, employeeId, time);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		JSONObject responseCode = new JSONObject();
		responseCode.put(ParameterKeys.TYPE, 3);
		responseCode.put(ParameterKeys.CODE, 2);
		responseCode.put(ParameterKeys.EMPLOYEE_ID, employeeId);
		return responseCode;

	}

	public static JSONObject handleJson3_11(JSONObject jsonObject, ChannelHandlerContext socketChannel) {
		String visitorId = jsonObject.getString(ParameterKeys.VISITOR_ID);
		String time = jsonObject.getString(ParameterKeys.TIME);
		String employeeId = jsonObject.getString(ParameterKeys.EMPLOYEE_ID);
		if (!StringUtil.isNullOrEmpty(employeeId)) {
			VisitorService visitorService = (VisitorService) ServiceDistribution.getContext().getBean("visitorService");
			String deviceId = ChannelNameToDeviceMap.getDeviceMap(socketChannel.channel().id());
			visitorService.insertVisitor(deviceId, visitorId, time, employeeId, null);

			Long maptime = messageMap.get(visitorId);
			if (maptime == null) {
				EmployeeService employeeService = (EmployeeService) getContext().getBean("employeeService");
				employeeService.sendMsg(employeeId, "1644158");
				messageMap.put(visitorId, new Date().getTime());
			} else {
				long now = new Date().getTime();
				long cha = (now - maptime) / 1000 / 60;
				if (cha > 60) {
					EmployeeService employeeService = (EmployeeService) getContext().getBean("employeeService");
					employeeService.sendMsg(employeeId, "1644158");
					messageMap.put(visitorId, new Date().getTime());
				}
			}
		}

		JSONObject responseCode = new JSONObject();
		responseCode.put(ParameterKeys.TYPE, 3);
		responseCode.put(ParameterKeys.CODE, 12);
		responseCode.put(ParameterKeys.VISITOR_ID, visitorId);
		return responseCode;

	}

	public static JSONObject handleJson5_1(JSONObject jsonObject, ChannelHandlerContext socketChannel) {
		String visitorId = jsonObject.getString(ParameterKeys.VISITOR_ID);
		String time = jsonObject.getString(ParameterKeys.TIME);
		String employeeId = jsonObject.getString(ParameterKeys.EMPLOYEE_ID);
		VisitorService visitorService = (VisitorService) getContext().getBean("visitorService");
		String deviceId = ChannelNameToDeviceMap.getDeviceMap(socketChannel.channel().id());
		visitorService.insertVisitor(deviceId, visitorId, time, employeeId, null);
		EmployeeService employeeService = (EmployeeService) getContext().getBean("employeeService");
		employeeService.sendMsg(employeeId, "1525674");

		JSONObject responseCode = new JSONObject();
		responseCode.put(ParameterKeys.TYPE, 5);
		responseCode.put(ParameterKeys.CODE, 2);
		responseCode.put(ParameterKeys.VISITOR_ID, visitorId);
		return responseCode;

	}

	public static JSONObject handleJson8_1(JSONObject jsonObject, ChannelHandlerContext socketChannel) {
		
		String employeeId = jsonObject.optString(ParameterKeys.EMPLOYEE_ID);
		String imageSeqno = jsonObject.optString(ParameterKeys.IMAGE_SEQ_NO);
		String templateId = jsonObject.optString(ParameterKeys.TEMPLATE_ID);
		String imageName = jsonObject.optString(ParameterKeys.IMAGE_NAME);
		String photo = jsonObject.optString(ParameterKeys.IMAGE_STAT);
		String deviceId = ChannelNameToDeviceMap.getDeviceMap(socketChannel.channel().id());
		String path = EMPLOYEE_TEMPLATE + deviceId + "/" + employeeId;
		logger.info("< deviceId: "+deviceId + ", employeeId:"+employeeId + ">");
		if (!(new File(path).isDirectory())) {
			new File(path).mkdirs();
		}
		//序号
		String seqno = imageName;
		if(imageName.lastIndexOf("_")>=0){
			seqno = imageName.substring(imageName.lastIndexOf("_"));
		}else{
			logger.warn("没有找到文件的序号, 文件名: "+imageName);
		}
		String photopath = path + "/" + templateId + seqno;
		logger.info("写入模板图片路径: "+photopath);
		GenerateImage(photo, photopath);
		
		//统一在109-2里面更新
//		if (imageSeqno.equals("1")) {
//			AdminService adminService = (AdminService) ServiceDistribution.getContext().getBean("adminService");
//			adminService.updateTemplatePath(employeeId, path);
//		}
		
		JSONObject responseCode = new JSONObject();
		responseCode.put(ParameterKeys.TYPE, 8);
		responseCode.put(ParameterKeys.CODE, 2);
		responseCode.put(ParameterKeys.EMPLOYEE_ID, employeeId);
		responseCode.put(ParameterKeys.TEMPLATE_ID, templateId);
		responseCode.put(ParameterKeys.IMAGE_NAME, imageName);
		responseCode.put(ParameterKeys.IMAGE_SEQ_NO, imageSeqno);
		logger.info("8-2 response code: "+responseCode);
		return responseCode;

	}
	
	public static JSONObject handleJson8_1_end(JSONObject jsonObject, ChannelHandlerContext socketChannel) {	
		String employeeId = jsonObject.getString(ParameterKeys.EMPLOYEE_ID);	
		String templateId = jsonObject.getString(ParameterKeys.TEMPLATE_ID);
		JSONObject responseCode = new JSONObject();
		responseCode.put(ParameterKeys.TYPE, 8);
		responseCode.put(ParameterKeys.CODE, 2);
		responseCode.put(ParameterKeys.EMPLOYEE_ID, employeeId);
		responseCode.put(ParameterKeys.TEMPLATE_ID, templateId);
		return responseCode;

	}

	public static JSONObject handleJson8_11(JSONObject jsonObject, ChannelHandlerContext socketChannel) {
		String visitorId = jsonObject.getString(ParameterKeys.VISITOR_ID);
		String imageSeqno = jsonObject.getString(ParameterKeys.IMAGE_SEQ_NO);
		String imageName = jsonObject.getString(ParameterKeys.IMAGE_NAME);
		String photo = jsonObject.getString(ParameterKeys.IMAGE_STAT);
		String deviceId = ChannelNameToDeviceMap.getDeviceMap(socketChannel.channel().id());
		String path = VISITOR_TEMPLATE + deviceId + "/" + visitorId;
		if (!(new File(path).isDirectory())) {
			new File(path).mkdirs();
		}
		String photopath = path + "/" + imageName;
		GenerateImage(photo, photopath);

		if (imageSeqno.equals("1")) {
			VisitorService visitorService = (VisitorService) getContext().getBean("visitorService");
			visitorService.updateVisitorTemplate(visitorId, path);
		}
		JSONObject responseCode = new JSONObject();
		responseCode.put(ParameterKeys.TYPE, 8);
		responseCode.put(ParameterKeys.CODE, 12);
		responseCode.put(ParameterKeys.VISITOR_ID, visitorId);
		responseCode.put(ParameterKeys.IMAGE_NAME, imageName);
		responseCode.put(ParameterKeys.IMAGE_SEQ_NO, imageSeqno);
		return responseCode;

	}

	public static JSONObject handleJson4_1(JSONObject jsonObject, ChannelHandlerContext socketChannel) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String photo = jsonObject.getString(ParameterKeys.PHOTO);
		if (photo.startsWith("data")) {
			//TODO:容错
			photo = photo.substring(photo.indexOf(",") + 1);
		}
		String id = jsonObject.getString(ParameterKeys.STRANGER_ID);
		String deviceId = ChannelNameToDeviceMap.getDeviceMap(socketChannel.channel().id());
		String path = FACE_PHOTO_PATH + deviceId + "/" + sdf.format(new Date());
		if (!(new File(path).isDirectory())) {
			new File(path).mkdirs();
		}
		path = path + "/" + id + ".jpg";
		GenerateImage(photo, path);

		String time = jsonObject.getString(ParameterKeys.TIME);
		String employeeId = jsonObject.getString(ParameterKeys.EMPLOYEE_ID);
		VisitorService visitorService = (VisitorService) getContext().getBean("visitorService");
		visitorService.insertVisitor(deviceId, null, time, employeeId, path);
		EmployeeService employeeService = (EmployeeService) getContext().getBean("employeeService");
		employeeService.sendMsg(employeeId, "1525674");

		JSONObject responseCode = new JSONObject();
		responseCode.put(ParameterKeys.TYPE, 4);
		responseCode.put(ParameterKeys.CODE, 2);
		responseCode.put(ParameterKeys.STRANGER_ID, id);
		return responseCode;

	}

	public static JSONObject handleJson100_100() {
		JSONObject responseCode = new JSONObject();
		responseCode.put(ParameterKeys.TYPE, 100);
		responseCode.put(ParameterKeys.CODE, 100);
		return responseCode;
	}

	public static boolean handleJson101_1(String deviceId) {
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ParameterKeys.TYPE, 101);
		jsonObject.put(ParameterKeys.CODE, 1);
		byte[] result = SocketService.responseByte(jsonObject, "101", "1");
		if (null != channel) {
			executeWrite(result, channel);
			return true;
		} else
			return false;
	}

	public static JSONObject handleJson3_21(JSONObject jsonObject, ChannelHandlerContext ctx) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String photo = jsonObject.getString(ParameterKeys.PHOTO);
		String time = jsonObject.getString(ParameterKeys.TIME);
		if (photo.startsWith("data")) {
			photo = photo.substring(photo.indexOf(",") + 1);
		}
		String id = jsonObject.getString(ParameterKeys.STRANGER_ID);
		String deviceId = ChannelNameToDeviceMap.getDeviceMap(ctx.channel().id());
		VisitorService visitorService = (VisitorService) getContext().getBean("visitorService");
		CollectionPhoto collectionPhoto = visitorService.getCollectByStrangerId(id);
		if (collectionPhoto == null) {
			String path = FACE_PHOTO_PATH + deviceId + "/" + sdf.format(new Date());
			if (!(new File(path).isDirectory())) {
				new File(path).mkdirs();
			}
			path = path + "/" + id + ".jpg";
			GenerateImage(photo, path);

			visitorService.collectionPhoto(deviceId, time, path, id);
		}

		JSONObject response = new JSONObject();
		response.put(ParameterKeys.TYPE, 3);
		response.put(ParameterKeys.CODE, 22);
		response.put(ParameterKeys.STRANGER_ID, id);
		return response;
	}

	public static JSONObject handleJson3_23(JSONObject jsonObject, ChannelHandlerContext ctx) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String photo = jsonObject.getString(ParameterKeys.PHOTO);
		if (photo.startsWith("data")) {
			//TODO: 容错
			photo = photo.substring(photo.indexOf(",") + 1);
		}
		String id = jsonObject.getString(ParameterKeys.STRANGER_ID);
		String deviceId = ChannelNameToDeviceMap.getDeviceMap(ctx.channel().id());
		String path = FACE_PHOTO_PATH + deviceId + "/" + sdf.format(new Date());
		;
		if (!(new File(path).isDirectory())) {
			new File(path).mkdirs();
		}
		path = path + "/" + id + ".jpg";
		boolean state = GenerateImage(photo, path);
		if (state) {
			PhotoMap.addMap(deviceId, path);
		}
		JSONObject response = new JSONObject();
		response.put(ParameterKeys.TYPE, 3);
		response.put(ParameterKeys.CODE, 24);
		response.put(ParameterKeys.STRANGER_ID, id);
		return response;

	}

	public static void handleJson102_1(String deviceId) {
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ParameterKeys.TYPE, 102);
		jsonObject.put(ParameterKeys.CODE, 1);
		byte[] result = SocketService.responseByte(jsonObject, "102", "1");
		if (null != channel) {
			executeWrite(result, channel);
		}
	}

	public static Boolean handleJson103_1(String employeeId, String templateId, String strangerId, String employeeName, String birth,
			String deviceId) {
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ParameterKeys.TYPE, 103);
		jsonObject.put(ParameterKeys.CODE, 1);
		jsonObject.put(ParameterKeys.EMPLOYEE_ID, employeeId);
		jsonObject.put(ParameterKeys.TEMPLATE_ID, templateId);
		jsonObject.put(ParameterKeys.STRANGER_ID, strangerId);
		jsonObject.put(ParameterKeys.EMPLOYEE_NAME, employeeName);
		jsonObject.put(ParameterKeys.BIRTH, birth);
		logger.info("103-1 code: "+jsonObject);
		byte[] result = SocketService.responseByte(jsonObject, "103", "1");
		if (null != channel) {
			executeWrite(result, channel);
			return true;
		} else {
			logger.warn("设备号" + deviceId + " 103-1无法写入web socket!");
			return false;
		}
	}

	public static Boolean handleJson106_1(String deviceId, String employeeId, String startTime, String endTime,
			String id, String visitorId, String message) {
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ParameterKeys.TYPE, 106);
		jsonObject.put(ParameterKeys.CODE, 1);
		jsonObject.put(ParameterKeys.EMPLOYEE_ID, employeeId);
		jsonObject.put(ParameterKeys.BEGIN_TIME, startTime);
		jsonObject.put(ParameterKeys.END_TIME, endTime);
		jsonObject.put(ParameterKeys.APPOINTMENT_ID, id);
		jsonObject.put(ParameterKeys.VISITOR_ID, visitorId);
		jsonObject.put(ParameterKeys.MESSAGE, message);
		byte[] result = SocketService.responseByte(jsonObject, "106", "1");
		if (null != channel) {
			executeWrite(result, channel);
			return true;
		} else {
			return false;
		}
	}

	public static Boolean handleJson106_11(String deviceId, String employeeId, String startTime, String endTime,
			String appointmentId, String visitorId, String message) {
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ParameterKeys.TYPE, 106);
		jsonObject.put(ParameterKeys.CODE, 11);
		jsonObject.put(ParameterKeys.EMPLOYEE_ID, employeeId);
		jsonObject.put(ParameterKeys.BEGIN_TIME, startTime);
		jsonObject.put(ParameterKeys.END_TIME, endTime);
		jsonObject.put(ParameterKeys.APPOINTMENT_ID, appointmentId);
		jsonObject.put(ParameterKeys.VISITOR_ID, visitorId);
		jsonObject.put(ParameterKeys.MESSAGE, message);
		byte[] result = SocketService.responseByte(jsonObject, "106", "11");
		if (null != channel) {
			executeWrite(result, channel);
			return true;
		} else {
			return false;
		}
	}

	public static Boolean handleJson106_21(String deviceId, String appointmentId, String employeeId, String visitorId) {
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ParameterKeys.TYPE, 106);
		jsonObject.put(ParameterKeys.CODE, 21);
		jsonObject.put(ParameterKeys.APPOINTMENT_ID, appointmentId);
		jsonObject.put(ParameterKeys.EMPLOYEE_ID, employeeId);
		jsonObject.put(ParameterKeys.VISITOR_ID, visitorId);
		byte[] result = SocketService.responseByte(jsonObject, "106", "21");
		if (null != channel) {
			executeWrite(result, channel);
			return true;
		} else {
			return false;
		}
	}

	public static Boolean handleJson103_11(String visitorId, String strangerId, String visitorName, String company,
			String position, String birth, String deviceId, String path) {
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ParameterKeys.TYPE, 103);
		jsonObject.put(ParameterKeys.CODE, 11);
		jsonObject.put(ParameterKeys.VISITOR_ID, visitorId);
		jsonObject.put(ParameterKeys.STRANGER_ID, strangerId);
		jsonObject.put(ParameterKeys.VISITOR_NAME, visitorName);
		jsonObject.put(ParameterKeys.COMPANY, company);
		jsonObject.put("position", position);
		try {
			if (path != null) {
				String base64 = Base64Utils.GetImageStr(path);
				jsonObject.put(ParameterKeys.PHOTO, base64);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (birth != null) {
			jsonObject.put(ParameterKeys.BIRTH, birth);
		}
		byte[] result = SocketService.responseByte(jsonObject, "103", "11");
		if (null != channel) {
			executeWrite(result, channel);
			return true;
		} else {
			return false;
		}
	}

	public static boolean handleJson110_1(String deviceId, String employeeId, String time) {
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ParameterKeys.TYPE, 110);
		jsonObject.put(ParameterKeys.CODE, 1);
		jsonObject.put(ParameterKeys.EMPLOYEE_ID, employeeId);
		jsonObject.put(ParameterKeys.TIME, time);
		byte[] result = SocketService.responseByte(jsonObject, "110", "1");
		if (null != channel) {
			executeWrite(result, channel);
			return true;
		} else {
			return false;
		}
	}

	public static void handleJson110_2(JSONObject jsonObject, ChannelHandlerContext ctx) {
		String employeeId = jsonObject.getString(ParameterKeys.EMPLOYEE_ID);
		String shootId = jsonObject.getString("shootId");
		String photo = jsonObject.getString(ParameterKeys.PHOTO);
		String deviceId = ChannelNameToDeviceMap.getDeviceMap(ctx.channel().id());

		String path = CLOCK_PHOTO_PATH + deviceId + "/" + employeeId;
		if (!(new File(path).isDirectory())) {
			new File(path).mkdirs();
		}
		path = path + "/" + shootId + ".jpg";
		GenerateImage(photo, path);
		logger.info(path);

		ClockService clockService = (ClockService) ServiceDistribution.getContext().getBean("clockService");
		clockService.insertAbonormalClockPhoto(employeeId, path, deviceId);

	}

	public static Boolean handleJson104_11(String deviceId, String id, String name, String company, String position,
			String telphone, String email, String importance, String birth) {
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ParameterKeys.TYPE, 104);
		jsonObject.put(ParameterKeys.CODE, 11);
		jsonObject.put(ParameterKeys.VISITOR_ID, id);
		jsonObject.put(ParameterKeys.VISITOR_NAME, name);
		jsonObject.put(ParameterKeys.BIRTH, birth);
		jsonObject.put(ParameterKeys.COMPANY, company);
		jsonObject.put("importance", importance);
		jsonObject.put("position", position);
		jsonObject.put("telephone", telphone);
		jsonObject.put(ParameterKeys.EMAIL, email);
		byte[] result = SocketService.responseByte(jsonObject, "104", "11");
		if (null != channel) {
			executeWrite(result, channel);
			return true;
		} else {
			return false;
		}
	}

	public static Boolean handleJson104_1(String deviceId, String employeeId, String name, String birth,
			String strangerId) {
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ParameterKeys.TYPE, 104);
		jsonObject.put(ParameterKeys.CODE, 1);
		jsonObject.put(ParameterKeys.EMPLOYEE_ID, employeeId);
		jsonObject.put(ParameterKeys.EMPLOYEE_NAME, name);
		jsonObject.put(ParameterKeys.STRANGER_ID, strangerId);
		jsonObject.put(ParameterKeys.BIRTH, birth);
		byte[] result = SocketService.responseByte(jsonObject, "104", "1");
		if (null != channel) {
			executeWrite(result, channel);
			return true;
		} else {
			return false;
		}
	}

	public static Boolean handleJson105_1(String deviceId, String employeeId) {
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ParameterKeys.TYPE, 105);
		jsonObject.put(ParameterKeys.CODE, 1);
		jsonObject.put(ParameterKeys.EMPLOYEE_ID, employeeId);
		byte[] result = SocketService.responseByte(jsonObject, "105", "1");
		if (null != channel) {
			executeWrite(result, channel);
			return true;
		} else {
			return false;
		}
	}

	public static Boolean handleJson105_11(String deviceId, String visitorId) {
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ParameterKeys.TYPE, 105);
		jsonObject.put(ParameterKeys.CODE, 11);
		jsonObject.put(ParameterKeys.VISITOR_ID, visitorId);
		byte[] result = SocketService.responseByte(jsonObject, "105", "11");
		if (null != channel) {
			executeWrite(result, channel);
			return true;
		} else {
			return false;
		}
	}

	public static Boolean handleJson105_21(String strangerId, String deviceId) {
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ParameterKeys.TYPE, 105);
		jsonObject.put(ParameterKeys.CODE, 21);
		jsonObject.put(ParameterKeys.STRANGER_ID, strangerId);
		byte[] result = SocketService.responseByte(jsonObject, "105", "21");
		if (null != channel) {
			executeWrite(result, channel);
			return true;
		} else {
			return false;
		}
	}

	public static Boolean handleJson109_1(String employeeId, String templateId, String strangerId, ChannelHandlerContext channel) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ParameterKeys.TYPE, 109);
		jsonObject.put(ParameterKeys.CODE, 1);
		jsonObject.put(ParameterKeys.EMPLOYEE_ID, employeeId);
		jsonObject.put(ParameterKeys.TEMPLATE_ID, templateId);
		jsonObject.put(ParameterKeys.STRANGER_ID, strangerId);
		logger.info("109-1 code: "+jsonObject);
		byte[] result = SocketService.responseByte(jsonObject, "109", "1");
		if (null != channel) {
			executeWrite(result, channel);
			return true;
		} else {
			logger.warn("109-1无法写入web socket!");
			return false;
		}
	}

	public static Boolean handleJson109_11(String visitorId, ChannelHandlerContext channel) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ParameterKeys.TYPE, 109);
		jsonObject.put(ParameterKeys.CODE, 11);
		jsonObject.put(ParameterKeys.VISITOR_ID, visitorId);
		byte[] result = SocketService.responseByte(jsonObject, "109", "11");
		if (null != channel) {
			executeWrite(result, channel);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @author lish
	 * @date 2016年10月11日 类说明 前端设备回应员工模板请求，暂无业务逻辑处理
	 */
	public static void handleJson109_2(JSONObject jsonObject, ChannelHandlerContext ctx) {
		String employeeId = jsonObject.optString(ParameterKeys.EMPLOYEE_ID);
		String templateId = jsonObject.optString(ParameterKeys.TEMPLATE_ID);
		if(StringUtil.isNullOrEmpty(employeeId)||StringUtil.isNullOrEmpty(templateId)){
			logger.warn("109-2报文有信息为空");
			return;
		}
		//String templateName = jsonObject.getString("templateName");
		//String imageCount = jsonObject.getString("imageCount");
		//String templateTime = jsonObject.getString("templateTime");
//		String deviceId = ChannelNameToDeviceMap.getDeviceMap(ctx.channel().id());
//		String path = EMPLOYEE_TEMPLATE + deviceId + "/" + employeeId;
//		logger.info("模板图片文件夹名: "+path);
//		if (!(new File(path).isDirectory())) {
//			new File(path).mkdirs();
//		}
//		AdminService adminService = (AdminService) ServiceDistribution.getContext().getBean("adminService");
//		adminService.updateTemplatePath(employeeId, path);
//		adminService.updateTemplatePath2(templateId, path);
	}

	/**
	 * @author lish
	 * @date 2016年10月11日 类说明 前端设备回应访客模板请求，暂无业务逻辑处理
	 */
	public static void handleJson109_12(JSONObject jsonObject, ChannelHandlerContext ctx) {
		String visitorId = jsonObject.getString(ParameterKeys.VISITOR_ID);
		String templateId = jsonObject.getString(ParameterKeys.TEMPLATE_ID);
		//String templateName = jsonObject.getString("templateName");
		//String templateCount = jsonObject.getString("templateCount");
		//String templateTime = jsonObject.getString("templateTime");
		String deviceId = ChannelNameToDeviceMap.getDeviceMap(ctx.channel().id());
		String path = VISITOR_TEMPLATE + deviceId + "/" + visitorId;
		if (!(new File(path).isDirectory())) {
			new File(path).mkdirs();
		}
		VisitorService visitorService = (VisitorService) ServiceDistribution.getContext().getBean("visitorService");
		visitorService.updateVisitorTemplate(visitorId, path);

	}

	public static Boolean handleJson114_1(String deviceId, String version, String path) {
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ParameterKeys.TYPE, 114);
		jsonObject.put(ParameterKeys.CODE, 1);
		jsonObject.put("url", path);
		jsonObject.put("version", version);

		String absolute = path.replace("120.26.60.164:5555", "/cloudweb/server/tomcat_intel/webapps");
		File file = new File(absolute);
		long size = file.length();
		jsonObject.put("length", size);
		byte[] result = SocketService.responseByte(jsonObject, "114", "1");
		if (null != channel) {
			executeWrite(result, channel);
			return true;
		} else {
			System.out.println("channel not established");
			return false;
		}

	}

	public static Boolean handleJson112_1(String deviceId) {
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ParameterKeys.TYPE, 112);
		jsonObject.put(ParameterKeys.CODE, 1);
		byte[] result = SocketService.responseByte(jsonObject, "112", "1");
		if (null != channel) {
			executeWrite(result, channel);
			return true;
		} else {
			return false;
		}

	}
	
	public static Boolean handleJson119_1(String deviceId) {
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ParameterKeys.TYPE, 119);
		jsonObject.put(ParameterKeys.CODE, 1);
		byte[] result = SocketService.responseByte(jsonObject, "119", "1");
		if (null != channel) {
			executeWrite(result, channel);
			return true;
		} else {
			return false;
		}

	}

	public static Boolean handleJson112_11(String deviceId, String voice) {
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ParameterKeys.TYPE, 112);
		jsonObject.put(ParameterKeys.CODE, 11);
		if (!StringUtil.isNullOrEmpty(voice)) {
			jsonObject.put("volume", Integer.parseInt(voice));
		}
		byte[] result = SocketService.responseByte(jsonObject, "112", "11");
		if (null != channel) {
			executeWrite(result, channel);
			return true;
		} else {
			return false;
		}

	}
	
	public static Boolean handleJson119_11(String deviceId, String focus) {
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ParameterKeys.TYPE, 119);
		jsonObject.put(ParameterKeys.CODE, 11);
		if (!StringUtil.isNullOrEmpty(focus)) {
			jsonObject.put("focus", Integer.parseInt(focus));
		}
		byte[] result = SocketService.responseByte(jsonObject, "119", "11");
		if (null != channel) {
			executeWrite(result, channel);
			return true;
		} else {
			return false;
		}

	}

	public static Boolean handleJson113_1(String deviceId) {
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ParameterKeys.TYPE, 113);
		jsonObject.put(ParameterKeys.CODE, 1);
		byte[] result = SocketService.responseByte(jsonObject, "113", "1");
		if (null != channel) {
			executeWrite(result, channel);
			return true;
		} else {
			return false;
		}

	}

	public static boolean handleJson115_1(String deviceId, String name, String address, String phone,
			String morningTimeStart, String morningTimeEnd, String nightTimeStart, String nightTimeEnd, int i) {
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ParameterKeys.TYPE, 115);
		jsonObject.put(ParameterKeys.CODE, 1);
		jsonObject.put(ParameterKeys.COMPANY, name);
		jsonObject.put(ParameterKeys.ADDRESS, address);
		jsonObject.put(ParameterKeys.PHONE, phone);
		jsonObject.put("znqt_num", String.valueOf(i));
		jsonObject.put("Sign_in_time_A", morningTimeStart);
		jsonObject.put("Sign_in_time_B", morningTimeEnd);
		jsonObject.put("Sign_out_time_A", nightTimeStart);
		jsonObject.put("Sign_out_time_B", nightTimeEnd);
		byte[] result = SocketService.responseByte(jsonObject, "115", "1");
		if (null != channel) {
			executeWrite(result, channel);
			return true;
		} else {
			return false;
		}
	}

	public static boolean GenerateImage(String imgStr, String path) { // 对字节数组字符串进行Base64解码并生成图片
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

	private static void executeWrite(byte[] responseMsg, ChannelHandlerContext socketChannel) {
		ByteBuf encoded = Unpooled.buffer();
		encoded.writeBytes(responseMsg);
		socketChannel.write(encoded);
		socketChannel.flush();
	}

	public static Boolean handleJson111_11(String deviceId, String path, String id) {
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ParameterKeys.TYPE, 111);
		jsonObject.put(ParameterKeys.CODE, 11);
		String adPic = Base64Utils.GetImageStr(path);
		jsonObject.put("picId", id);
		jsonObject.put("adPic", adPic);
		byte[] result = SocketService.responseByte(jsonObject, "111", "11");
		if (null != channel) {
			executeWrite(result, channel);
			return true;
		} else {
			return false;
		}
	}

	public static Boolean handleJson111_21(String deviceId, String path) {
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ParameterKeys.TYPE, 111);
		jsonObject.put(ParameterKeys.CODE, 21);
		//TODO: 容错
		String id = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
		jsonObject.put("picId", id);
		byte[] result = SocketService.responseByte(jsonObject, "111", "21");
		if (null != channel) {
			executeWrite(result, channel);
			return true;
		} else {
			return false;
		}
	}

	public static void handleJson7_1(JSONObject jsonObject, ChannelHandlerContext socketChannel) {
		String employeeId = jsonObject.optString(ParameterKeys.EMPLOYEE_ID);
		String templateId = jsonObject.optString(ParameterKeys.TEMPLATE_ID);
		if(StringUtil.isNullOrEmpty(employeeId)||StringUtil.isNullOrEmpty(templateId)){
			logger.warn("7-1报文中有空信息");
			return;
		}
		AdminService adminService = (AdminService) ServiceDistribution.getContext().getBean("adminService");
		Employee employee = adminService.getEmployeeById(employeeId);
		String templatePath = employee.getTemplatePath();
		if (!StringUtil.isNullOrEmpty(templatePath)) {
			File file = new File(templatePath);
			if (file.isDirectory()) {
				File[] files = file.listFiles();
				int num = 0;
				int isEnd=0;
				for (File fileTemp : files) {
					//只传送该模板的图片信息
					if(fileTemp.getName().startsWith(templateId)){
						num++;
						String path = fileTemp.getAbsolutePath();
						String adPic = Base64Utils.GetImageStr(path);
						if(num==files.length){
							isEnd=1;
						}
						sendTempToDevice(adPic, num, employeeId, templateId, socketChannel,employee.getEmployeeFold(),isEnd,fileTemp.getName());
					}
				}
			}
		} else {
			sendTempToDevice("", 0, employeeId, templateId, socketChannel,employee.getEmployeeFold(),1,null);
		}
	}

	public static void handleJson7_11(JSONObject jsonObject, ChannelHandlerContext socketChannel) {
		String visitorId = jsonObject.getString(ParameterKeys.VISITOR_ID);
		VisitorService visitorService = (VisitorService) ServiceDistribution.getContext().getBean("visitorService");
		VisitorInfo info = visitorService.getVisitorById(visitorId);
		String templatePath = info.getTemplatePath();
		if (!StringUtil.isNullOrEmpty(templatePath)) {
			File file = new File(templatePath);
			if (file.isDirectory()) {
				File[] files = file.listFiles();
				for (File fileTemp : files) {
					String path = fileTemp.getAbsolutePath();
					String adPic = Base64Utils.GetImageStr(path);
					String imageName = fileTemp.getName();
					sendVisitorTempToDevice(adPic, imageName, visitorId, socketChannel);
				}
			}
		} else {
			sendVisitorTempToDevice("", "", visitorId, socketChannel);
		}
	}

	private static void sendVisitorTempToDevice(String adPic, String imageName, String visitorId,
			ChannelHandlerContext channel) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ParameterKeys.TYPE, 7);
		jsonObject.put(ParameterKeys.CODE, 12);
		jsonObject.put(ParameterKeys.IMAGE_STAT, adPic);
		jsonObject.put(ParameterKeys.IMAGE_NAME, imageName);
		jsonObject.put(ParameterKeys.VISITOR_ID, visitorId);
		byte[] result = SocketService.responseByte(jsonObject, "7", "12");
		if (null != channel) {
			executeWrite(result, channel);
		}
	}

	private static void sendTempToDevice(String adPic, int num, String employeeId, String templateId, ChannelHandlerContext channel,String employeeFold, int isEnd,String filename) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ParameterKeys.TYPE, 7);
		jsonObject.put(ParameterKeys.CODE, 2);
		jsonObject.put(ParameterKeys.IMAGE_STAT, adPic);
		jsonObject.put(ParameterKeys.IMAGE_NAME, filename);
		jsonObject.put(ParameterKeys.IMAGE_SEQ_NO, String.valueOf(num));
		jsonObject.put("isEnd", String.valueOf(isEnd));
		jsonObject.put(ParameterKeys.EMPLOYEE_ID, employeeId);
		jsonObject.put(ParameterKeys.TEMPLATE_ID, templateId);
		jsonObject.put(ParameterKeys.EMPLOYEE_FOLD, employeeFold);
		byte[] result = SocketService.responseByte(jsonObject, "7", "2");
		if (null != channel) {
			executeWrite(result, channel);
		}
	}

	public static JSONObject handleJson9_1(JSONObject jsonObject, ChannelHandlerContext socketChannel) {
		String data=null;
		try {
			data = Base64Utils.encodeFile(XML+"employee.xml");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JSONObject result = new JSONObject();
		result.put(ParameterKeys.TYPE, 9);
		result.put(ParameterKeys.CODE, 2);
		result.put("fileData", data);
		return result;
	}

	/*public static String beanToXML(List<Employee> list) {
		try {
			JAXBContext context = JAXBContext.newInstance(Employee.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);  
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");  
            StringWriter writer = new StringWriter();  
            marshaller.marshal(list, writer);  
            String result = writer.toString();
            return result;
		} catch (JAXBException e) {
			e.printStackTrace();
			return null;
		}
	}*/
	
	public static JSONObject handleJson9_11(JSONObject jsonObject, ChannelHandlerContext socketChannel) {
		String data=null;
		try {
			data = Base64Utils.encodeFile(XML+"visitor.xml");
		} catch (Exception e) {
			e.printStackTrace();
		}
		JSONObject result = new JSONObject();
		result.put(ParameterKeys.TYPE, 9);
		result.put(ParameterKeys.CODE, 12);
		result.put("fileData", data);
		return result;
	}
	
	/*public static String visitorBeanToXML(List<VisitorInfo> list) {
		try {
			JAXBContext context = JAXBContext.newInstance(VisitorInfo.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);  
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");  
            StringWriter writer = new StringWriter();  
            marshaller.marshal(list, writer);  
            String result = writer.toString();
            return result;
		} catch (JAXBException e) {
			e.printStackTrace();
			return null;
		}
	}*/

	public static JSONObject handleJson10_1(JSONObject jsonObject, ChannelHandlerContext socketChannel) {
		String fileData = jsonObject.getString("fileData");
		String fileName = jsonObject.getString("fileName");
		try {
			Base64Utils.decodeToFile(XML+fileName, fileData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		JSONObject result = new JSONObject();
		result.put(ParameterKeys.TYPE, 10);
		result.put(ParameterKeys.CODE, 2);
		return result;
	}

	public static JSONObject handleJson10_11(JSONObject jsonObject, ChannelHandlerContext socketChannel) {
		String fileData = jsonObject.getString("fileData");
		String fileName = jsonObject.getString("fileName");
		try {
			Base64Utils.decodeToFile(XML+fileName, fileData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		JSONObject result = new JSONObject();
		result.put(ParameterKeys.TYPE, 10);
		result.put(ParameterKeys.CODE, 12);
		return result;
	}


	public static void handleJson118_1(String deviceId, String employeeId, String employeeFold, String employeeName,
			String templateId, String birth, String photoPath) {
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		String base64=Base64Utils.GetImageStr(photoPath);
		JSONObject jsonObject=new JSONObject();
		jsonObject.put(ParameterKeys.TYPE, 118);
		jsonObject.put(ParameterKeys.CODE, 1);
		jsonObject.put(ParameterKeys.EMPLOYEE_ID, employeeId);
		jsonObject.put(ParameterKeys.EMPLOYEE_FOLD, employeeFold);
		jsonObject.put(ParameterKeys.EMPLOYEE_NAME, employeeName);
		jsonObject.put(ParameterKeys.TEMPLATE_ID, templateId);
		jsonObject.put(ParameterKeys.BIRTH, birth);
		jsonObject.put(ParameterKeys.PORTRAIT, base64);
		jsonObject.put(ParameterKeys.OPERATION, "add");
		//logger.info("118-1 code: " + jsonObject);
		byte[] result = SocketService.responseByte(jsonObject, "118", "1");
		if (null != channel) {
			executeWrite(result, channel);
		}else{
			logger.warn("118-1 cannot find socket channel");
		}
		
		
	}

	public static void handleJson103_12(JSONObject jsonObject, ChannelHandlerContext socketChannel) {
		String deviceId=ChannelNameToDeviceMap.getDeviceMap(socketChannel.channel().id());
		String visitorId = jsonObject.getString(ParameterKeys.VISITOR_ID);
		String visitorFold = jsonObject.getString(ParameterKeys.VISITOR_FOLD);
		VisitorService visitorService = (VisitorService) ServiceDistribution.getContext().getBean("visitorService");
		visitorService.updateVisitorAndSync(visitorId,visitorFold,deviceId);
	}

	public static void handleJson118_11(String id, String visitorId, String visitorFold, String name, String birth,
			String photoPath) {
		ChannelHandlerContext channel = DeviceService.getSocketMap(id);
		String base64=Base64Utils.GetImageStr(photoPath);
		JSONObject jsonObject=new JSONObject();
		jsonObject.put(ParameterKeys.TYPE, 118);
		jsonObject.put(ParameterKeys.CODE, 11);
		jsonObject.put(ParameterKeys.VISITOR_ID, visitorId);
		jsonObject.put(ParameterKeys.VISITOR_FOLD, visitorFold);
		jsonObject.put(ParameterKeys.VISITOR_NAME, name);
		jsonObject.put(ParameterKeys.BIRTH, birth);
		jsonObject.put(ParameterKeys.PORTRAIT, base64);
		jsonObject.put(ParameterKeys.OPERATION, "add");
		byte[] result = SocketService.responseByte(jsonObject, "118", "11");
		if (null != channel) {
			executeWrite(result, channel);
		}
	}

	public static void SyncEmployee(JSONObject jsonObject) {
		String employeeId = jsonObject.optString(ParameterKeys.EMPLOYEE_ID);
		String templateId = jsonObject.optString(ParameterKeys.TEMPLATE_ID);
		if(StringUtil.isNullOrEmpty(employeeId)||StringUtil.isNullOrEmpty(templateId)){
			logger.warn("no key employeeId or templateId in 8-2");
			return;
		}
		AdminService adminService = (AdminService) ServiceDistribution.getContext().getBean("adminService");
		adminService.updateEmployeeFoldAndSync(employeeId,templateId);
	}

	public static void handleJson12_1(JSONObject jsonObject, ChannelHandlerContext socketChannel) {
		String deviceId=ChannelNameToDeviceMap.getDeviceMap(socketChannel.channel().id());
		JSONArray jsonArray = jsonObject.optJSONArray("employeeIdArray");
		if(jsonArray==null){
			logger.warn("no key \"employeeIdArray\" associated with json array in 12-1");
			return;
		}
		
		//前端发过来的员工数据
		Set<String> employeeIdSet = new HashSet<String>();
		Map<String, Employee> employeeMap = new HashMap<String, Employee>();
		for(int i=0;i<jsonArray.size();i++){
			JSONObject employeeRecord = jsonArray.getJSONObject(i);
			String employeeId = employeeRecord.optString(ParameterKeys.EMPLOYEE_ID);
			if(StringUtil.isNullOrEmpty(employeeId)){
				continue;
			}
			if(employeeIdSet.add(employeeId)){
				Employee employee = new Employee();
				employee.setEmployeeId(employeeId);
				employee.setEmpVersion(employeeRecord.optInt("empVersion"));
				employee.setTempVersion(employeeRecord.optInt("tempVersion"));
				employeeMap.put(employeeId, employee);
			}
		}
		
		//数据库中的员工数据
		AdminService adminService = (AdminService) ServiceDistribution.getContext().getBean("adminService");
		List<Employee> employeeList = adminService.getEmployeeListByDeviceId(deviceId);
		
		
		boolean state = true;
		for(int i=0;i<employeeList.size();i++){
			Employee employee = employeeList.get(i);
			if(employee==null){
				continue;
			}
			
			//机器上存在该条信息
			if(employeeIdSet.contains(employee.getEmployeeId())){
				String employeeId = employee.getEmployeeId();
				Employee empFromDevice = employeeMap.remove(employeeId);
				if(employee.getEmpVersion()!=empFromDevice.getEmpVersion() && employee.getTempVersion()!=empFromDevice.getTempVersion()){
					state = send12_2(deviceId, employee,"both");
				}else if(employee.getEmpVersion()!=empFromDevice.getEmpVersion()){
					state = send12_2(deviceId, employee,"emp");
				}else if(employee.getTempVersion()!=empFromDevice.getTempVersion()){
					state = send12_2(deviceId, employee,"temp");
				}else{
					//版本完全相同
				}
				employeeIdSet.remove(employeeId);
			}else{//机器上不存在该条信息
				state = send12_2(deviceId, employee, "both");
			}
			if(!state) 
				return;
		}
		
		//数据库中已经不存在的员工信息
		for(String employeeId: employeeIdSet){
			state = send12_2(deviceId, employeeMap.get(employeeId), "del");
			if(!state) 
				return;
		}
		
//		String employeeIds = jsonObject.getString("employeeIdArray");
//		if(!StringUtil.isNullOrEmpty(employeeIds)){
//			employeeIds=employeeIds.replace("[", "(").replace("]", ")");
//		}
//		AdminService adminService = (AdminService) ServiceDistribution.getContext().getBean("adminService");
//		List<Employee> list=adminService.getEmployeeByIds(employeeIds,deviceId);
//		for(Employee employee:list){
//			sendAdd12_2(deviceId, employee.getEmployeeId(), employee.getEmployeeFold(), employee.getEmployeeName(), employee.getBirth(), employee.getPhotoPath());
//		}
//		
//		if(!StringUtil.isNullOrEmpty(employeeIds)){
//			List<String> existEmployee=adminService.getExistEmployee(employeeIds,deviceId);
//			String[] eids=employeeIds.substring(1,employeeIds.length()-1).split(",");
//			for(String eid:eids){
//				eid=eid.substring(1,eid.length()-1);
//				if(!existEmployee.contains(eid)){
//					sendDel12_2(deviceId,eid);
//				}
//			}
//		}
	}
	
	private static boolean send12_2(String deviceId, Employee employee, String operation){
		if(employee==null){
			logger.warn("employee is null in send12_2");
			return true;
		}
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject=new JSONObject();
		jsonObject.put(ParameterKeys.TYPE, 12);
		jsonObject.put(ParameterKeys.CODE, 2);
		jsonObject.put(ParameterKeys.EMPLOYEE_ID, employee.getEmployeeId());
		jsonObject.put(ParameterKeys.OPERATION, operation);
		if(!StringUtil.isNullOrEmpty(employee.getEmployeeFold())){
			jsonObject.put(ParameterKeys.EMPLOYEE_FOLD, employee.getEmployeeFold());
		}
		
		if("emp".equals(operation) || "both".equals(operation)){
			jsonObject.put(ParameterKeys.EMPLOYEE_NAME, employee.getEmployeeName());
			jsonObject.put(ParameterKeys.BIRTH, employee.getBirth());
			jsonObject.put("empVersion", employee.getEmpVersion());
			String base64=Base64Utils.GetImageStr(employee.getPhotoPath());
			jsonObject.put(ParameterKeys.PORTRAIT, base64);
		}
		
		if("temp".equals(operation) || "both".equals(operation)){
			AdminService adminService = (AdminService) ServiceDistribution.getContext().getBean("adminService");
			List<Template> templateList = adminService.getTemplateListByEmployeeId(employee.getEmployeeId());
			JSONArray jsonArray = new JSONArray();
			for(Template t : templateList){
				if(t==null){
					continue;
				}
				jsonArray.add(t.getTemplateId());
			}
			jsonObject.put("templateIdList", jsonArray);
			jsonObject.put("tempVersion", employee.getTempVersion());
		}
		
		byte[] result = SocketService.responseByte(jsonObject, "12", "2");
		if (null != channel) {
			executeWrite(result, channel);
			return true;
		}else{
			logger.warn("cannot write to socket channel in 12-2");
			return false;
		}
	}
	
//	private static void sendDel12_2(String deviceId, String employeeId) {
//		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
//		JSONObject jsonObject=new JSONObject();
//		jsonObject.put(ParameterKeys.TYPE, 12);
//		jsonObject.put(ParameterKeys.CODE, 2);
//		jsonObject.put(ParameterKeys.EMPLOYEE_ID, employeeId);
//		jsonObject.put(ParameterKeys.OPERATION, "del");
//		byte[] result = SocketService.responseByte(jsonObject, "12", "2");
//		if (null != channel) {
//			executeWrite(result, channel);
//		}	
//	}
//	
//	private static void sendAdd12_2(String deviceId, String employeeId, String employeeFold, String employeeName,
//			String birth, String photoPath) {
//		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
//		String base64=Base64Utils.GetImageStr(photoPath);
//		JSONObject jsonObject=new JSONObject();
//		jsonObject.put(ParameterKeys.TYPE, 12);
//		jsonObject.put(ParameterKeys.CODE, 2);
//		jsonObject.put(ParameterKeys.OPERATION, "add");
//		jsonObject.put(ParameterKeys.EMPLOYEE_ID, employeeId);
//		jsonObject.put(ParameterKeys.EMPLOYEE_FOLD, employeeFold);
//		jsonObject.put(ParameterKeys.EMPLOYEE_NAME, employeeName);
//		jsonObject.put(ParameterKeys.BIRTH, birth);
//		jsonObject.put(ParameterKeys.PORTRAIT, base64);
//		byte[] result = SocketService.responseByte(jsonObject, "12", "2");
//		if (null != channel) {
//			executeWrite(result, channel);
//		}	
//	}
	
	public static void handleJson12_11(JSONObject jsonObject, ChannelHandlerContext socketChannel) {
		String deviceId=ChannelNameToDeviceMap.getDeviceMap(socketChannel.channel().id());
		String visitorIds = jsonObject.getString("visitorIdArray");
		if(!StringUtil.isNullOrEmpty(visitorIds)){
			visitorIds=visitorIds.replace("[", "(").replace("]", ")");
		}
		VisitorService visitorService = (VisitorService) ServiceDistribution.getContext().getBean("visitorService");
		List<VisitorInfo> list=visitorService.getVisitorByIds(visitorIds,deviceId);
		for(VisitorInfo visitorInfo:list){
			send12_12(deviceId, visitorInfo.getId(), visitorInfo.getVisitorFold(), visitorInfo.getName(), visitorInfo.getBirth(), visitorInfo.getPhotoPath());
		}
		
		if(!StringUtil.isNullOrEmpty(visitorIds)){
			List<String> existVisitor=visitorService.getExistVisitor(visitorIds,deviceId);
			String[] eids=visitorIds.substring(1,visitorIds.length()-1).split(",");
			for(String eid:eids){
				eid=eid.substring(1,eid.length()-1);
				if(!existVisitor.contains(eid)){
					sendDel12_12(deviceId,eid);
				}
			}
		}
	}
	
	public static void sendDel12_12(String deviceId, String visitorId) {
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject=new JSONObject();
		jsonObject.put(ParameterKeys.TYPE, 12);
		jsonObject.put(ParameterKeys.CODE, 12);
		jsonObject.put(ParameterKeys.VISITOR_ID, visitorId);
		jsonObject.put(ParameterKeys.OPERATION, "del");
		byte[] result = SocketService.responseByte(jsonObject, "12", "12");
		if (null != channel) {
			executeWrite(result, channel);
		}	
	}
	
	public static void send12_12(String id, String visitorId, String visitorFold, String name, String birth,
			String photoPath) {
		ChannelHandlerContext channel = DeviceService.getSocketMap(id);
		String base64=Base64Utils.GetImageStr(photoPath);
		JSONObject jsonObject=new JSONObject();
		jsonObject.put(ParameterKeys.TYPE, 12);
		jsonObject.put(ParameterKeys.CODE, 12);
		jsonObject.put(ParameterKeys.OPERATION, "add");
		jsonObject.put(ParameterKeys.VISITOR_ID, visitorId);
		jsonObject.put(ParameterKeys.VISITOR_FOLD, visitorFold);
		jsonObject.put(ParameterKeys.VISITOR_NAME, name);
		jsonObject.put(ParameterKeys.BIRTH, birth);
		jsonObject.put(ParameterKeys.PORTRAIT, base64);
		byte[] result = SocketService.responseByte(jsonObject, "12", "12");
		if (null != channel) {
			executeWrite(result, channel);
		}
	}

	public static void handleJson105_3(String employeeId, String templateId, String deviceId) {
		ChannelHandlerContext channel = DeviceService.getSocketMap(deviceId);
		JSONObject jsonObject=new JSONObject();
		jsonObject.put(ParameterKeys.TYPE, 105);
		jsonObject.put(ParameterKeys.CODE, 3);
		jsonObject.put(ParameterKeys.EMPLOYEE_ID, employeeId);
		jsonObject.put(ParameterKeys.TEMPLATE_ID, templateId);
		byte[] result = SocketService.responseByte(jsonObject, "105", "3");
		if (null != channel) {
			executeWrite(result, channel);
		}else{
			logger.warn("105-3无法写入web socket");
		}
	}
	
	public static void main(String[] args){
		String jsonString = "{\"type\":12, \"code\":01,\"employeeIdArray\":[{\"employeeId\":\"e12\",\"empVersion\":1,\"tempVersion\":1},{\"employeeId\":\"w15\",\"empVersion\":0,\"tempVersion\":2}]}";
		JSONArray jarray = JSONObject.fromObject(jsonString).getJSONArray("employeeIdArray");
		for(int i=0;i<jarray.size();i++){
			JSONObject jsonObject = jarray.getJSONObject(i);
			System.out.println(jsonObject.getString("employeeId"));
			System.out.println(jsonObject.getInt("empVersion"));
			System.out.println(jsonObject.getInt("tempVersion"));
			System.out.println();
		}
	}
}
