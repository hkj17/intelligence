package com.is.rest;

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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.is.constant.ResponseCode;
import com.is.model.ClockPhoto;
import com.is.model.ClockRecord;
import com.is.model.Employee;
import com.is.service.AdminService;
import com.is.service.ClockService;
import com.is.util.BusinessHelper;
import com.is.util.ResponseFactory;

@Component("clockHandle")
@Path("clock")
public class ClockHandle {

	@Autowired
	private ClockService clockService;
	
	@Autowired
	private AdminService adminService;
	

	// private static final String IMAGES_PATH =
	// "D:\\tomcat1.8\\webapps\\intelligentStage-system-manage\\images\\clock_record_photo\\";

	@POST
	@Path("/getClockList")
	public Response getClockList(@Context HttpServletRequest request) throws ParseException {
		List<ClockRecord> clocklist = clockService.getClockList();
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, clocklist);
	}

	@POST
	@Path("/getClockByWhere")
	public Response getClockByWhere(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) {
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		List<ClockRecord> clockRecords = clockService.getClockByWhere(requestMap.get(DEPARTMENT),requestMap.get(NAME), requestMap.get(START_TIME),
				requestMap.get(END_TIME), requestMap.get(RULE));
		if (!clockRecords.equals(null)) {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, clockRecords);
		} else {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
	}

	@POST
	@Path("/getEmployeeByCompany")
	public Response getEmployeeByCompany(@Context HttpServletRequest request,
			MultivaluedMap<String, String> formParams) {
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		List<Employee> employees = clockService.getEmployeeByCompany(requestMap.get(COMPANY_ID));
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, employees);
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/getEmployeeByCompanyId/{company_id}")
	public Response getEmployeeByCompanyId(@PathParam("company_id") String companyId) {
		List<Employee> employees = clockService.getEmployeeByCompany(companyId);
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, employees);
	}

	@POST
	@Path("/addClock")
	public Response addClock(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) {
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		boolean state = clockService.addClock(requestMap.get(EMPLOYEE_ID), requestMap.get(MORNING_CLOCK),
				requestMap.get(NIGHT_CLOCK));
		if (state) {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		} else {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
	}

	@POST
	@Path("/addClockMobile")
	@Consumes("multipart/form-data")
	public Response addClockMobile(@FormDataParam("employeeId") String employeeId,
			@FormDataParam("clockTime") String clockTime, @FormDataParam("photo") InputStream uploadedInputStream,
			@FormDataParam("photo") FormDataContentDisposition fileDetail, @Context HttpServletRequest request)
			throws IOException {
		@SuppressWarnings("deprecation")
		String path = request.getRealPath("/");

		path = path + "/images/clock_record_photo/";
		String fileName = fileDetail.getFileName();
		String fileEnd = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
		if (!fileEnd.equals("jpg") && !fileEnd.equals("png")) {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, "请输入jpg或者png格式的图片格式");
		}
		// 1、创建一个DiskFileItemFactory工厂
		DiskFileItemFactory factory = new DiskFileItemFactory();
		// 2、创建一个文件上传解析器
		ServletFileUpload upload = new ServletFileUpload(factory);
		// 解决上传文件名的中文乱码
		upload.setHeaderEncoding("UTF-8");
		FileOutputStream out = new FileOutputStream(
				path + employeeId  + ".jpg");
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

		boolean state = clockService.addClockMobile(employeeId, clockTime, path);
		if (state) {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		} else {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
	}

	@POST
	@Path("/getClockByDate")
	public Response getClockByDate(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) {
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		String employee = requestMap.get(EMPLOYEE_ID);
		String morningClock = requestMap.get(MORNING_CLOCK);
		String nightClock = requestMap.get(NIGHT_CLOCK);
		if (!"".equals(morningClock) && morningClock != null) {
			morningClock = morningClock.substring(0, 10);
			ClockRecord clockRecord = clockService.getClockByMc(Integer.parseInt(employee), morningClock);
			ClockRecord clockRecord2 = clockService.getClockByNc(Integer.parseInt(employee), morningClock);
			if (null != clockRecord) {
				return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, clockRecord);
			} else {
				return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, clockRecord2);
			}
		} else {
			nightClock = nightClock.substring(0, 10);
			ClockRecord clockRecord = clockService.getClockByNc(Integer.parseInt(employee), nightClock);
			ClockRecord clockRecord2 = clockService.getClockByMc(Integer.parseInt(employee), nightClock);
			if (null != clockRecord) {
				return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, clockRecord);
			} else {
				return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, clockRecord2);
			}
		}
	}

	@POST
	@Path("/updateClock")
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
	public Response getClockPhoto(@Context HttpServletRequest request) {
		List<ClockPhoto> clockPhotos = clockService.getClockPhoto();
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, clockPhotos);
	}
	
	@POST
	@Path("/getClockByEmployee")
	public Response getClockByEmployee(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) {
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		List<ClockRecord> list=clockService.getClockByEmployee(requestMap.get(EMPLOYEE_ID));
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, list);
	}



}
