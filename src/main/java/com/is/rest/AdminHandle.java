package com.is.rest;

import static com.is.constant.ParameterKeys.AUTHORITY;
import static com.is.constant.ParameterKeys.BIRTH;
import static com.is.constant.ParameterKeys.COMPANY;
import static com.is.constant.ParameterKeys.CONTACT;
import static com.is.constant.ParameterKeys.CONTENT;
import static com.is.constant.ParameterKeys.EMPLOYEE_ID;
import static com.is.constant.ParameterKeys.EMPLOYEE_NAME;
import static com.is.constant.ParameterKeys.ENTRY_TIME;
import static com.is.constant.ParameterKeys.NAME;
import static com.is.constant.ParameterKeys.OLD_PASSWORD;
import static com.is.constant.ParameterKeys.SESSION_USER;
import static com.is.constant.ParameterKeys.SEX;
import static com.is.constant.ParameterKeys.USER_NAME;
import static com.is.constant.ParameterKeys.USER_PSW;
import static com.is.constant.ParameterKeys.WECHAT;
import static com.is.constant.ParameterKeys.DEVICE_ID;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.is.constant.ResponseCode;
import com.is.model.Admin;
import com.is.model.Company;
import com.is.model.Employee;
import com.is.service.AdminService;
import com.is.util.BusinessHelper;
import com.is.util.ResponseFactory;
import com.is.util.TestGet;

@Component("adminHandler")
@Path("admin")
public class AdminHandle {

	@Autowired
	private AdminService adminService;

	private static final String IMAGES_PATH = "/cloudweb/server/tomcat_intel/webapps/employee_img/";

	@POST
	@Path("/AccountAssignment")
	public Response AccountAssignment(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) {
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		boolean state = adminService.AccountAssignment(requestMap.get(USER_NAME), requestMap.get(USER_PSW),
				requestMap.get(AUTHORITY), requestMap.get(EMPLOYEE_ID), requestMap.get(DEVICE_ID));
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
			return ResponseFactory.response(Response.Status.OK, admin.getResponseCode(), admin);
		}
		return ResponseFactory.response(Response.Status.OK, admin.getResponseCode(), null);
	}

	@POST
	@Path("/confirm")
	public Response confirm(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) {
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		Admin admin = (Admin) request.getSession().getAttribute(SESSION_USER);
		if (null == admin) {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, "登录过期，请重新登录！");
		}
		String username = admin.getUsername();
		Admin newadmin = adminService.login(username, requestMap.get(OLD_PASSWORD));
		if (newadmin.getResponseCode().compareTo(ResponseCode.SUCCESS) == 0) {

			return ResponseFactory.response(Response.Status.OK, newadmin.getResponseCode(), null);
		} else {
			return ResponseFactory.response(Response.Status.OK, newadmin.getResponseCode(), "原密码错误！");
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
		boolean state = adminService.deleteUser(requestMap.get(EMPLOYEE_ID));
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
				requestMap.get(SEX), requestMap.get(BIRTH), requestMap.get(CONTACT), requestMap.get(ENTRY_TIME),
				requestMap.get(WECHAT), requestMap.get(COMPANY), requestMap.get(CONTENT));
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
	public Response demo(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) {
		String result = TestGet.httpGet("http://192.168.1.188:808/");
		result = result.substring(result.indexOf("<table>"), result.indexOf("</body>"));
		Map<String, Object> map = new HashMap<>();
		map.put("you", result);
		map.put("me", "my table");
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, map);
	}

	@POST
	@Path("/updateEmployee")
	public Response updateEmployee(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) {
		boolean state = adminService.updateEmployee();
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, state);
	}

	@POST
	@Path("/addEmployee")
	public Response addEmployee(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) {
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		String state = adminService.addEmployeeTest(requestMap.get("name"), requestMap.get("sex"),
				requestMap.get("birth"), requestMap.get("contact"), requestMap.get("entryTime"),
				requestMap.get("wechat"), requestMap.get("company"), requestMap.get("content"),
				requestMap.get("folder_name"), requestMap.get("position"), requestMap.get("jobId"),
				requestMap.get("address"), requestMap.get("email"), requestMap.get("idCard"),
				requestMap.get("department"));
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, state);
	}

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
	@Path("/getImg")
	@Consumes("multipart/form-data")
	public Response getImg(@FormDataParam("id") String id, @FormDataParam("photo") InputStream uploadedInputStream,
			@FormDataParam("photo") FormDataContentDisposition fileDetail) throws IOException {
		DiskFileItemFactory factory = new DiskFileItemFactory();
		// 2、创建一个文件上传解析器
		ServletFileUpload upload = new ServletFileUpload(factory);
		// 解决上传文件名的中文乱码
		upload.setHeaderEncoding("UTF-8");
		FileOutputStream out = new FileOutputStream(IMAGES_PATH + id + ".jpg");
		try {
			byte buffer[] = new byte[1024];
			// 判断输入流中的数据是否已经读完的标识
			int len = 0;
			// 循环将输入流读入到缓冲区当中，(len=in.read(buffer))>0就表示in里面还有数据
			while ((len = uploadedInputStream.read(buffer)) > 0) {
				// 使用FileOutputStream输出流将缓冲区的数据写入到指定的目录(savePath + "\\" +
				// filename)当中
				out.write(buffer, 0, len);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			uploadedInputStream.close();
			out.close();
		}
		boolean state = adminService.updateEmployee(id, IMAGES_PATH + id + ".jpg");
		if (state) {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		} else {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, "no user");
		}

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
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		boolean state = adminService.excuteCollection(requestMap.get(DEVICE_ID));
		if (state) {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		} else {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, "no user");
		}
	}

	@POST
	@Path("/completeCollection")
	public Response completeCollection(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) {
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		String path = adminService.completeCollection(requestMap.get(DEVICE_ID));
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, path);
	}

	@POST
	@Path("/addEmployee")
	public Response addEmployeeInfo(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams)  {
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		String employeeId = adminService.addEmployee(requestMap.get("name"), requestMap.get("sex"), requestMap.get("birth"), requestMap.get("contact"),
				requestMap.get("entryTime"), requestMap.get("wechat"), requestMap.get("deviceId"),  requestMap.get("content"),  requestMap.get("path"), 
				 requestMap.get("position"), requestMap.get("jobId"),  requestMap.get("address"),  requestMap.get("email"),  requestMap.get("idCard"),  requestMap.get("department"));

		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, employeeId);
	}
}
