package com.is.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.is.dao.CloudDao;
import com.is.dao.IntelligenceDao;
import com.is.model.Appointment;
import com.is.model.Employee;
import com.is.model.Notification;

/** 
 * @author lishuhuan 
 * @date 2016年4月7日
 * 类说明 
 */
@Transactional
@Component("notificationService")
public class NotificationService {
	
	@Autowired
	private IntelligenceDao intelligenceDao;
	
	@Autowired
	private CloudDao cloudDao;
	
	public Boolean addNews(String name,String title,String text){
		Notification notification=new Notification();
		notification.setNoteAuthor(name);
		notification.setNoteTitle(title);
		notification.setNoteText(text);
		 SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:MM:ss");
		notification.setNoteTime(df.format(new Date()));
		cloudDao.add(notification);
		return true;
	}
	
	public List<Notification> getNotifyList(){
		return intelligenceDao.getNotifyList();
	}
	
	public List<Appointment> getAppointList(){
		return intelligenceDao.getAppointList();
	}
	
	public Employee getEmployeeByAdmin(String adminId){
		return intelligenceDao.getEmployeeByAdmin(adminId);
	}
	
	
	public Boolean deleteNews(String id){
		Notification notification=intelligenceDao.getNotifyById(Integer.parseInt(id));
		cloudDao.delete(notification);
		return true;
	}
	
	public Boolean deleteAppoint(String id){
		Appointment appointment=intelligenceDao.getAppointById(id);
		cloudDao.delete(appointment);
		return true;
	}

}
