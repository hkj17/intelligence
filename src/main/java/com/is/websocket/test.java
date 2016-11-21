package com.is.websocket;

import static com.is.constant.ParameterKeys.EMPLOYEE_FACE;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import com.is.util.PasswordUtil;

import net.sf.json.JSONObject;
import sun.misc.BASE64Encoder;
import sun.misc.BASE64Decoder;


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
	
	   public static String GetImageStr()
	    {//将图片文件转化为字节数组字符串，并对其进行Base64编码处理
	        String imgFile = "D:\\Backup\\23.jpg";//待处理的图片
	        InputStream in = null;
	        byte[] data = null;
	        //读取图片字节数组
	        try 
	        {
	            in = new FileInputStream(imgFile);        
	            data = new byte[in.available()];
	            in.read(data);
	            in.close();
	        } 
	        catch (IOException e) 
	        {
	            e.printStackTrace();
	        }
	      //对字节数组Base64编码
	        BASE64Encoder encoder = new BASE64Encoder();
	        return encoder.encode(data);//返回Base64编码过的字节数组字符串
	    }
	   
	   public static boolean GenerateImage(String imgStr, String imgFilePath) {// 对字节数组字符串进行Base64解码并生成图片  
		   if (imgStr == null) // 图像数据为空  
		   return false;  
		   BASE64Decoder decoder = new BASE64Decoder();  
		   try {  
		   // Base64解码  
		   byte[] bytes = decoder.decodeBuffer(imgStr);  
		   for (int i = 0; i < bytes.length; ++i) {  
		   if (bytes[i] < 0) {// 调整异常数据  
		   bytes[i] += 256;  
		   }  
		   }  
		   // 生成jpeg图片  
		   OutputStream out = new FileOutputStream(imgFilePath);  
		   out.write(bytes);  
		   out.flush();  
		   out.close();  
		   return true;  
		   } catch (Exception e) {  
		   return false;  
		   }  
		   }  
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		String aa="{'222':'222','photo':'123'}";
		JSONObject log=JSONObject.fromObject(aa);
		log.put("photo", "123");
		log.put("222", "222");
		
		JSONObject cc=new JSONObject();
		cc=JSONObject.fromObject(aa);
		
		if(cc.containsKey("photo")){
			cc.remove("photo");
		}
		System.out.println(cc);
		System.out.println(log);
	}
}
