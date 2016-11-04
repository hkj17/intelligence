package com.is.websocket;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.is.map.FutureMap;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import net.sf.json.JSONObject;
public class SocketService {
	private static Logger logger = Logger.getLogger(SocketService.class);
	//private static ByteArrayOutputStream databuff = new ByteArrayOutputStream();

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
		//System.out.println(encoded.getByte(0));
		socketChannel.write(encoded);
		socketChannel.flush();
	}
	
	public static void handlePayload(String payload,ChannelHandlerContext socketChannel) {
		JSONObject jsonObject = JSONObject.fromObject(payload);
		JSONObject responseCode = new JSONObject();
		String type = jsonObject.getString("type");
		String code = jsonObject.getString("code");
		
		logger.info("record:"+type+","+code);
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
		else if (type.equals("8") && code.equals("1")) {
			responseCode=ServiceDistribution.handleJson8_1(jsonObject,socketChannel);
			anType="8";
			anCode="2";
			byte[] answer=responseByte(responseCode,anType,anCode);
			excuteWrite(answer,socketChannel);
		}
		else if (type.equals("100") && code.equals("100")) {
			responseCode=ServiceDistribution.handleJson100_100();
			anType="100";
			anCode="100";
			byte[] answer=responseByte(responseCode,anType,anCode);
			excuteWrite(answer,socketChannel);
		}
		else if (type.equals("101") && code.equals("2")) {
			 SyncFuture<String> future=FutureMap.getFutureMap(socketChannel.name());
			 future.setResponse("101_2");
			 System.out.println("accept!");
		}
		else if (type.equals("102") && code.equals("2")) {
			 SyncFuture<String> future=FutureMap.getFutureMap(socketChannel.name());
			 future.setResponse("102_2");
		}
		else if (type.equals("103") && code.equals("2")) {
			 SyncFuture<String> future=FutureMap.getFutureMap(socketChannel.name());
			 future.setResponse("103_2");
			 String employeeId=jsonObject.getString("employeeId");
			 ServiceDistribution.handleJson109_1(employeeId, socketChannel);
		}
		else if (type.equals("103") && code.equals("12")) {
			 SyncFuture<String> future=FutureMap.getFutureMap(socketChannel.name());
			 future.setResponse("103_12");
			 String visitorId=jsonObject.getString("visitorId");
			 ServiceDistribution.handleJson109_11(visitorId, socketChannel);
		}
		else if (type.equals("106") && code.equals("2")) {
			 SyncFuture<String> future=FutureMap.getFutureMap(socketChannel.name());
			 future.setResponse("106_2");
		}
		else if (type.equals("110") && code.equals("2")) {
			 SyncFuture<String> future=FutureMap.getFutureMap(socketChannel.name());
			 future.setResponse("110_2");
			 ServiceDistribution.handleJson110_2(jsonObject, socketChannel);
		}
		else if (type.equals("104") && code.equals("2")) {
			 SyncFuture<String> future=FutureMap.getFutureMap(socketChannel.name());
			 future.setResponse("104_2");
		}
		else if (type.equals("104") && code.equals("12")) {
			 SyncFuture<String> future=FutureMap.getFutureMap(socketChannel.name());
			 future.setResponse("104_12");
		}
		else if (type.equals("105") && code.equals("2")) {
			 SyncFuture<String> future=FutureMap.getFutureMap(socketChannel.name());
			 future.setResponse("105_2");
		}
		else if (type.equals("105") && code.equals("12")) {
			 SyncFuture<String> future=FutureMap.getFutureMap(socketChannel.name());
			 future.setResponse("105_12");
		}
		else if (type.equals("109") && code.equals("2")) {
			ServiceDistribution.handleJson109_2(jsonObject,socketChannel);
		}
		else if (type.equals("109") && code.equals("12")) {
			ServiceDistribution.handleJson109_12(jsonObject,socketChannel);
		}
		else if (type.equals("114") && code.equals("2")) {
			 SyncFuture<String> future=FutureMap.getFutureMap(socketChannel.name());
			 future.setResponse("114_2");
		}
		else {
			excuteWrite("error type or code!".getBytes(),socketChannel);
		}

	}
	
	public static byte[] responseByte(JSONObject jsonObject,String type,String code){
		byte[] json = jsonObject.toString().getBytes();
		int len = json.length;
		/*byte[] lenb = TransformByte.hexStr2ByteArray(len+34);
		System.out.println(new String(lenb));*/
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
		return result;
	}
	
	

}
