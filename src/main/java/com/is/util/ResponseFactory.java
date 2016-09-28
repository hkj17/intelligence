package com.is.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ContainerRequest;

import com.google.gson.reflect.TypeToken;
import com.is.constant.ResponseCode;
import com.is.gson.CloudJsonObject;
import com.is.gson.GsonFactory;

public class ResponseFactory {

	public static Response response(Response.Status status, ResponseCode code,Object entity) {
		Response.ResponseBuilder responseBuilder = Response.status(status);
		RestResponse response = new RestResponse(code.ordinal(), entity);
		responseBuilder.entity(response);
		return responseBuilder.type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	public static Response responseIllegal(){
		return response(Response.Status.OK, ResponseCode.REQUEST_ILLEGAL, null);
	}

	public static CloudJsonObject convertRequest(ContainerRequest request){
		try{
			Map<String, Object> map = GsonFactory.createGson().fromJson(requestConvertToJson(request), new TypeToken<Map<String, Object>>() {}.getType());
			CloudJsonObject jsonObject = new CloudJsonObject(map);
			return jsonObject;
		}catch(Exception e){
			return null;
		}
		
	}
	
//	public static CloudJsonObject request2Map(ContainerRequest request){
//		
//	}
	
	private static String requestConvertToJson(ContainerRequest request)
			throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(
			request.getEntityStream()));
		String line = null;
		StringBuilder sb = new StringBuilder();
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		return sb.toString();
	}
	
}
