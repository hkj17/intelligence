package com.is.rest;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.is.constant.ResponseCode;
import com.is.model.Visitor;
import com.is.model.VisitorInfo;
import com.is.service.VisitorService;
import com.is.util.BusinessHelper;
import com.is.util.ResponseFactory;
import static com.is.constant.ParameterKeys.START_TIME;
import static com.is.constant.ParameterKeys.END_TIME;

@Component("visitorHandler")
@Path("visitor")
public class VisitorHandler {

	@Autowired
	private VisitorService visitorService;
	
	
	@POST
	@Path("/addVisitorInfo") 
	@Consumes("multipart/form-data")
	public Response addVisitorInfo(
			@FormDataParam("deviceId") String deviceId,
			@FormDataParam("name") String name,
			@FormDataParam("company") String company,
			@FormDataParam("position") String position,
			@FormDataParam("telphone") String telphone,
			@FormDataParam("email") String email,
			@FormDataParam("companyUrl") String companyUrl,
			@FormDataParam("importance") String importance,
			@FormDataParam("birth") String birth,
			@FormDataParam("photo") InputStream uploadedInputStream,
		    @FormDataParam("photo") FormDataContentDisposition fileDetail) throws IOException{
				boolean state=false;
				if(null!=fileDetail){
					String id=visitorService.addVisitorInfo(name, company, position, telphone, email, companyUrl,1,deviceId,importance,birth);
					state=visitorService.addImage(uploadedInputStream, id);
				}
				else {
					state=true;
					visitorService.addVisitorInfo(name, company, position, telphone, email, companyUrl,0,deviceId,importance,birth);
					
				}
				if(state){
					return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
				}else{
					return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
				}
		
	}
	
	@POST
	@Path("/indexVisitor")
	public Response indexVisitor(@Context HttpServletRequest request,MultivaluedMap<String, String> formParams) throws ParseException{
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		List<Visitor> list=visitorService.indexVisitor(requestMap.get(START_TIME), requestMap.get(END_TIME));
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, list);
	}
	
	@POST
	@Path("/getVisitorById")
	public Response getVisitorById(@Context HttpServletRequest request,MultivaluedMap<String, String> formParams) throws ParseException{
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		VisitorInfo info=visitorService.getVisitorById(requestMap.get("visitorId"));
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, info);
		
	}
}
