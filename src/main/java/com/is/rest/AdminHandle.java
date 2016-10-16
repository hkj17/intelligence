package com.is.rest;

import static com.is.constant.ParameterKeys.AUTHORITY;
import static com.is.constant.ParameterKeys.BIRTH;
import static com.is.constant.ParameterKeys.CONTACT;
import static com.is.constant.ParameterKeys.EMPLOYEE_ID;
import static com.is.constant.ParameterKeys.EMPLOYEE_NAME;
import static com.is.constant.ParameterKeys.NAME;
import static com.is.constant.ParameterKeys.OLD_PASSWORD;
import static com.is.constant.ParameterKeys.SESSION_USER;
import static com.is.constant.ParameterKeys.USER_NAME;
import static com.is.constant.ParameterKeys.USER_PSW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.is.constant.ResponseCode;
import com.is.model.Admin;
import com.is.model.Company;
import com.is.model.Employee;
import com.is.service.AdminService;
import com.is.util.BusinessHelper;
import com.is.util.ResponseFactory;

@Component("adminHandler")
@Path("admin")
public class AdminHandle {

	@Autowired
	private AdminService adminService;


	@POST
	@Path("/AccountAssignment")
	public Response AccountAssignment(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) {
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		String deviceId=(String) request.getSession().getAttribute("deviceSn");
		boolean state = adminService.AccountAssignment(requestMap.get(USER_NAME), requestMap.get(USER_PSW),
				requestMap.get(AUTHORITY), requestMap.get(EMPLOYEE_ID), deviceId);
		if (state) {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		} else {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
	}

	@POST
	@Path("/login")
	public Response login(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) {
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		Admin admin = adminService.login(requestMap.get(USER_NAME), requestMap.get(USER_PSW));
		if (admin == null) {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
		if (admin.getResponseCode().compareTo(ResponseCode.SUCCESS) == 0) {
			request.getSession().setAttribute(SESSION_USER, admin);
			admin.setPassword("");
			Employee employee=adminService.getEmployeeByAdminId(admin.getAdminId());
			Admin admin2=employee.getAdmin();
			admin2.setPassword(null);
			employee.setAdmin(admin2);
			request.getSession().setAttribute("deviceSn", admin.getDeviceId());
			//System.out.println(request.getSession().getAttribute("deviceSn"));
			return ResponseFactory.response(Response.Status.OK, admin.getResponseCode(), employee);
		}
		return ResponseFactory.response(Response.Status.OK, admin.getResponseCode(), null);
	}

	@POST
	@Path("/confirm")
	public Response confirm(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) {
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		Admin admin = (Admin) request.getSession().getAttribute(SESSION_USER);
		if (null == admin) {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, "鐧诲綍杩囨湡锛岃閲嶆柊鐧诲綍锛�");
		}
		String username = admin.getUsername();
		Admin newadmin = adminService.login(username, requestMap.get(OLD_PASSWORD));
		if (newadmin.getResponseCode().compareTo(ResponseCode.SUCCESS) == 0) {

			return ResponseFactory.response(Response.Status.OK, newadmin.getResponseCode(), null);
		} else {
			return ResponseFactory.response(Response.Status.OK, newadmin.getResponseCode(), "鍘熷瘑鐮侀敊璇紒");
		}
	}

	@POST
	@Path("/editPassword")
	public Response editPassword(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) {
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		Admin admin = (Admin) request.getSession().getAttribute(SESSION_USER);
		boolean state = adminService.editPassword(admin.getUsername(), requestMap.get(USER_PSW));
		if (state) {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		} else {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
	}

	@GET
	@Path("/getUserList")
	public Response getUserList(@Context HttpServletRequest request) {
		List<Employee> userlist = adminService.getEmployeeList();
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, userlist);
	}

	@GET
	@Path("/getCompanyList")
	public Response getCompanyList(@Context HttpServletRequest request) {
		List<Company> companyList = adminService.getCompanyList();
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, companyList);
	}

