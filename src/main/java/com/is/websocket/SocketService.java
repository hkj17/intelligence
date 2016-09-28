package com.is.websocket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.socket.SocketChannel;
import net.sf.json.JSONObject;
public class SocketService {

	private static ByteArrayOutputStream databuff = new ByteArrayOutputStream();

	public static void handleSocketMsg(byte[] bytes,SocketChannel socketChannel) throws IOException {
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
	
	public static void excuteWrite(byte[] responseMsg,SocketChannel socketChannel){
		ByteBuf encoded = Unpooled.buffer();
		encoded.writeBytes(responseMsg);
		//System.out.println(encoded.getByte(0));
		socketChannel.write(encoded);
		socketChannel.flush();
	}
	
	public static void handlePayload(String payload,SocketChannel socketChannel) {
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
			responseCode=ServiceDistribution.handleJson3_21(jsonObject);
			anType="3";
			anCode="22";
			byte[] answer=responseByte(responseCode,anType,anCode);
			excuteWrite(answer,socketChannel);
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
		byte[] other = new byte[34];
		System.arraycopy(other, 0, result, 6, 34);
		System.arraycopy(json, 0, result, 40, len);
		return result;
	}
	
	

}
