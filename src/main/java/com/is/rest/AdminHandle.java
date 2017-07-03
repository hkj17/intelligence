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
import static com.is.constant.ParameterKeys.DEVICE_SN;
import static com.is.constant.ParameterKeys.COMPANY_ID;
import static com.is.constant.ParameterKeys.PHOTO_PATH;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

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
import com.is.util.LoginRequired;
import com.is.util.ResponseFactory;

import net.sf.json.JSONObject;

@Component("adminHandler")
@Path("admin")
public class AdminHandle {

	@Autowired
	private AdminService adminService;


	@POST
	@LoginRequired
	@Path("/AccountAssignment")
	public Response AccountAssignment(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) {
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		String deviceId=(String) request.getSession().getAttribute(DEVICE_SN);
		Admin admin=adminService.getAdminByName(requestMap.get(USER_NAME));
		if(admin!=null){
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, "user exist!");
		}
		boolean state = adminService.AccountAssignment(requestMap.get(USER_NAME), requestMap.get(USER_PSW),
				requestMap.get(AUTHORITY), requestMap.get(EMPLOYEE_ID), deviceId);
		if (state) {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		} else {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
	}
	
	@POST
	@Path("/checkname")
	public Response checkname(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) {
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		Admin admin=adminService.getAdminByName(requestMap.get(USER_NAME));
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, admin);
	}
	
	@POST
	@Path("/adminLogin")
	public Response adminLogin(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) {
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		String username=requestMap.get("adminName");
		String password=requestMap.get("password");
		if (username.equals("superadmin") && password.equals("123456")) {
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
			request.getSession().setAttribute(COMPANY_ID, employee.getCompany().getCompanyId());
			request.getSession().setAttribute(DEVICE_SN, admin.getDeviceId());
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
	@LoginRequired
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
	@LoginRequired
	public Response getUserList(@Context HttpServletRequest request) {
		int companyId=(int) request.getSession().getAttribute(COMPANY_ID);
		List<Employee> userlist = adminService.getEmployeeList(companyId);
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, userlist);
	}

	@GET
	@Path("/getCompanyList")
	@LoginRequired
	public Response getCompanyList(@Context HttpServletRequest request) {
		List<Company> companyList = adminService.getCompanyList();
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, companyList);
	}

	@POST
	@Path("/deleteUser")
	@LoginRequired
	public Response deleteUser(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) throws InterruptedException, ExecutionException, TimeoutException {
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		String deviceId=(String) request.getSession().getAttribute(DEVICE_SN);
		boolean state = adminService.deleteUser(deviceId,requestMap.get(EMPLOYEE_ID));
		if (state) {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		} else {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
	}

	@POST
	@Path("/editEmployee")
	@LoginRequired
	public Response editEmployee(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) {
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		String deviceId=(String) request.getSession().getAttribute(DEVICE_SN);
		boolean state = adminService.editEmployee(requestMap.get(EMPLOYEE_ID), requestMap.get(NAME),
				requestMap.get(BIRTH), requestMap.get(CONTACT),  deviceId,
				requestMap.get("positon"), requestMap.get("jobId"), requestMap.get("address"),
				requestMap.get("email"), requestMap.get("idCard"), requestMap.get("workPos"),
				requestMap.get("sex"),requestMap.get("path"),requestMap.get("departmentId"),requestMap.get("adminName"));
		if (state) {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		} else {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
	}

	@POST
	@Path("/getEmployeeInfo")
	@LoginRequired
	public Response getEmployeeInfo(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) {
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		Employee employee = adminService.getEmployeeById(requestMap.get(EMPLOYEE_ID));
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, employee);
	}

	@POST
	@Path("/getEmployeeByName")
	@LoginRequired
	public Response getEmployeeByName(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) {
		int companyId=(int) request.getSession().getAttribute(COMPANY_ID);
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		List<Employee> employees = adminService.getEmployeeByName(requestMap.get(EMPLOYEE_NAME),companyId);
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, employees);
	}

	/*@POST
	@Path("/updateEmployee")
	public Response updateEmployee(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) {
		boolean state = adminService.updateEmployee();
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, state);
	}*/


	@GET
	@Path("/addEmployeeGetPhotoList")
	@LoginRequired
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
	@LoginRequired
	@Path("/getEmployeeByWhere")
	public Response getEmployeeByWhere(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) {
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		int companyId=(int) request.getSession().getAttribute(COMPANY_ID);
		List<Employee> employees = adminService.getEmployeeByWhere(requestMap.get("word"),
				requestMap.get("department"),companyId);
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, employees);
	}

	@POST
	@LoginRequired
	@Path("/excuteCollection")
	public Response excuteCollection(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) {
		String deviceId=(String) request.getSession().getAttribute(DEVICE_SN);
		boolean state = adminService.excuteCollection(deviceId);
		if (state) {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		} else {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
	}

	@POST
	@LoginRequired
	@Path("/completeCollection")
	public Response completeCollection(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) {
		String deviceId=(String) request.getSession().getAttribute(DEVICE_SN);
		String path = adminService.completeCollection(deviceId);
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, path);
	}

	@POST
	@LoginRequired
	@Path("/addEmployee")
	public Response addEmployeeInfo(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) throws InterruptedException, ExecutionException, TimeoutException  {
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		String deviceId=(String) request.getSession().getAttribute(DEVICE_SN);
		String employeeId = adminService.addEmployee(requestMap.get("name"), 
				requestMap.get("birth"), requestMap.get("contact"),deviceId, requestMap.get("path"), 
				 requestMap.get("position"), requestMap.get("jobId"),  requestMap.get("address"),  
				 requestMap.get("email"),  requestMap.get("idCard"),
				 requestMap.get("workPos"),requestMap.get("departmentId"),
				 requestMap.get("sex"),requestMap.get("isDuty"),requestMap.get("cid"));
		if (employeeId!=null) {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, employeeId);
		} else {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
	}
	
	@POST
	@LoginRequired
	@Path("/addTemplate")
	public Response addTemplate(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams){
		String deviceId=(String) request.getSession().getAttribute(DEVICE_SN);
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		try {
			JSONObject jsonObject = adminService.addTemplate(deviceId,requestMap.get(EMPLOYEE_ID),requestMap.get(PHOTO_PATH),requestMap.get("cid"));
			if(jsonObject!=null){
				return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, jsonObject);
			}else{
				return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
	}
	
	@POST
	@LoginRequired
	@Path("/deleteTemplate")
	public Response deleteTemplate(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) throws InterruptedException, ExecutionException, TimeoutException{
		String deviceId=(String) request.getSession().getAttribute(DEVICE_SN);
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		boolean state = adminService.deleteTemplate(deviceId, requestMap.get(EMPLOYEE_ID), requestMap.get(PHOTO_PATH));
		if(state){
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		}else{
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
	}
	
	@POST
	@LoginRequired
	@Path("/searchAdmin")
	public Response searchAdmin(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams)  {
		String deviceId=(String) request.getSession().getAttribute(DEVICE_SN);
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		List<Admin> list=adminService.searchAdmin(requestMap.get("name"), requestMap.get("auth"),deviceId);
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, list);
	}
	
	@POST
	@LoginRequired
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
	@LoginRequired
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
	@LoginRequired
	@Path("/getAuditPerson")
	public Response getAuditPerson(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams){
		String deviceId=(String) request.getSession().getAttribute(DEVICE_SN);
		List<Employee> list=adminService.getAuditPersonList(deviceId);
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, list);
	}
	
	@POST
	@Path("/adminManage")
	public Response adminManage(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams){
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		boolean state=adminService.adminManage(requestMap.get(DEVICE_SN), requestMap.get(COMPANY_ID), requestMap.get("username"),requestMap.get("password"));
		if (state) {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		} else {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
	}
	
	
	@POST
	@LoginRequired
	@Path("/getPhotoByTemplate")
	public Response getPhotoByTemplate(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams){
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		List<String> list=adminService.getPhotoByTemplate(requestMap.get(EMPLOYEE_ID));
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, list);
	}
	
	@POST
	@LoginRequired
	@Path("/resetPassword")
	public Response resetPassword(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams){
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		boolean state=adminService.resetPassword(requestMap.get("adminId"));
		if (state) {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		} else {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
				
	}
	
	
	@POST
	@LoginRequired
	@Path("/checkPhoneNumber")
	public Response checkPhoneNumber(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams){
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		boolean state=adminService.checkPhoneNumber(requestMap.get("phone"));
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, state);
	}
	
	@POST
	@LoginRequired
	@Path("/editAdminPassword")
	public Response editAdminPassword(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams){
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		boolean state=adminService.editAdminPassword(requestMap.get("oldPassword"),requestMap.get("newPassword"),requestMap.get("adminId"));
		if (state) {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		} else {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
				
	}
	
//	@POST
//	@Path("/test")
//	public Response test(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams){
//		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
//		String deviceId=requestMap.get("id");
//		String employeeIds=requestMap.get("value");
//		if(employeeIds!=null || !"".equals(employeeIds)){
//			employeeIds=employeeIds.replace("[", "(").replace("]", ")");
//		}
//		List<Employee> list=adminService.getEmployeeByIds(employeeIds,deviceId);
//		for(Employee employee:list){
//			System.out.println(1);
//		}
//		
//		List<String> existEmployee=adminService.getExistEmployee(employeeIds,deviceId);
//		String[] eids=employeeIds.substring(1,employeeIds.length()-1).split(",");
//		for(String eid:eids){
//			if(!existEmployee.contains(eid)){
//				System.out.println(eid);
//			}
//		}
//		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
//	}
	
//	/** for testing purpose only */
//	@GET
//	@Path("/firstTemplate")
//	public Response firstTemplate(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams){
//		adminService.addFirstTemplate();
//		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
//	}

}
