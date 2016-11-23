package com.is.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.is.model.Appointment;
import com.is.model.VisitorInfo;
import com.is.system.dao.CloudDao;
import com.is.system.dao.IntelligenceDao;
import com.is.websocket.AddFuture;
import com.is.websocket.CheckResponse;
import com.is.websocket.ServiceDistribution;
import com.is.websocket.SyncFuture;

@Transactional
@Component("appointmentService")
public class AppointmentService {

	@Autowired
	private IntelligenceDao intelligenceDao;

	@Autowired
	private CloudDao cloudDao;
	
	public Boolean addAppointment(String visitorInfoId,String startTime,String endTime,
			String content,String deviceId,String employeeId){
		startTime=startTime+":00";
		Appointment appointment=new Appointment();
		VisitorInfo info=intelligenceDao.getVisitorInfoById(visitorInfoId);
		appointment.setInfo(info);
		appointment.setStartTime(startTime);
		appointment.setEndTime(endTime);
		appointment.setContent(content);
		appointment.setCreateAt(new Date());
		appointment.setCreateBy(employeeId);
		cloudDao.add(appointment);
		
		SyncFuture<String> future=AddFuture.setFuture(deviceId);
		CheckResponse response=new CheckResponse(deviceId, "106_2",future);
		response.start();
		ServiceDistribution.handleJson106_1(deviceId, employeeId, startTime, visitorInfoId, content);
		return true;
	}
	
	public List<Appointment> getAppointmentByUser(String userid){
		return intelligenceDao.getAppointmentByUser(userid);
	}
	
	public Boolean editAppointment(String appid,String startTime,String endTime,
			String content,String deviceId){
		Appointment appointment=intelligenceDao.getAppointById(Integer.parseInt(appid));
		appointment.setStartTime(startTime);
		appointment.setEndTime(endTime);
		appointment.setContent(content);
		
		cloudDao.update(appointment);
		return true;
		
	}
}
