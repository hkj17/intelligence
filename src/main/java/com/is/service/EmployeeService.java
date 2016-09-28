package com.is.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.is.system.dao.CloudDao;
import com.is.system.dao.IntelligenceDao;

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
	
	@Autowired
	private CloudDao cloudDao;
	
	public String getEmployeeByMobile(String mobile){
		return intelligenceDao.getEmployeeByMobile(mobile);
	}

}
