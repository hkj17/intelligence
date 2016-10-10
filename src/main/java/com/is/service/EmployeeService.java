package com.is.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.is.model.Employee;
import com.is.system.dao.CloudDao;
import com.is.system.dao.IntelligenceDao;
import com.is.util.JavaSms;

/** 
 * @author lishuhuan 
 * @date 2016年9月5日
 * 类说明 
 */
@Transactional
@Component("employeeService")
public class EmployeeService {
	
	@Autowired
	private IntelligenceDao intelligenceDao;
	
	private static String ENCODING = "UTF-8";
	
	@Autowired
	private CloudDao cloudDao;
	
	public String getEmployeeByMobile(String mobile){
		return intelligenceDao.getEmployeeByMobile(mobile);
	}
	
	public String sendMsg(String employeeId){
		String result=null;
		Employee employee=intelligenceDao.getEmployeeById(employeeId);
		if(null==employee.getEmployeeName()){
			result="no user!";
		}
		String tpl_value;
		try {
			tpl_value = URLEncoder.encode("#name#",ENCODING) +"="
			        + URLEncoder.encode(employee.getEmployeeName(), ENCODING);
			result=JavaSms.tplSendSms(tpl_value, employee.getTelphone());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

}