	@POST
	@Path("/deleteUser")
	public Response deleteUser(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) {
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		String deviceId=(String) request.getSession().getAttribute("deviceSn");
		boolean state = adminService.deleteUser(deviceId,requestMap.get(EMPLOYEE_ID));
		if (state) {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		} else {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
	}

	@POST
	@Path("/editEmployee")
	public Response editEmployee(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) {
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		boolean state = adminService.editEmployee(requestMap.get(EMPLOYEE_ID), requestMap.get(NAME),
				requestMap.get(BIRTH), requestMap.get(CONTACT),  requestMap.get("deviceId"),
				requestMap.get("positon"), requestMap.get("jobId"), requestMap.get("address"),
				requestMap.get("email"), requestMap.get("idCard"), requestMap.get("workPos"));
		if (state) {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		} else {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
	}

	@POST
	@Path("/getEmployeeInfo")
	public Response getEmployeeInfo(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) {
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		Employee employee = adminService.getEmployeeById(requestMap.get(EMPLOYEE_ID));
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, employee);
	}

	@POST
	@Path("/getEmployeeByName")
	public Response getEmployeeByName(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) {
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		List<Employee> employees = adminService.getEmployeeByName(requestMap.get(EMPLOYEE_NAME));
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, employees);
	}

	@GET
	@Path("/demo")
	public void demo() {
		adminService.test();
	}

	/*@POST
	@Path("/updateEmployee")
	public Response updateEmployee(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) {
		boolean state = adminService.updateEmployee();
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, state);
	}*/


	@GET
	@Path("/addEmployeeGetPhotoList")
	public Response addEmployeeGetPhotoList(@Context HttpServletRequest request,
			MultivaluedMap<String, String> formParams) {
		List<Map<String, String>> list = new ArrayList<>();
		for (int i = 1; i < 6; i++) {
			Map<String, String> map = new HashMap<>();
			map.put("folder_name", String.valueOf(i));
			map.put("photo", "http://120.26.60.164:5555/employee_img/" + i + ".jpg");
			list.add(map);
		}
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, list);
	}


	@POST
	@Path("/getEmployeeByWhere")
	public Response getEmployeeByWhere(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) {
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		List<Employee> employees = adminService.getEmployeeByWhere(requestMap.get("word"), requestMap.get("company"),
				requestMap.get("department"));
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, employees);
	}

	@POST
	@Path("/excuteCollection")
	public Response excuteCollection(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) {
		String deviceId=(String) request.getSession().getAttribute("deviceSn");
		boolean state = adminService.excuteCollection(deviceId);
		if (state) {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		} else {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
	}

	@POST
	@Path("/completeCollection")
	public Response completeCollection(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) {
		String deviceId=(String) request.getSession().getAttribute("deviceSn");
		String path = adminService.completeCollection(deviceId);
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, path);
	}

	@POST
	@Path("/addEmployee")
	public Response addEmployeeInfo(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams)  {
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		String deviceId=(String) request.getSession().getAttribute("deviceSn");
		String employeeId = adminService.addEmployee(requestMap.get("name"), 
				requestMap.get("birth"), requestMap.get("contact"),deviceId,   requestMap.get("path"), 
				 requestMap.get("position"), requestMap.get("jobId"),  requestMap.get("address"),  
				 requestMap.get("email"),  requestMap.get("idCard"),
				 requestMap.get("workPos"),requestMap.get("departmentId"),
				 requestMap.get("sex"),requestMap.get("isDuty"));

		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, employeeId);
	}
	
	@POST
	@Path("/searchAdmin")
	public Response searchAdmin(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams)  {
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		List<Admin> list=adminService.searchAdmin(requestMap.get("name"), requestMap.get("auth"));
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, list);
	}
	
	@POST
	@Path("/editAdmin")
	public Response editAdmin(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams){
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		boolean state=adminService.editAdmin(requestMap.get("id"), requestMap.get("name"),requestMap.get("password"), requestMap.get("auth"));
		if (state) {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		} else {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
	}
	
	@POST
	@Path("/deleteAdmin")
	public Response deleteAdmin(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams){
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		boolean state=adminService.deleteAdmin(requestMap.get("id"));
		if (state) {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		} else {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
	}
	
	@POST
	@Path("/getAuditPerson")
	public Response getAuditPerson(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams){
		String deviceId=(String) request.getSession().getAttribute("deviceSn");
		List<Employee> list=adminService.getAuditPersonList(deviceId);
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, list);
	}
	
}
