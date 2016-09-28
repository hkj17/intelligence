package com.is.rest;

import static com.is.constant.ParameterKeys.NAME;
import static com.is.constant.ParameterKeys.SESSION_USER;
import static com.is.constant.ParameterKeys.TEXT;
import static com.is.constant.ParameterKeys.TITLE;
import static com.is.constant.ParameterKeys.EMPLOYEE_ID;
import static com.is.constant.ParameterKeys.APP_TYPE;
import static com.is.constant.ParameterKeys.NOTE_ID;
import static com.is.constant.ParameterKeys.APP_ID;

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
import com.is.model.Admin;
import com.is.model.Appointment;
import com.is.model.Employee;
import com.is.model.Notification;
import com.is.service.NotificationService;
import com.is.util.BusinessHelper;
import com.is.util.ResponseFactory;

@Component("notificationHandle")
@Path("notification")
public class NotificationHandle {

	@Autowired
	private NotificationService notificationService;
	
	
	@POST
	@Path("/addnews")
	public Response addnews(@Context HttpServletRequest request,MultivaluedMap<String, String> formParams){
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		boolean state=notificationService.addNews(requestMap.get(NAME), requestMap.get(TITLE), requestMap.get(TEXT));
		if(state){
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		}else{
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
	}
	
	@POST
	@Path("/getNotifyList")
	public Response getNotifyList(@Context HttpServletRequest request){
		List<Notification> notificationServices=notificationService.getNotifyList();
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS,notificationServices);
	}
	
	@POST
	@Path("/getAppointList")
	public Response getAppointList(@Context HttpServletRequest request){
		List<Appointment> appointments=notificationService.getAppointList();
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS,appointments);
	}
	
	@POST
	@Path("/getEmployeeByAdmin")
	public Response getEmployeeByAdmin(@Context HttpServletRequest request){
		Admin admin=(Admin) request.getSession().getAttribute(SESSION_USER);
		List<Employee> employees=notificationService.getEmployeeByAdmin(admin.getAdminId());
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS,employees);
	}
	
	@POST
	@Path("/addAppointment")
	public Response addAppointment(@Context HttpServletRequest request,MultivaluedMap<String, String> formParams){
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		boolean state=notificationService.addAppointment(requestMap.get(EMPLOYEE_ID), requestMap.get(APP_TYPE), requestMap.get(TEXT));
		if(state){
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		}else{
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
	}
	
	@POST
	@Path("/deleteNews")
	public Response deleteNews(@Context HttpServletRequest request,MultivaluedMap<String, String> formParams){
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		boolean state=notificationService.deleteNews(requestMap.get(NOTE_ID));
		if(state){
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		}else{
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
	}
	
	@POST
	@Path("/deleteAppoint")
	public Response deleteAppoint(@Context HttpServletRequest request,MultivaluedMap<String, String> formParams){
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		boolean state=notificationService.deleteAppoint(requestMap.get(APP_ID));
		if(state){
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		}else{
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
	}
}
