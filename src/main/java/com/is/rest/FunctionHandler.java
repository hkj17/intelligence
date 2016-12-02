package com.is.rest;

import static com.is.constant.ParameterKeys.EMPLOYEE_ID;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.is.constant.ResponseCode;
import com.is.map.DeviceService;
import com.is.map.FutureMap;
import com.is.model.VersionUpdate;
import com.is.service.EmployeeService;
import com.is.util.BusinessHelper;
import com.is.util.LoginRequired;
import com.is.util.ResponseFactory;
import com.is.websocket.AddFuture;
import com.is.websocket.CheckResponse;
import com.is.websocket.ServiceDistribution;
import com.is.websocket.SyncFuture;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import net.sf.json.JSONObject;

@Component("functionHandler")
@Path("function")
public class FunctionHandler {
	
	
	@Autowired
	private EmployeeService employeeService;
	
	@POST
	@Path("/sendMsg")
	public Response sendMsg(@Context HttpServletRequest request,MultivaluedMap<String, String> formParams) throws IOException{
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		String result=employeeService.sendMsg(requestMap.get(EMPLOYEE_ID),"1525674");
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, result);
	}
	
	@POST
	@Path("/autoUpdate")
	public Response autoUpdate(@Context HttpServletRequest request,MultivaluedMap<String, String> formParams) throws IOException{
		String deviceId=(String) request.getSession().getAttribute("deviceSn");
		VersionUpdate update=employeeService.autoUpdate();
		SyncFuture<String> future=AddFuture.setFuture(deviceId,"114_2");
		CheckResponse response=new CheckResponse(deviceId, "114_2",future);
		response.start();
		boolean state=ServiceDistribution.handleJson114_1(deviceId,update.getVersion(),update.getPath());
		if (state) {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		} else {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
	}
	
	@POST
	@Path("/systemVoice")
	public Response getSystemVoice(@Context HttpServletRequest request,MultivaluedMap<String, String> formParams) throws IOException, InterruptedException, ExecutionException, TimeoutException{
		String deviceId=(String) request.getSession().getAttribute("deviceSn");
		SyncFuture<String> future=new SyncFuture<>();
		 ChannelHandlerContext ctx=DeviceService.getSocketMap(deviceId);
		 if(ctx==null){
			 return null;
		 }
		 ChannelId name=ctx.channel().id();
		 FutureMap.addFuture(name.asLongText()+"112_2", future);
		 
		ServiceDistribution.handleJson112_1(deviceId);
		String voice=future.get(6, TimeUnit.SECONDS);
		FutureMap.removeFutureMap(name.asLongText()+"112_2");
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, voice);

	}
	
	@POST
	@Path("/modifySystemVoice")
	public Response modifySystemVoice(@Context HttpServletRequest request,MultivaluedMap<String, String> formParams) throws IOException{
		String deviceId=(String) request.getSession().getAttribute("deviceSn");
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		SyncFuture<String> future=AddFuture.setFuture(deviceId,"112_12");
		CheckResponse response=new CheckResponse(deviceId, "112_12",future);
		response.start();
		boolean state=ServiceDistribution.handleJson112_11(deviceId,requestMap.get("voice"));
		if (state) {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		} else {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
	}
	
	@POST
	@Path("/resetWifiPassword")
	public Response resetWifiPassword(@Context HttpServletRequest request,MultivaluedMap<String, String> formParams) throws IOException{
		String deviceId=(String) request.getSession().getAttribute("deviceSn");
		SyncFuture<String> future=AddFuture.setFuture(deviceId,"113_2");
		CheckResponse response=new CheckResponse(deviceId, "113_2",future);
		response.start();
		boolean state=ServiceDistribution.handleJson113_1(deviceId);
		if (state) {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, null);
		} else {
			return ResponseFactory.response(Response.Status.OK, ResponseCode.REQUEST_FAIL, null);
		}
	}
	
	
	@POST
	@Path("/getStrangerPhoto")
	@LoginRequired
	public Response getStrangerPhoto(@Context HttpServletRequest request,MultivaluedMap<String, String> formParams) throws IOException{
		String deviceId=(String) request.getSession().getAttribute("deviceSn");
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		JSONObject result=employeeService.getStrangerPhoto(requestMap.get("departmentId"),requestMap.get("name"),requestMap.get("startTime"),requestMap.get("endTime"),requestMap.get("tag"),deviceId);
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, result);
	}
	
	@POST
	@Path("/getCollectionPhoto")
	@LoginRequired
	public Response getCollectionPhoto(@Context HttpServletRequest request,MultivaluedMap<String, String> formParams) throws IOException{
		String deviceId=(String) request.getSession().getAttribute("deviceSn");
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		JSONObject result=employeeService.getCollectionPhotoList(requestMap.get("startTime"),requestMap.get("endTime"),requestMap.get("tag"),deviceId);
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS, result);
	}
	

}
