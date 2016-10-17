package com.is.rest;

import static com.is.constant.ParameterKeys.ADDRESS;
import static com.is.constant.ParameterKeys.COMPANY_ID;
import static com.is.constant.ParameterKeys.COMPANY_NAME;
import static com.is.constant.ParameterKeys.CONTACT;
import static com.is.constant.ParameterKeys.END_TIME;
import static com.is.constant.ParameterKeys.GRADE;
import static com.is.constant.ParameterKeys.NAME;
import static com.is.constant.ParameterKeys.START_TIME;
import static com.is.constant.ParameterKeys.USER_NAME;
import static com.is.constant.ParameterKeys.USER_PSW;

import java.util.ArrayList;
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
import com.is.util.LoginRequired;
import com.is.util.ResponseFactory;

@Component("companyHandle")
@Path("company")
public class CompanyHandle {
	
	@Autowired
	private CompanyService companyService;
	
	
	@POST
	@LoginRequired
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
	@LoginRequired
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
	@LoginRequired
	public Response deleteCompany(@Context HttpServletRequest request,MultivaluedMap<String, String> formParams){
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		String deviceId=(String) request.getSession().getAttribute("deviceSn");
		boolean state=companyService.deleteCompany(deviceId,requestMap.get(COMPANY_ID));
		if(state){
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		}else{
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
	}
	
	@POST
	@Path("/getDepartmentByCompany")
	@LoginRequired
	public Response getDepartmentByCompany(@Context HttpServletRequest request,MultivaluedMap<String, String> formParams){
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		List<Department> list=companyService.getDepartmentByCompany(requestMap.get(COMPANY_ID));
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, list);
	}
	
	
	@POST
	@Path("/getDepartmentByGrade")
	@LoginRequired
	public Response getDepartmentByGrade(@Context HttpServletRequest request,MultivaluedMap<String, String> formParams){
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		List<Department> list=companyService.getDepartmentByGrade(requestMap.get(COMPANY_ID),requestMap.get(GRADE));
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, list);
	}
	
	@POST
	@Path("/insertDepartment")
	@LoginRequired
	public Response insertDepartment(@Context HttpServletRequest request,MultivaluedMap<String, String> formParams){
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		boolean state=companyService.insertDepartment(requestMap.get("name"), requestMap.get("people"), requestMap.get("grade"), requestMap.get("parentId"), requestMap.get("companyId"));
		if(state){
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		}else{
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
	}
	
	@POST
	@Path("/editDepartment")
	@LoginRequired
	public Response editDepartment(@Context HttpServletRequest request,MultivaluedMap<String, String> formParams){
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		boolean state=companyService.editDepartment(requestMap.get("name"), requestMap.get("people"), requestMap.get("grade"), requestMap.get("parentId"), requestMap.get("departmentId"));
		if(state){
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		}else{
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
	}
	
	@POST
	@LoginRequired
	@Path("/deleteDepartment")
	public Response deleteDepartment(@Context HttpServletRequest request,MultivaluedMap<String, String> formParams){
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		boolean state=companyService.deleteDepartment(requestMap.get("departmentId"));
		if(state){
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		}else{
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
	}
	
	@POST
	@Path("/getDepartmentOrganization")
	@LoginRequired
	public Response getDepartmentOrganization(@Context HttpServletRequest request,MultivaluedMap<String, String> formParams){
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		Map<String, String> map=companyService.getDepartmentOrganization(requestMap.get("departmentId"),requestMap.get("grade"));
		int grade=Integer.parseInt(requestMap.get("grade"));
		List<String> list=new ArrayList<>();
		list.add(map.get("one"));
		if(grade>=2){
			list.add(map.get("two"));
		}
		if(grade>=3){
			list.add(map.get("three"));
		}
		if(grade>=4){
			list.add(map.get("four"));
		}
		if(grade>=5){
			list.add(map.get("five"));
		}
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, list);
	}
	

}
