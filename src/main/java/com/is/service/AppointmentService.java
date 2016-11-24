package com.is.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
		try {
			startTime=startTime+":00";
			Appointment appointment=new Appointment();
			String id=UUID.randomUUID().toString().trim().replaceAll("-", "");
			appointment.setId(id);
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
			ServiceDistribution.handleJson106_1(deviceId, employeeId, startTime,endTime, id,visitorInfoId, content);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		
	}
	
	public List<Appointment> getAppointmentByUser(String userid){
		return intelligenceDao.getAppointmentByUser(userid);
	}
	
	public Boolean editAppointment(String appid,String startTime,String endTime,
			String content,String deviceId){
		try {
			Appointment appointment=intelligenceDao.getAppointById(appid);
			appointment.setStartTime(startTime);
			appointment.setEndTime(endTime);
			appointment.setContent(content);
			
			cloudDao.update(appointment);
			
			SyncFuture<String> future=AddFuture.setFuture(deviceId);
			CheckResponse response=new CheckResponse(deviceId, "106_12",future);
			response.start();
			ServiceDistribution.handleJson106_11(deviceId, appointment.getCreateBy(), startTime,endTime, appid,appointment.getInfo().getId(), content);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		
	}
	
	
	public Boolean deleteAppointment(String appId,String deviceId){
		try {
			Appointment appointment=intelligenceDao.getAppointById(appId);
			if(appointment!=null){
				SyncFuture<String> future=AddFuture.setFuture(deviceId);
				CheckResponse response=new CheckResponse(deviceId, "106_22",future);
				response.start();
				boolean state=ServiceDistribution.handleJson106_21(deviceId, appId);
				if(state){
					cloudDao.delete(appointment);
				}
				else {
					return false;
				}
			}
			
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}
}
