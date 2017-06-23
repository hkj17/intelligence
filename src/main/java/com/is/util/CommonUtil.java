package com.is.util;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.UUID;


/** 
 * @author zhuolin(zl@nbicc.com) 
 * @date 2014年6月16日
 * 工具类
 */
public class CommonUtil {

	private static final String CHARSET = "UTF-8";
	
	public static boolean isEmpty(Collection collection) {
		return (collection == null || collection.isEmpty());
	}

	public static int hexString2int(String str){
		return Integer.parseInt(str, 16);
	}

	public static long hexString2long(String str){
		BigInteger bigInteger = new BigInteger(str, 16);
		return bigInteger.longValue();
	}
	
	public static StringBuilder zeroize(String originalStr,int length){
		int zeroizeLength = length - originalStr.length();
		StringBuilder sBuilder = new StringBuilder();
		for(int i=0;i<zeroizeLength;i++){
			sBuilder.append("0");
		}
		sBuilder.append(originalStr);
		return sBuilder;
	}
	
	public static String cutFirstZeros(String s){
		String newStr = s.replaceFirst("^0*", "");
		return newStr;
	}
	
	public static String dateFormat(Date date){
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		return dateFormat.format(date);
	}
	
	public static String dateFormat(Date date,String dateFormatString){
		DateFormat dateFormat = new SimpleDateFormat(dateFormatString);
		return dateFormat.format(date);
	}
	
