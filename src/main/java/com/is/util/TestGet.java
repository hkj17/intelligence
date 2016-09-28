package com.is.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

@SuppressWarnings("deprecation")
public class TestGet {

	public static String httpGet(String url) {
		try {
			HttpGet httpGet = new HttpGet(url);
			@SuppressWarnings({ "resource" })
			HttpClient client = new DefaultHttpClient();
			HttpResponse resp = client.execute(httpGet);

			HttpEntity entity = resp.getEntity();
			String respContent = EntityUtils.toString(entity, "GBK").trim();
			httpGet.abort();
			client.getConnectionManager().shutdown();

			return respContent;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String httpPost(String url, Map<String, String> params){
		try {
			HttpPost httpPost = new HttpPost(url);
		      @SuppressWarnings({ "resource" })
			HttpClient client = new DefaultHttpClient();
		      List<NameValuePair> valuePairs = new ArrayList<NameValuePair>(params.size());
		      for(Map.Entry<String, String> entry : params.entrySet()){
		        NameValuePair nameValuePair = new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue()));
		        valuePairs.add(nameValuePair);
		      }
		      UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(valuePairs, "GBK");
		      httpPost.setEntity(formEntity);
		      HttpResponse resp = client.execute(httpPost);
		      
		      HttpEntity entity = resp.getEntity();
		      String respContent = EntityUtils.toString(entity , "GBK").trim();
		      httpPost.abort();
		      client.getConnectionManager().shutdown();

		      return respContent;
		      
		    } catch (Exception e) {
		      e.printStackTrace();
		      return null;
		    }
		  }

}
