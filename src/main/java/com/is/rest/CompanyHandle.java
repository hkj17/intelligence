package com.is.rest;

import static com.is.constant.ParameterKeys.COMPANY_NAME;
import static com.is.constant.ParameterKeys.DEVICE_ID;
import static com.is.constant.ParameterKeys.ADDRESS;
import static com.is.constant.ParameterKeys.START_TIME;
import static com.is.constant.ParameterKeys.END_TIME;
import static com.is.constant.ParameterKeys.USER_NAME;
import static com.is.constant.ParameterKeys.USER_PSW;
import static com.is.constant.ParameterKeys.NAME;
import static com.is.constant.ParameterKeys.CONTACT;
import static com.is.constant.ParameterKeys.COMPANY_ID;

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
import com.is.model.Department;
import com.is.service.CompanyService;
import com.is.util.BusinessHelper;
import com.is.util.ResponseFactory;

@Component("companyHandle")
@Path("company")
public class CompanyHandle {
	
	@Autowired
	private CompanyService companyService;
	
	
	@POST
	@Path("/addCompany")
	public Response addCompany(@Context HttpServletRequest request,MultivaluedMap<String, String> formParams){
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		boolean state=companyService.addCompany(requestMap.get(COMPANY_NAME), requestMap.get(ADDRESS), requestMap.get(START_TIME), requestMap.get(END_TIME), requestMap.get(USER_NAME), requestMap.get(USER_PSW), requestMap.get(NAME), requestMap.get(CONTACT));
		if(state){
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		}else{
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
	}
	
	@POST
	@Path("/editCompany")
	public Response editCompany(@Context HttpServletRequest request,MultivaluedMap<String, String> formParams){
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		boolean state=companyService.editCompany(requestMap.get(COMPANY_ID), requestMap.get(COMPANY_NAME), requestMap.get(ADDRESS), requestMap.get(START_TIME), requestMap.get(END_TIME));
		if(state){
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		}else{
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
	}
	
	@POST
	@Path("/deleteCompany")
	public Response deleteCompany(@Context HttpServletRequest request,MultivaluedMap<String, String> formParams){
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		boolean state=companyService.deleteCompany(requestMap.get(DEVICE_ID),requestMap.get(COMPANY_ID));
		if(state){
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		}else{
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
	}
	
	@POST
	@Path("/getDepartmentByCompany")
	public Response getDepartmentByCompany(@Context HttpServletRequest request,MultivaluedMap<String, String> formParams){
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		List<Department> list=companyService.getDepartmentByCompany(requestMap.get(COMPANY_ID));
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, list);
	}
	

}
