package com.is.websocket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.is.map.FutureMap;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import net.sf.json.JSONObject;
public class SocketService {

	private static ByteArrayOutputStream databuff = new ByteArrayOutputStream();

	public static void handleSocketMsg(byte[] bytes,ChannelHandlerContext socketChannel) throws IOException {
		int len=bytes.length;
		String head = new String(bytes, 0, 2);
		if (head.equals("##")) {
			//String aa=new String(bytes, 5, 1,"UTF-8");
			byte[] b = new byte[4];
			b[0]=bytes[2];
			b[1]=bytes[3];
			b[2]=bytes[4];
			b[3]=bytes[5];
			int length = Integer.parseInt(TransformByte.bytesToHexString(b),16);
			// System.out.println(length);
			if (len == length + 6) {
				String body = new String(bytes, 40, length-34);
				handlePayload(body,socketChannel);
			} else if (len > length + 6) {
				String body = new String(bytes, 40, length-34);
				String left = new String(bytes, 6 + length, len - 6 - length);
				byte[] leftbyte = left.getBytes();
				databuff.write(leftbyte);
				handlePayload(body,socketChannel);
			} else {
				databuff.write(bytes);
				excuteWrite( "errorformat".getBytes(),socketChannel);
			}
		} else {
			databuff.write(bytes);
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
		String anType=null;
		String anCode=null;
		if (type.equals("8") && code.equals("1")) {
			responseCode=ServiceDistribution.handleJson8_1(jsonObject);
			anType="8";
			anCode="2";
			byte[] answer=responseByte(responseCode,anType,anCode);
			excuteWrite(answer,socketChannel);
		}
		if (type.equals("1") && code.equals("1")) {
			responseCode=ServiceDistribution.handleJson1_1(jsonObject,socketChannel);
			anType="1";
			anCode="2";
			byte[] answer=responseByte(responseCode,anType,anCode);
			excuteWrite(answer,socketChannel);
		}
		if (type.equals("3") && code.equals("21")) {
			responseCode=ServiceDistribution.handleJson3_21(jsonObject,socketChannel);
			anType="3";
			anCode="22";
			byte[] answer=responseByte(responseCode,anType,anCode);
			excuteWrite(answer,socketChannel);
		}
		if (type.equals("101") && code.equals("2")) {
			 SyncFuture<String> future=FutureMap.getFutureMap(socketChannel.name());
			 future.setResponse("101_2");
		}
		if (type.equals("102") && code.equals("2")) {
			 SyncFuture<String> future=FutureMap.getFutureMap(socketChannel.name());
			 future.setResponse("102_2");
		}
		if (type.equals("103") && code.equals("2")) {
			 SyncFuture<String> future=FutureMap.getFutureMap(socketChannel.name());
			 future.setResponse("103_2");
		}

	}
	
	public static byte[] responseByte(JSONObject jsonObject,String type,String code){
		byte[] json = jsonObject.toString().getBytes();
		int len = json.length;
		byte[] lenb = TransformByte.hexStr2ByteArray(String.format("%8s", Integer.toHexString(len + 32)).replace(' ', '0'));
		System.out.println(new String(lenb));
		byte[] xing = "##".getBytes();
		byte[] result = new byte[40 + len];
		System.arraycopy(xing, 0, result, 0, 2);
		System.arraycopy(lenb, 0, result, 2, 4);
		
		//暂时空着
		byte[] other = new byte[32];
		System.arraycopy(other, 0, result, 6, 32);
		
		//type和code
		byte[] typeByte=TransformByte.hexStr2ByteArray(String.format("%2s", Integer.toHexString(Integer.parseInt(type))).replace(' ', '0'));
		System.arraycopy(typeByte, 0, result, 38, 1);
		byte[] codeByte=TransformByte.hexStr2ByteArray(String.format("%2s", Integer.toHexString(Integer.parseInt(code))).replace(' ', '0'));
		System.arraycopy(codeByte, 0, result, 39, 1);
		
		//json报文
		System.arraycopy(json, 0, result, 40, len);
		return result;
	}
	
	

}
