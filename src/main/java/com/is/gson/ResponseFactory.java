package com.is.gson;



import java.lang.annotation.Annotation;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;



public class ResponseFactory {
	public static Response response(Response.Status status, Object entity,
			JsonPolicyDef.Policy policy) {

		Response.ResponseBuilder responseBuilder = Response.status(status);

		if (policy == null && entity != null) {
			responseBuilder.entity(entity);
		} else if (entity != null) {
			Annotation[] annotations = { new JsonPolicyApply.JsonPolicyApplyLiteral(
					policy) };
			responseBuilder.entity(entity, annotations);
		}

		return responseBuilder.type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	public static byte[] responseBody(Object entity){
		if(entity != null){
			Gson gson = GsonFactory.createGson();
			String response = gson.toJson(entity);
			return response.getBytes();
		}
		return null;
	}
	
//	public static byte[] responseBody(Object entity,JsonPolicyDef.Policy policy){
//		if(policy == null){
//			return responseBody(entity);
//		}else if(entity != null){
//			Annotation[] annotations = { new JsonPolicyApply.JsonPolicyApplyLiteral(
//					policy) };
//			String response = createGson(annotations).toJson(entity);
//			return response.getBytes();
//		}else{
//			return null;
//		}
//		
//	}
	
	public static Response response(Response.Status status, Object entity) {
		Response.ResponseBuilder responseBuilder = Response.status(status);
		if ( entity != null) {
			responseBuilder.entity(entity);
		} 
		return responseBuilder.type(MediaType.APPLICATION_JSON_TYPE).build();
	}


	
	public static Response response(Response.Status status) {
		return response(status, null);
	}
	
	public static Gson createGson(Annotation[] annotations) {
        int count = 0;
        JsonPolicyDef.Policy policy = null;
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(JsonPolicyApply.class)) {
                JsonPolicyApply jsonPolicyApply = (JsonPolicyApply) annotation;
                policy = jsonPolicyApply.value();
                if (++count > 1) {
                    throw new IllegalArgumentException("Two or more active JSON policies");
                }
            }
        }
        return policy != null ? GsonFactory.createGson(policy) : GsonFactory.createGson();
    }
	

}
