package com.is.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.is.dao.CloudDao;
import com.is.dao.IntelligenceDao;
import com.is.model.Message;

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
