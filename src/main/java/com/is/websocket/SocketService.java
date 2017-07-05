package com.is.websocket;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.is.constant.ParameterKeys;
import com.is.map.EmployeeFoldMap;
import com.is.map.FutureMap;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import net.sf.json.JSONObject;
public class SocketService {
	private static Logger logger = Logger.getLogger(SocketService.class);

	public static void handleSocketMsg(byte[] bytes,ChannelHandlerContext socketChannel) throws IOException {
		int len=bytes.length;
		String head = new String(bytes, 0, 2);
		if (head.equals("##")) {
			String body = new String(bytes, 36, len-36);
			handlePayload(body,socketChannel);
		} else {
			/*databuff.write(bytes);*/
			excuteWrite( "errorformat".getBytes(),socketChannel);
		}
		
	}
	
	public static void excuteWrite(byte[] responseMsg,ChannelHandlerContext socketChannel){
		ByteBuf encoded = Unpooled.buffer();
		encoded.writeBytes(responseMsg);
		socketChannel.write(encoded);
		socketChannel.flush();
	}
	
	public static void handlePayload(String payload,ChannelHandlerContext socketChannel) {
		JSONObject jsonObject = JSONObject.fromObject(payload);
		JSONObject responseCode = new JSONObject();
		String type = jsonObject.getString(ParameterKeys.TYPE);
		String code = jsonObject.getString(ParameterKeys.CODE);
		JSONObject log=new JSONObject();
		log=JSONObject.fromObject(payload);
		if(log.containsKey(ParameterKeys.PHOTO)){
			log.remove(ParameterKeys.PHOTO);
		}
		if(log.containsKey(ParameterKeys.IMAGE_STAT)){
			log.remove(ParameterKeys.IMAGE_STAT);
		}
		if(log.containsKey(ParameterKeys.PORTRAIT)){
			log.remove(ParameterKeys.PORTRAIT);
		}
		logger.info(log);
		String anType=null;
		String anCode=null;
	
		if (type.equals("1") && code.equals("1")) {
			responseCode=ServiceDistribution.handleJson1_1(jsonObject,socketChannel);
			anType="1";
			anCode="2";
			byte[] answer=responseByte(responseCode,anType,anCode);
			excuteWrite(answer,socketChannel);
		}
		else if (type.equals("3") && code.equals("1")) {
			responseCode=ServiceDistribution.handleJson3_1(jsonObject,socketChannel);
			anType="3";
			anCode="2";
			byte[] answer=responseByte(responseCode,anType,anCode);
			excuteWrite(answer,socketChannel);
		}
		else if (type.equals("3") && code.equals("11")) {
			responseCode=ServiceDistribution.handleJson3_11(jsonObject,socketChannel);
			anType="3";
			anCode="12";
			byte[] answer=responseByte(responseCode,anType,anCode);
			excuteWrite(answer,socketChannel);
		}
		else if (type.equals("3") && code.equals("21")) {
			responseCode=ServiceDistribution.handleJson3_21(jsonObject,socketChannel);
			anType="3";
			anCode="22";
			byte[] answer=responseByte(responseCode,anType,anCode);
			excuteWrite(answer,socketChannel);
		}
		else if (type.equals("3") && code.equals("23")) {
			responseCode=ServiceDistribution.handleJson3_23(jsonObject,socketChannel);
			anType="3";
			anCode="24";
			byte[] answer=responseByte(responseCode,anType,anCode);
			excuteWrite(answer,socketChannel);
		}
		else if (type.equals("4") && code.equals("1")) {
			responseCode=ServiceDistribution.handleJson4_1(jsonObject,socketChannel);
			anType="4";
			anCode="2";
			byte[] answer=responseByte(responseCode,anType,anCode);
			excuteWrite(answer,socketChannel);
		}
		else if (type.equals("5") && code.equals("1")) {
			responseCode=ServiceDistribution.handleJson5_1(jsonObject,socketChannel);
			anType="5";
			anCode="2";
			byte[] answer=responseByte(responseCode,anType,anCode);
			excuteWrite(answer,socketChannel);
		}
		else if (type.equals("7") && code.equals("1")) {
			ServiceDistribution.handleJson7_1(jsonObject,socketChannel);
		}
		else if (type.equals("7") && code.equals("11")) {
			ServiceDistribution.handleJson7_11(jsonObject,socketChannel);
		}
		else if (type.equals("8") && code.equals("1")) {
			String isEnd=jsonObject.getString("isEnd");
			if("0".equals(isEnd)){
				responseCode=ServiceDistribution.handleJson8_1(jsonObject,socketChannel);
				anType="8";
				anCode="2";
				byte[] answer=responseByte(responseCode,anType,anCode);
				excuteWrite(answer,socketChannel);
			}
			else{
				responseCode=ServiceDistribution.handleJson8_1_end(jsonObject,socketChannel);
				anType="8";
				anCode="2";
				byte[] answer=responseByte(responseCode,anType,anCode);
				excuteWrite(answer,socketChannel);
				ServiceDistribution.SyncEmployee(jsonObject);
			}
		}
		else if (type.equals("8") && code.equals("11")) {
			responseCode=ServiceDistribution.handleJson8_11(jsonObject,socketChannel);
			anType="8";
			anCode="12";
			byte[] answer=responseByte(responseCode,anType,anCode);
			excuteWrite(answer,socketChannel);
		}
		else if (type.equals("9") && code.equals("1")) {
			responseCode=ServiceDistribution.handleJson9_1(jsonObject,socketChannel);
			anType="9";
			anCode="2";
			byte[] answer=responseByte(responseCode,anType,anCode);
			excuteWrite(answer,socketChannel);
		}
		else if (type.equals("9") && code.equals("11")) {
			responseCode=ServiceDistribution.handleJson9_11(jsonObject,socketChannel);
			anType="9";
			anCode="12";
			byte[] answer=responseByte(responseCode,anType,anCode);
			excuteWrite(answer,socketChannel);
		}
		else if (type.equals("10") && code.equals("1")) {
			responseCode=ServiceDistribution.handleJson10_1(jsonObject,socketChannel);
			anType="10";
			anCode="2";
			byte[] answer=responseByte(responseCode,anType,anCode);
			excuteWrite(answer,socketChannel);
		}
		else if (type.equals("10") && code.equals("11")) {
			responseCode=ServiceDistribution.handleJson10_11(jsonObject,socketChannel);
			anType="10";
			anCode="12";
			byte[] answer=responseByte(responseCode,anType,anCode);
			excuteWrite(answer,socketChannel);
		}
		else if (type.equals("12") && code.equals("1")) {
			ServiceDistribution.handleJson12_1(jsonObject,socketChannel);
		}
		else if (type.equals("12") && code.equals("11")) {
			ServiceDistribution.handleJson12_11(jsonObject,socketChannel);
		}
		else if (type.equals("100") && code.equals("100")) {
			responseCode=ServiceDistribution.handleJson100_100();
			anType="100";
			anCode="100";
			byte[] answer=responseByte(responseCode,anType,anCode);
			excuteWrite(answer,socketChannel);
		}
		else if (type.equals("101") && code.equals("2")) {
			 SyncFuture<String> future=FutureMap.getFutureMap(socketChannel.channel().id().asLongText()+"101_2");
			 if(future!=null){
				 future.setResponse("101_2");
			 }
		}
		else if (type.equals("102") && code.equals("2")) {
			 SyncFuture<String> future=FutureMap.getFutureMap(socketChannel.channel().id().asLongText()+"102_2");
			 if(future!=null){
				 future.setResponse("102_2");
			 }
		}
		else if (type.equals("103") && code.equals("2")) {
			 SyncFuture<String> future=FutureMap.getFutureMap(socketChannel.channel().id().asLongText()+"103_2");
			 if(future!=null){
				 String employeeId=jsonObject.optString(ParameterKeys.EMPLOYEE_ID);
				 String templateId=jsonObject.optString(ParameterKeys.TEMPLATE_ID);
				 String strangerId=jsonObject.optString(ParameterKeys.STRANGER_ID);
				 String employeeName=jsonObject.optString(ParameterKeys.EMPLOYEE_NAME);
				 String employeeFold = jsonObject.optString(ParameterKeys.EMPLOYEE_FOLD);
				 EmployeeFoldMap.setData(employeeId, employeeFold);	 
				 future.setResponse("103_2");
				 ServiceDistribution.handleJson109_1(employeeId, templateId, strangerId, employeeName, socketChannel);
			 }
		}
		else if (type.equals("103") && code.equals("12")) {
			 SyncFuture<String> future=FutureMap.getFutureMap(socketChannel.channel().id().asLongText()+"103_12");
			 if(future!=null){
				 future.setResponse("103_12");
			 }
			 String visitorId=jsonObject.getString(ParameterKeys.VISITOR_ID);
			 ServiceDistribution.handleJson109_11(visitorId, socketChannel);
			 ServiceDistribution.handleJson103_12(jsonObject,socketChannel);
		}
		else if (type.equals("106") && code.equals("2")) {
			 SyncFuture<String> future=FutureMap.getFutureMap(socketChannel.channel().id().asLongText()+"106_2");
			 if(future!=null){
				 future.setResponse("106_2");
			 }
		}
		else if (type.equals("106") && code.equals("12")) {
			 SyncFuture<String> future=FutureMap.getFutureMap(socketChannel.channel().id().asLongText()+"106_12");
			 if(future!=null){
				 future.setResponse("106_12");
			 }
		}
		else if (type.equals("106") && code.equals("22")) {
			 SyncFuture<String> future=FutureMap.getFutureMap(socketChannel.channel().id().asLongText()+"106_22");
			 if(future!=null){
				 future.setResponse("106_22");
			 }
		}
		else if (type.equals("110") && code.equals("2")) {
			 SyncFuture<String> future=FutureMap.getFutureMap(socketChannel.channel().id().asLongText()+"110_2");
			 if(future!=null){
				 future.setResponse("110_2");
			 }
			 ServiceDistribution.handleJson110_2(jsonObject, socketChannel);
		}
		else if (type.equals("104") && code.equals("2")) {
			 SyncFuture<String> future=FutureMap.getFutureMap(socketChannel.channel().id().asLongText()+"104_2");
			 if(future!=null){
				 future.setResponse("104_2");
			 }
		}
		else if (type.equals("104") && code.equals("12")) {
			 SyncFuture<String> future=FutureMap.getFutureMap(socketChannel.channel().id().asLongText()+"104_12");
			 if(future!=null){
				 future.setResponse("104_12");
			 }
		}
		else if (type.equals("105") && code.equals("2")) {
			 SyncFuture<String> future=FutureMap.getFutureMap(socketChannel.channel().id().asLongText()+"105_2");
			 if(future!=null){
				 future.setResponse("105_2");
			 }
		}
		else if (type.equals("105") && code.equals("4")){
			SyncFuture<String> future=FutureMap.getFutureMap(socketChannel.channel().id().asLongText()+"105_4");
			 if(future!=null){
				 future.setResponse("105_4");
			 }
		}
		else if (type.equals("105") && code.equals("12")) {
			 SyncFuture<String> future=FutureMap.getFutureMap(socketChannel.channel().id().asLongText()+"105_12");
			 if(future!=null){
				 future.setResponse("105_12");
			 }
		}
		else if (type.equals("105") && code.equals("22")) {
			 SyncFuture<String> future=FutureMap.getFutureMap(socketChannel.channel().id().asLongText()+"105_22");
			 if(future!=null){
				 future.setResponse("105_22");
			 }
		}
		else if (type.equals("109") && code.equals("2")) {
			SyncFuture<String> future=FutureMap.getFutureMap(socketChannel.channel().id().asLongText()+"109_2");
			 if(future!=null){
				 future.setResponse("109_2");
			 }
			ServiceDistribution.handleJson109_2(jsonObject,socketChannel);
		}
		else if (type.equals("109") && code.equals("12")) {
			ServiceDistribution.handleJson109_12(jsonObject,socketChannel);
		}
		else if (type.equals("114") && code.equals("2")) {
			 SyncFuture<String> future=FutureMap.getFutureMap(socketChannel.channel().id().asLongText()+"114_2");
			 if(future!=null){
				 future.setResponse("114_2");
			 }
		}
		else if (type.equals("111") && code.equals("12")) {
			 SyncFuture<String> future=FutureMap.getFutureMap(socketChannel.channel().id().asLongText()+"111_12");
			 if(future!=null){
				 future.setResponse("111_12");
			 }
		}
		else if (type.equals("111") && code.equals("22")) {
			 SyncFuture<String> future=FutureMap.getFutureMap(socketChannel.channel().id().asLongText()+"111_22");
			 if(future!=null){
				 future.setResponse("111_22");
			 }
		}
		else if (type.equals("112") && code.equals("2")) {
			 SyncFuture<String> future=FutureMap.getFutureMap(socketChannel.channel().id().asLongText()+"112_2");
			 if(future!=null){
				 String voice=jsonObject.getString("volume");
				 future.setResponse(voice);
			 }
		}
		else if (type.equals("119") && code.equals("2")) {
			 SyncFuture<String> future=FutureMap.getFutureMap(socketChannel.channel().id().asLongText()+"119_2");
			 if(future!=null){
				 String focus=jsonObject.getString("focus");
				 future.setResponse(focus);
			 }
		}
		else if (type.equals("112") && code.equals("12")) {
			 SyncFuture<String> future=FutureMap.getFutureMap(socketChannel.channel().id().asLongText()+"112_12");
			 if(future!=null){
				 future.setResponse("112_12");
			 }
		}
		else if (type.equals("119") && code.equals("12")) {
			 SyncFuture<String> future=FutureMap.getFutureMap(socketChannel.channel().id().asLongText()+"119_12");
			 if(future!=null){
				 future.setResponse("119_12");
			 }
		}
		else if (type.equals("113") && code.equals("2")) {
			 SyncFuture<String> future=FutureMap.getFutureMap(socketChannel.channel().id().asLongText()+"113_2");
			 if(future!=null){
				 future.setResponse("113_2");
			 }
		}
		else {
			excuteWrite("error type or code!".getBytes(),socketChannel);
		}

	}
	
