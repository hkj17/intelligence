package com.is.rest;

import static com.is.constant.ParameterKeys.END_TIME;
import static com.is.constant.ParameterKeys.START_TIME;

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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.is.constant.ResponseCode;
import com.is.model.Visitor;
import com.is.model.VisitorInfo;
import com.is.service.VisitorService;
import com.is.util.BusinessHelper;
import com.is.util.ResponseFactory;

@Component("visitorHandler")
@Path("visitor")
public class VisitorHandler {

	@Autowired
	private VisitorService visitorService;
	
	
	@POST
	@Path("/addVisitorInfo") 
	@Consumes("multipart/form-data")
	public Response addVisitorInfo(@Context HttpServletRequest request,MultivaluedMap<String, String> formParams) {
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		String id=visitorService.addVisitorInfo(requestMap.get("name"), requestMap.get("company"), 
				requestMap.get("position"), requestMap.get("telphone"), requestMap.get("email"), 
				requestMap.get("companyUrl"),requestMap.get("deviceId"),requestMap.get("importance"),requestMap.get("birth"),requestMap.get("path"));
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, id);
		
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
