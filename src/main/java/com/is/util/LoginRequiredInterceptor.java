package com.is.util;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.is.constant.ParameterKeys;
import com.is.constant.ResponseCode;
import com.is.gson.ResponseFactory;


/**
   @author lishuhuan 
 * @date 2016骞?3鏈?23鏃?    鐢ㄦ埛鐧诲綍楠岃瘉鎷︽埅鍣?
 */
public class LoginRequiredInterceptor implements MethodInterceptor {


	@Override
	public Object invoke(MethodInvocation methodInvocation) throws Throwable {
		if (methodInvocation.getMethod().isAnnotationPresent(
				LoginRequired.class)) {
			Object[] args = methodInvocation.getArguments();
			if (args.length != 0) {
				for (Object obj : args) {
					if (obj instanceof HttpServletRequest) {
						HttpServletRequest request = (HttpServletRequest)obj;
						if(request.getSession().getAttribute(ParameterKeys.SESSION_USER) == null){
							return ResponseFactory.response(Response.Status.OK, ResponseCode.NOT_LOGIN, null);
						}
					}
				}
			}
		}
		return methodInvocation.proceed();
		
	}
}
