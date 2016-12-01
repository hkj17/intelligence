package com.is.rest;

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
import com.is.model.Appointment;
import com.is.service.AppointmentService;
import com.is.util.BusinessHelper;
import com.is.util.LoginRequired;
import com.is.util.ResponseFactory;

@Component("appointmentHandle")
@Path("appointment")
public class AppointmentHandle {

	@Autowired
	private AppointmentService appointmentService;
	
	
	@POST
	@LoginRequired
	@Path("/addAppointment")
	public Response addAppointment(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) {
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		String deviceId=(String) request.getSession().getAttribute("deviceSn");
		boolean state=appointmentService.addAppointment(requestMap.get("visitorId"), requestMap.get("startTime"), requestMap.get("endTime"), requestMap.get("content"),deviceId, requestMap.get("employeeId"));
		if(state){
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		}else{
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
	}
	
	@POST
	@LoginRequired
	@Path("/getAppointmentByUser")
	public Response getAppointmentByUser(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) {
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		List<Appointment> list=appointmentService.getAppointmentByUser(requestMap.get("employeeId"));
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, list);
	}
	
	@POST
	@LoginRequired
	@Path("/editAppointment")
	public Response editAppointment(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) {
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		String deviceId=(String) request.getSession().getAttribute("deviceSn");
		boolean state=appointmentService.editAppointment(requestMap.get("appId"), requestMap.get("startTime"), requestMap.get("endTime"), requestMap.get("content"),deviceId);
		if(state){
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		}else{
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
	}
	
	@POST
	@LoginRequired
	@Path("/deleteAppointment")
	public Response deleteAppointment(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) {
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		String deviceId=(String) request.getSession().getAttribute("deviceSn");
		boolean state=appointmentService.deleteAppointment(requestMap.get("appId"),deviceId);
		if(state){
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		}else{
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
	}
}
