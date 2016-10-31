package com.is.rest;

import static com.is.constant.ParameterKeys.EMPLOYEE_ID;

import java.io.IOException;
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
import com.is.service.EmployeeService;
import com.is.util.BusinessHelper;
import com.is.util.LoginRequired;
import com.is.util.ResponseFactory;

import net.sf.json.JSONObject;

@Component("functionHandler")
@Path("function")
public class FunctionHandler {
	
	
	@Autowired
	private EmployeeService employeeService;
	
	@POST
	@Path("/sendMsg")
	public Response sendMsg(@Context HttpServletRequest request,MultivaluedMap<String, String> formParams) throws IOException{
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		String result=employeeService.sendMsg(requestMap.get(EMPLOYEE_ID));
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, result);
	}
	
	@POST
	@Path("/getStrangerPhoto")
	@LoginRequired
	public Response getStrangerPhoto(@Context HttpServletRequest request,MultivaluedMap<String, String> formParams) throws IOException{
		String deviceId=(String) request.getSession().getAttribute("deviceSn");
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		JSONObject result=employeeService.getStrangerPhoto(requestMap.get("departmentId"),requestMap.get("name"),requestMap.get("startTime"),requestMap.get("endTime"),requestMap.get("tag"),deviceId);
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, result);
	}

}
