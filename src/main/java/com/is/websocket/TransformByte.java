package com.is.websocket;

public class TransformByte {

	public static byte[] hexStr2ByteArray(int length) {  
	    byte[] byteArray=new byte[4];
	    byteArray[0]=(byte) ((length >> 24)& 0xFF);
	    byteArray[1]=(byte) ((length >> 16)& 0xFF);
	    byteArray[2]=(byte) ((length >> 8)& 0xFF);
	    byteArray[3]=(byte) ((length >> 0)& 0xFF);
	    return byteArray;  
	}
	
	public static int bytesToHexString(byte[] src) {
	    int value;    
	    value = (int) ( ((src[0] & 0xFF)<<24)  
	            |((src[1] & 0xFF)<<16)  
	            |((src[2] & 0xFF)<<8)  
	            |(src[3] & 0xFF)<<0);  
	    return value;  
	}
}
