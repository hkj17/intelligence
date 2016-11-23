package com.is.rest;

import static com.is.constant.ParameterKeys.CLOCK_TIME;
import static com.is.constant.ParameterKeys.COMPANY_ID;
import static com.is.constant.ParameterKeys.CR_ID;
import static com.is.constant.ParameterKeys.DEPARTMENT;
import static com.is.constant.ParameterKeys.EMPLOYEE_ID;
import static com.is.constant.ParameterKeys.END_TIME;
import static com.is.constant.ParameterKeys.MORNING_CLOCK;
import static com.is.constant.ParameterKeys.NAME;
import static com.is.constant.ParameterKeys.NIGHT_CLOCK;
import static com.is.constant.ParameterKeys.RULE;
import static com.is.constant.ParameterKeys.START_TIME;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.is.constant.ResponseCode;
import com.is.model.ClockAbnormal;
import com.is.model.ClockAppeal;
import com.is.model.ClockRecord;
import com.is.model.ClockTime;
import com.is.model.Employee;
import com.is.service.ClockService;
import com.is.util.BusinessHelper;
import com.is.util.LoginRequired;
import com.is.util.ResponseFactory;

@Component("clockHandle")
@Path("clock")
public class ClockHandle {

	@Autowired
	private ClockService clockService;
	

	// private static final String IMAGES_PATH =
	// "D:\\tomcat1.8\\webapps\\intelligentStage-system-manage\\images\\clock_record_photo\\";

	@POST
	@Path("/getClockList")
	//@LoginRequired
	public Response getClockList(@Context HttpServletRequest request) throws ParseException {
		List<ClockRecord> clocklist = clockService.getClockList();
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, clocklist);
	}

	@POST
	@Path("/getClockByWhere")
	//@LoginRequired
	public Response getClockByWhere(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) {
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		String deviceId=(String) request.getSession().getAttribute("deviceSn");
		List<ClockRecord> clockRecords = clockService.getClockByWhere(requestMap.get(DEPARTMENT),requestMap.get(NAME), requestMap.get(START_TIME),
				requestMap.get(END_TIME), requestMap.get(RULE),deviceId);
		if (!clockRecords.equals(null)) {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, clockRecords);
		} else {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
	}

	@POST
	//@LoginRequired
	@Path("/getEmployeeByCompany")
	public Response getEmployeeByCompany(@Context HttpServletRequest request,
			MultivaluedMap<String, String> formParams) {
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		List<Employee> employees = clockService.getEmployeeByCompany(requestMap.get(COMPANY_ID));
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, employees);
	}

	@GET
	//@LoginRequired
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/getEmployeeByCompanyId/{company_id}")
	public Response getEmployeeByCompanyId(@PathParam("company_id") String companyId) {
		List<Employee> employees = clockService.getEmployeeByCompany(companyId);
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, employees);
	}

	
	@POST
	@Path("/clockTimeAppeal")
	//@LoginRequired
	public Response clockTimeAppeal(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) {
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		String deviceId=(String) request.getSession().getAttribute("deviceSn");
		boolean state = clockService.clockTimeAppeal(deviceId,requestMap.get("employeeId"), requestMap.get("firstClock"), requestMap.get("lastClock"), 
				requestMap.get("appealReason"), requestMap.get("appealContent"), requestMap.get("auditPersonId"), requestMap.get("appealTime"));
		if (state) {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		} else {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
	}
	
	@POST
	@Path("/getClockTimeAppeal")
	//@LoginRequired
	public Response getClockTimeAppeal(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) {
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		List<ClockAppeal> list=clockService.getClockTimeAppeal(requestMap.get(EMPLOYEE_ID));
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, list);
	}
	
	@POST
	@Path("/getClockAuditList")
	//@LoginRequired
	public Response getClockAuditList(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) {
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		List<ClockAppeal> list=clockService.getClockAuditList(requestMap.get(EMPLOYEE_ID));
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, list);
	}

	@POST
	@Path("/deleteClockTimeAppeal")
	//@LoginRequired
	public Response deleteClockTimeAppeal(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) {
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		boolean state = clockService.deleteClockTimeAppeal(requestMap.get("appealId"));
		if (state) {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		} else {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
	}
	
	@POST
	@Path("/checkHandClock")
	//@LoginRequired
	public Response checkHandClock(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) {
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		String deviceId=(String) request.getSession().getAttribute("deviceSn");
		boolean state = clockService.checkHandClock(requestMap.get("clockId"),requestMap.get("result"),deviceId);
		if (state) {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		} else {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
	}

	@POST
	@Path("/addClockMobile")
	//@LoginRequired
	public Response addClockMobile(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams)
			throws IOException {
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		String deviceId=(String) request.getSession().getAttribute("deviceSn");
		boolean state = clockService.addClockAbnormal(deviceId,requestMap.get(EMPLOYEE_ID), requestMap.get(CLOCK_TIME));
		if (state) {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		} else {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
	}

	@POST
	@Path("/getClockByDate")
	//@LoginRequired
	public Response getClockByDate(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) {
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		String employee = requestMap.get(EMPLOYEE_ID);
		String morningClock = requestMap.get(MORNING_CLOCK);
		String nightClock = requestMap.get(NIGHT_CLOCK);
		if (!"".equals(morningClock) && morningClock != null) {
			morningClock = morningClock.substring(0, 10);
			ClockRecord clockRecord = clockService.getClockByMc(employee, morningClock);
			ClockRecord clockRecord2 = clockService.getClockByNc(Integer.parseInt(employee), morningClock);
			if (null != clockRecord) {
				return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, clockRecord);
			} else {
				return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, clockRecord2);
			}
		} else {
			nightClock = nightClock.substring(0, 10);
			ClockRecord clockRecord = clockService.getClockByNc(Integer.parseInt(employee), nightClock);
			ClockRecord clockRecord2 = clockService.getClockByMc(employee, nightClock);
			if (null != clockRecord) {
				return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, clockRecord);
			} else {
				return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, clockRecord2);
			}
		}
	}

	@POST
	@Path("/updateClock")
	//@LoginRequired
	public Response updateClock(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) {
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		boolean state = clockService.updateClock(requestMap.get(CR_ID), requestMap.get(EMPLOYEE_ID),
				requestMap.get(MORNING_CLOCK), requestMap.get(NIGHT_CLOCK));
		if (state) {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		} else {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
	}

	@POST
	@Path("/getClockPhoto")
	//@LoginRequired
	public Response getClockPhoto(@Context HttpServletRequest request) {
		List<ClockTime> clockPhotos = clockService.getClockPhoto();
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, clockPhotos);
	}
	
	@POST
	@Path("/getClockByEmployee")
	//@LoginRequired
	public Response getClockByEmployee(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) {
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		List<ClockRecord> list=clockService.getClockByEmployee(requestMap.get(EMPLOYEE_ID));
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, list);
	}


	@POST
	@Path("/getDetailClock")
	//@LoginRequired
	public Response getDetailClock(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) {
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		List<ClockTime> list=clockService.getDetailClock(requestMap.get(EMPLOYEE_ID),requestMap.get("time"));
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, list);
	}
	
	@POST
	@Path("/getHandClockList")
	//@LoginRequired
	public Response getHandClockList(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) {
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		String deviceId=(String) request.getSession().getAttribute("deviceSn");
		List<ClockAbnormal> list=clockService.getHandClockList(requestMap.get("startTime"), requestMap.get("endTime"), deviceId);
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, list);
	}

}
