package com.is.websocket;

import org.apache.commons.lang.StringUtils;

public class TransformByte {

	public static byte[] hexStr2ByteArray(String hexString) {  
	    if (StringUtils.isEmpty(hexString))  
	        throw new IllegalArgumentException("this hexString must not be empty");  
	  
	    hexString = hexString.toLowerCase();  
	    final byte[] byteArray = new byte[hexString.length() / 2];  
	    int k = 0;  
	    for (int i = 0; i < byteArray.length; i++) {  
	                    //因为是16进制，最多只会占用4位，转换成字节需要两个16进制的字符，高位在先  
	                    //将hex 转换成byte   "&" 操作为了防止负数的自动扩展  
	                    // hex转换成byte 其实只占用了4位，然后把高位进行右移四位  
	                    // 然后“|”操作  低四位 就能得到 两个 16进制数转换成一个byte.  
	                    //  
	        byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);  
	        byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);  
	        byteArray[i] = (byte) (high << 4 | low);  
	        k += 2;  
	    }  
	    return byteArray;  
	}
	
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}
}
