package com.is.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.is.model.Message;
import com.is.system.dao.CloudDao;
import com.is.system.dao.IntelligenceDao;

@Transactional
@Component("messageService")
public class MessageService {
	
	@Autowired
	private IntelligenceDao intelligenceDao;
	
	@Autowired
	private CloudDao cloudDao;
	
	public List<Message> getMessageByEmployee(String employeeId){
		return intelligenceDao.getMessageByEmployee(employeeId);
	}

}
