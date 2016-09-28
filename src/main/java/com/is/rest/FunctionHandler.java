package com.is.rest;

import java.io.IOException;
import java.net.URLEncoder;
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
import com.is.model.Employee;
import com.is.service.AdminService;
import com.is.service.EmployeeService;
import com.is.util.BusinessHelper;
import com.is.util.JavaSms;
import com.is.util.ResponseFactory;
import static com.is.constant.ParameterKeys.EMPLOYEE_ID;

@Component("functionHandler")
@Path("function")
public class FunctionHandler {
	
	private static String ENCODING = "UTF-8";
	
	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	private AdminService adminService;
	
	
	@POST
	@Path("/sendMsg")
	public Response sendMsg(@Context HttpServletRequest request,MultivaluedMap<String, String> formParams) throws IOException{
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		String id=requestMap.get(EMPLOYEE_ID);
		Employee employee=adminService.getEmployeeById(id);
		if(null==employee.getEmployeeName()){
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, "no user");
		}
		String tpl_value=URLEncoder.encode("#name#",ENCODING) +"="
	            + URLEncoder.encode(employee.getEmployeeName(), ENCODING);
		String result=JavaSms.tplSendSms(tpl_value, employee.getTelphone());
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, result);
	}

}