	public static byte[] responseByte(JSONObject jsonObject,String type,String code){
		byte[] json = jsonObject.toString().getBytes();
		int len = json.length;
		byte[] xing = "##".getBytes();
		byte[] result = new byte[36 + len];
		System.arraycopy(xing, 0, result, 0, 2);
		
		//暂时空着
		byte[] other = new byte[32];
		System.arraycopy(other, 0, result, 2, 32);
		
		//type和code
		byte[] typeByte=TransformByte.hexStr2ByteArray(Integer.parseInt(type));
		System.arraycopy(typeByte, 0, result, 34, 1);
		byte[] codeByte=TransformByte.hexStr2ByteArray(Integer.parseInt(code));
		System.arraycopy(codeByte, 0, result, 35, 1);
		
		//json报文
		System.arraycopy(json, 0, result, 36, len);
		
		//打印的日志去掉base64字符串
		if(jsonObject.containsKey(ParameterKeys.PHOTO)){
			jsonObject.remove(ParameterKeys.PHOTO);
		}
		if(jsonObject.containsKey(ParameterKeys.PORTRAIT)){
			jsonObject.remove(ParameterKeys.PORTRAIT);
		}
		if(jsonObject.containsKey(ParameterKeys.IMAGE_STAT)){
			jsonObject.remove(ParameterKeys.IMAGE_STAT);
		}
		if(jsonObject.containsKey("adPic")){
			jsonObject.remove("adPic");
		}
		logger.info(jsonObject);
		return result;
	}
	
	

}
