package com.is.rest;

import java.text.ParseException;
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
import com.is.model.Message;
import com.is.service.MessageService;
import com.is.util.BusinessHelper;
import com.is.util.ResponseFactory;

import static com.is.constant.ParameterKeys.EMPLOYEE_ID;

@Component("messageHandle")
@Path("message")
public class MessageHandle {
	
	@Autowired
	private MessageService messageService;
	
	@POST
	@Path("/getMessageByEmployee")
	public Response getMessageByEmployee(@Context HttpServletRequest request,MultivaluedMap<String, String> formParams) throws ParseException{
		Map<String, String> requestMap = BusinessHelper.changeMap(formParams);
		List<Message> list=messageService.getMessageByEmployee(requestMap.get(EMPLOYEE_ID));
		return ResponseFactory.response(Response.Status.OK, ResponseCode.SUCCESS,list);
	}

}
