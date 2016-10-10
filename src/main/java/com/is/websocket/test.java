package com.is.websocket;

import java.io.File;

public class test {

	public static byte[] hexStr2ByteArray(int length) {  
	    byte[] byteArray=new byte[4];
	    byteArray[0]=(byte) (length & 0xFF);
	    byteArray[1]=(byte) ((length >> 8)& 0xFF);
	    byteArray[2]=(byte) ((length >> 16)& 0xFF);
	    byteArray[3]=(byte) ((length >> 24)& 0xFF);
	    return byteArray;  
	}
	
	public static int bytesToHexString(byte[] src) {
	    int value;    
	    value = (int) ( ((src[0] & 0xFF)<<0)  
	            |((src[1] & 0xFF)<<8)  
	            |((src[2] & 0xFF)<<16)  
	            |(src[3] & 0xFF)<<24);  
	    return value;  
	}
	
	public static void main(String[] args) {
		String path = "D:\\Backup\\567\\236";
		if (!(new File(path).isDirectory())) {
			new File(path).mkdirs();
		}
	}
}