	public static Date string2Date(String dateString){
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		try {
			return dateFormat.parse(dateString);
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static Date string2Date(String dateString,String dateFormatString){
		DateFormat dateFormat = new SimpleDateFormat(dateFormatString);
		try {
			return dateFormat.parse(dateString);
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static String stringToAscii(String value) {  
	    StringBuffer sbu = new StringBuffer();  
		  char[] chars = value.toCharArray();   
		   for (int i = 0; i < chars.length; i++) {  
		            sbu.append(Integer.toHexString((int)chars[i]).toUpperCase());  
		    }  
		   return sbu.toString();  
		} 
	
	private static byte uniteBytes(String src0, String src1) {  
	    byte b0 = Byte.decode("0x" + src0).byteValue();  
	    b0 = (byte) (b0 << 4);  
	    byte b1 = Byte.decode("0x" + src1).byteValue();  
	    byte ret = (byte) (b0 | b1);  
	    return ret;  
	}  
	  
	
	public static byte[] hexStr2Bytes(String src) {
		if(src == null){
			return new byte[]{};
		}
	    int m = 0, n = 0;  
	    int l = src.length() / 2;  
	    byte[] ret = new byte[l];  
	    for (int i = 0; i < l; i++) {  
	        m = i * 2 + 1;  
	        n = m + 1;  
	        ret[i] = uniteBytes(src.substring(i * 2, m), src.substring(m, n));  
	    }  
	    return ret;  
	}  
	 
	public static String bytesToHexString(byte[] data, String separator) {
        if (data == null || data.length == 0) {
            return "";
        }
        if (separator == null) {
            separator = "";
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            String hex = Integer.toHexString(b & 0xFF).toUpperCase(Locale.getDefault());
            sb.append(hex.length() < 2 ? "0" + hex : hex);
            sb.append(separator);
        }
        sb.setLength(sb.length() - separator.length());
        return sb.toString();
    }

	public static String bytesToHexString(byte[] data) {
        if (data == null || data.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            String hex = Integer.toHexString(b & 0xFF).toUpperCase(Locale.getDefault());
            sb.append(hex.length() < 2 ? "0" + hex : hex);
        }
        sb.setLength(sb.length());
        return sb.toString();
    }
	
	public static String bytesToString(byte[] data){
		try {
			return new String(data,CHARSET);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String shortToHexString(short data){
		return Integer.toHexString(data&0xFFFF).toUpperCase();
	}
	
	   public static byte[] shortToByte(short number) { 
	        int temp = number; 
	        byte[] b = new byte[2]; 
	        for (int i = b.length-1; i >=0; i--) { 
	            b[i] = new Integer(temp & 0xff).byteValue();
	            temp = temp >> 8; // 向右移8位 
	        } 
	        return b; 
	    } 
	 
	    /** 
	     * 注释：字节数组到short的转换！ 
	     * 
	     * @param b 
	     * @return 
	     */ 
	    public static int byteToInt(byte[] b) { 
	        int s = 0; 
	        short s0 = (short) (b[0] & 0xff);// 最高位
	        if(b.length > 1){
	        	short s1 = (short) (b[1] & 0xff); 
		        s0 <<= 8; 
		        s = ((short) (s0 | s1)) & 0xffff; 
	        }else{
	        	s = s0;
	        }
	        return s; 
	    }
	    
	    public static long byteToLong(byte[] b){
	    	 return ((((long) b[0] & 0xff) << 56)  
	                 | (((long) b[1] & 0xff) << 48)  
	                 | (((long) b[2] & 0xff) << 40)  
	                 | (((long) b[3] & 0xff) << 32)  
	                 | (((long) b[4] & 0xff) << 24)  
	                 | (((long) b[5] & 0xff) << 16)  
	                 | (((long) b[6] & 0xff) << 8) | (((long) b[7] & 0xff) << 0));  
	    }
	    
	    public static long byte6ToLong(byte[] b){
	    	byte[] bytes = new byte[8];
	    	System.arraycopy(b, 0, bytes, 2, 6);
	    	return byteToLong(bytes);
	    }
	    
	    public static byte[] longToBytes(long x) {
	    	byte[] bytes = new byte[8];
	    	bytes[0] = (byte) (x >> 56);  
	    	bytes[1] = (byte) (x >> 48);  
	    	bytes[2] = (byte) (x >> 40);  
	    	bytes[3] = (byte) (x >> 32);  
	    	bytes[4] = (byte) (x >> 24);  
	    	bytes[5] = (byte) (x >> 16);  
	    	bytes[6] = (byte) (x >> 8);  
	    	bytes[7] = (byte) (x >> 0);  
	    	return bytes;
	    }  
	    
	    public static byte[] longTo6Bytes(long x){
	    	byte[] bytes = new byte[6];
	    	bytes[0] = (byte) (x >> 40);  
	    	bytes[1] = (byte) (x >> 32);  
	    	bytes[2] = (byte) (x >> 24);  
	    	bytes[3] = (byte) (x >> 16);  
	    	bytes[4] = (byte) (x >> 8);  
	    	bytes[5] = (byte) (x >> 0);  
	    	return bytes;
	    }
	    
	    public static byte[] intToBytes(int x) {  
	    	byte[] bytes = new byte[4];
	    	bytes[0] = (byte) (x >> 24);  
	    	bytes[1] = (byte) (x >> 16);  
	    	bytes[2] = (byte) (x >> 8);  
	    	bytes[3] = (byte) (x >> 0);  
	    	return bytes;
	    }  
	    
	    public static byte[] intToByte(int x){
	    	byte[] bytes = new byte[1];
	    	bytes[0] = (byte)(x);
	    	return bytes;
	    }
	    
	    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2){  
	        byte[] byte_3 = new byte[byte_1.length+byte_2.length];  
	        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);  
	        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);  
	        return byte_3;  
	    }  
	    
	    public static byte[] bytesMerger(byte[]...bytes){
	    	int size = 0;
	    	for(byte[] byteUnit:bytes){
	    		size+=byteUnit.length;
	    	}
	    	byte[] totalBytes = new byte[size];
	    	int i = 0;
	    	for(byte[] byteUnit:bytes){
	    		System.arraycopy(byteUnit, 0, totalBytes, i, byteUnit.length);
	    		i+=byteUnit.length;
	    	}
	    	return totalBytes;
	    }
	    
	    public static byte[] bytesMerger(List<byte[]> byteList){
	    	int size = 0;
	    	for(byte[] byteUnit:byteList){
	    		size+=byteUnit.length;
	    	}
	    	byte[] totalBytes = new byte[size];
	    	int i = 0;
	    	for(byte[] byteUnit:byteList){
	    		System.arraycopy(byteUnit, 0, totalBytes, i, byteUnit.length);
	    		i+=byteUnit.length;
	    	}
	    	return totalBytes;
	    }
	    
	    public static byte[] splitBytes(byte[] bytes,int from,int length){
	    	byte[] des = new byte[length];
	    	System.arraycopy(bytes, from, des, 0, length);
	    	return des;
	    }
	    
	    public static void buildBytes(byte[] bytes,byte[] desBytes, int from,int desFrom,int length){
	    	System.arraycopy(bytes, from, desBytes, desFrom, length);
	    }
	    
	    public static String genRandomNum(int pwd_len){
	        //35是因为数组是从0开始的，26个字母+10个数字
	        final int maxNum = 36;
	        int i; //生成的随机数
	        int count = 0; //生成的密码的长度
	        char[] str = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' ,'a','b','c','d','e','f'};
	        StringBuffer pwd = new StringBuffer("");
	        Random r = new Random();
	        while(count < pwd_len){
	         //生成随机数，取绝对值，防止生成负数，
	         i = Math.abs(r.nextInt(maxNum)); //生成的数最大为36-1
	         if (i >= 0 && i < str.length) {
	          pwd.append(str[i]);
	          count ++;
	         }
	        }
	        return pwd.toString();
	     }
	    
	    public static boolean checkMapKeys(Map<String, Object>map,String...strs){
	    	boolean flag = true;
	    	for(String str:strs){
	    		if(!map.containsKey(str)){
	    			flag = false;
	    			break;
	    		}
	    	}
	    	return flag;
	    }
	    
	    public static int hexCharToInt(char c){
	    	int i = 0;
	    	if(c >= '0' && c <= '9'){
	    		i = c - '0';
	    	}else if(c >= 'a' && c <= 'f'){
	    		i = c - 'a' + 10;
	    	}else if(c >= 'A' && c <= 'F'){
	    		i = c - 'A' + 10;
	    	}
	    	return i;
	    }
	    
	    public static String long2hexString(long l){
	    	return Long.toHexString(l);
	    }
	    
	    public static String long2hexString16(long l){
	    	return zeroize(long2hexString(l), 16).toString();
	    }
	    
	    public static boolean checkIllegal(Object...objects){
	    	boolean result = false;
	    	for(Object obj:objects){
	    		if(obj == null){
	    			result = true;
	    			break;
	    		}
	    	}
	    	return result;
	    }
	    
	    public static double bytesToDouble(byte[] b){
	    	 long l = ((long) b[0] << 56) & 0xFF00000000000000L;  
	         // 如果不强制转换为long，那么默认会当作int，导致最高32位丢失  
	         l |= ((long) b[1] << 48) & 0xFF000000000000L;  
	         l |= ((long) b[2] << 40) & 0xFF0000000000L;  
	         l |= ((long) b[3] << 32) & 0xFF00000000L;  
	         l |= ((long) b[4] << 24) & 0xFF000000L;  
	         l |= ((long) b[5] << 16) & 0xFF0000L;  
	         l |= ((long) b[6] << 8) & 0xFF00L;  
	         l |= (long) b[7] & 0xFFL;  
	         return Double.longBitsToDouble(l);  
	    }
	    
	    public static float bytesToFloat(byte[] b){
	    	int i = (b[0] << 24) & 0xFF000000;  
	        i |= (b[1] << 16) & 0xFF0000;  
	        i |= (b[2] << 8) & 0xFF00;  
	        i |= b[3] & 0xFF; 
	        return Float.intBitsToFloat(i);
	    }
	    
	    public static byte[] floatToBytes(float data)
	    {
	        int intBits = Float.floatToIntBits(data);
	        return intToBytes(intBits);
	    }

	    public static byte[] doubleToBytes(double data)
	    {
	        long intBits = Double.doubleToLongBits(data);
	        return longToBytes(intBits);
	    }
	    
	    public static String buildStringWithLabel(String originalStr,String label,String...replaceStrs){
	    	for(String str:replaceStrs){
	    		originalStr = originalStr.replaceFirst(label, str);
	    	}
	    	return originalStr;
	    }
	    
	    public static String generateRandomUUID(){
	    	return UUID.randomUUID().toString().trim().replaceAll("-", "");
	    }
}
