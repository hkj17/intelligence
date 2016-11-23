package com.is.rest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.is.constant.ResponseCode;
import com.is.model.Visitor;
import com.is.model.VisitorInfo;
import com.is.service.VisitorService;
import com.is.util.BusinessHelper;
import com.is.util.LoginRequired;
import com.is.util.ResponseFactory;
import static com.is.constant.ParameterKeys.VISITOR_FACE;

@Component("visitorHandler")
@Path("visitor")
public class VisitorHandler {

	@Autowired
	private VisitorService visitorService;
	
	
	@POST
	//@LoginRequired
	@Path("/addVisitorInfo") 
	public Response addVisitorInfo(@Context HttpServletRequest request,MultivaluedMap<String, String> formParams) {
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		String deviceId=(String) request.getSession().getAttribute("deviceSn");
		String id=visitorService.addVisitorInfo(requestMap.get("name"), requestMap.get("company"), 
				requestMap.get("position"), requestMap.get("telphone"), requestMap.get("email"), 
				requestMap.get("companyUrl"),deviceId,requestMap.get("importance"),
				requestMap.get("birth"),requestMap.get("path"),requestMap.get("cid"));
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, id);
		
	}
	
	@POST
	@Path("/addVisitorInfoByMobile")
	@Consumes("multipart/form-data")
	public Response addVisitorInfoByMobile(@FormDataParam("name") String name,@FormDataParam("company") String company,
			@FormDataParam("position") String position, @FormDataParam("telphone") String telphone, 
			@FormDataParam("email") String email, @FormDataParam("companyUrl") String companyUrl, 
			@FormDataParam("birth") String birth, @FormDataParam("importance") String importance, 
			@FormDataParam("photo") InputStream uploadedInputStream,@FormDataParam("photo") FormDataContentDisposition fileDetail, @Context HttpServletRequest request)
			throws IOException {
		String deviceId=(String) request.getSession().getAttribute("deviceSn");
		String id = UUID.randomUUID().toString().trim().replaceAll("-", "");
		String path=VISITOR_FACE+deviceId+"/"+id+".jpg";
		visitorService.rememPhoto(path, uploadedInputStream);
		boolean state=visitorService.addVisitorInfoByMobile(id, name, company, position, telphone, email, companyUrl, deviceId, importance, birth, path);
		if(state){
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		}else{
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
	}
	
	@POST
	//@LoginRequired
	@Path("/indexVisitor")
	public Response indexVisitor(@Context HttpServletRequest request,MultivaluedMap<String, String> formParams) throws ParseException{
		String deviceId=(String) request.getSession().getAttribute("deviceSn");
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		List<Visitor> list=visitorService.indexVisitor(requestMap.get("departmentId"), requestMap.get("name"),
				requestMap.get("startTime"), requestMap.get("endTime"),deviceId);
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, list);
	}
	
	@POST
	@Path("/getVisitorById")
	//@LoginRequired
	public Response getVisitorById(@Context HttpServletRequest request,MultivaluedMap<String, String> formParams) throws ParseException{
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		VisitorInfo info=visitorService.getVisitorById(requestMap.get("visitorId"));
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, info);
		
	}
	
	@POST
	@Path("/addVisitorLeaveTime")
	//@LoginRequired
	public Response addVisitorLeaveTime(@Context HttpServletRequest request,MultivaluedMap<String, String> formParams) throws ParseException{
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		boolean state=visitorService.addVisitorLeaveTime(requestMap.get("time"), requestMap.get("id"));
		if(state){
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		}else{
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
		
	}
	
	@POST
	@Path("/updateVisitorInfo")
	//@LoginRequired
	public Response updateVisitorInfo(@Context HttpServletRequest request,MultivaluedMap<String, String> formParams) throws ParseException{
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		String deviceId=(String) request.getSession().getAttribute("deviceSn");
		boolean state=visitorService.updateVisitorInfo(deviceId,requestMap.get("visitorId"),requestMap.get("name"), requestMap.get("company"), 
				requestMap.get("position"), requestMap.get("telphone"), requestMap.get("email"), 
				requestMap.get("importance"),requestMap.get("birth"));
		if(state){
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		}else{
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
	}
	
	@POST
	@Path("/deleteVisitorInfo")
	//@LoginRequired
	public Response deleteVisitorInfo(@Context HttpServletRequest request,MultivaluedMap<String, String> formParams) throws ParseException{
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		String deviceId=(String) request.getSession().getAttribute("deviceSn");
		boolean state=visitorService.deleteVisitorInfo(deviceId, requestMap.get("visitorId"));
		if(state){
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		}else{
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
	}
	
	@POST
	@Path("/deleteVisitorRecord")
	//@LoginRequired
	public Response deleteVisitorRecord(@Context HttpServletRequest request,MultivaluedMap<String, String> formParams) throws ParseException{
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		boolean state=visitorService.deleteVisitorRecord(requestMap.get("id"));
		if(state){
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		}else{
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
	}
	
	@POST
	@Path("/getVisitorInfoList")
	//@LoginRequired
	public Response getVisitorInfoList(@Context HttpServletRequest request,MultivaluedMap<String, String> formParams) throws ParseException{
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		String deviceId=(String) request.getSession().getAttribute("deviceSn");
		List<VisitorInfo> list=visitorService.getVisitorInfoByWhere(deviceId, requestMap.get("name"));
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, list);
	}
	
	
	@POST
	@Path("/updateVisitorInfoByRecord")
	//@LoginRequired
	public Response updateVisitorInfoByRecord(@Context HttpServletRequest request,MultivaluedMap<String, String> formParams) throws ParseException{
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		String deviceId=(String) request.getSession().getAttribute("deviceSn");
		String id=visitorService.updateVisitorInfoByRecord(requestMap.get("name"), requestMap.get("company"), 
				requestMap.get("position"), requestMap.get("telphone"), requestMap.get("email"), 
				requestMap.get("companyUrl"),deviceId,requestMap.get("importance"),requestMap.get("birth"),requestMap.get("id"));
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, id);
	}
	
	@POST
	@Path("/deleteCollectionPhoto")
	//@LoginRequired
	public Response deleteCollectionPhoto(@Context HttpServletRequest request,MultivaluedMap<String, String> formParams) throws ParseException{
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		String deviceId=(String) request.getSession().getAttribute("deviceSn");
		String path=requestMap.get("path");
		String cid=requestMap.get("cid");
		visitorService.deletePhoto(cid,deviceId);
		File file=new File(path);
		if(file.exists()){
			file.delete();
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		}
		else {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, "file not found!");
		}
	}
}
