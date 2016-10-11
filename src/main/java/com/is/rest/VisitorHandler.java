package com.is.rest;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
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
		List<Visitor> list=visitorService.indexVisitor(requestMap.get("departmentId"), requestMap.get("name"),requestMap.get("startTime"), requestMap.get("endTime"));
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, list);
	}
	
	@POST
	@Path("/getVisitorById")
	public Response getVisitorById(@Context HttpServletRequest request,MultivaluedMap<String, String> formParams) throws ParseException{
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		VisitorInfo info=visitorService.getVisitorById(requestMap.get("visitorId"));
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, info);
		
	}
	
	@POST
	@Path("/addVisitorLeaveTime")
	public Response addVisitorLeaveTime(@Context HttpServletRequest request,MultivaluedMap<String, String> formParams) throws ParseException{
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		boolean state=visitorService.addVisitorLeaveTime(requestMap.get("time"), requestMap.get("id"));
		if(state){
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		}else{
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
		
	}
	
	@POST
	@Path("/updateVisitorInfo")
	public Response updateVisitorInfo(@Context HttpServletRequest request,MultivaluedMap<String, String> formParams) throws ParseException{
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		boolean state=visitorService.updateVisitorInfo(requestMap.get("deviceId"),requestMap.get("visitorId"),requestMap.get("name"), requestMap.get("company"), 
				requestMap.get("position"), requestMap.get("telphone"), requestMap.get("email"), 
				requestMap.get("importance"),requestMap.get("birth"));
		if(state){
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		}else{
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
	}
	
	@POST
	@Path("/deleteVisitorInfo")
	public Response deleteVisitorInfo(@Context HttpServletRequest request,MultivaluedMap<String, String> formParams) throws ParseException{
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		boolean state=visitorService.deleteVisitorInfo(requestMap.get("deviceId"), requestMap.get("visitorId"));
		if(state){
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		}else{
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
	}
	
	@POST
	@Path("/deleteVisitorRecord")
	public Response deleteVisitorRecord(@Context HttpServletRequest request,MultivaluedMap<String, String> formParams) throws ParseException{
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		boolean state=visitorService.deleteVisitorRecord(requestMap.get("id"));
		if(state){
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		}else{
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
	}
	
}
