package com.is.websocket;

import java.io.UnsupportedEncodingException;

import net.sf.json.JSONObject;

public class test {
	
	

	public static void main(String[] args) throws UnsupportedEncodingException {
		String aString="[\"433839\",\"3334364\",\"44134333733\"]";
		String[] lStrings=aString.substring(1,aString.length()-1).split(",");
		String vString=lStrings[0];
		System.out.println(vString.substring(1,vString.length()-1));
	}
	
	public static String hexString2String(String src) {  
        String temp = "";  
        for (int i = 0; i < src.length() / 2; i++) {  
            temp = temp  
                    + (char) Integer.valueOf(src.substring(i * 2, i * 2 + 2),  
                            16).byteValue();  
        }  
        return temp;  
    }  
}
