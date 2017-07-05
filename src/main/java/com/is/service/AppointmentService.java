package com.is.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.is.dao.CloudDao;
import com.is.dao.IntelligenceDao;
import com.is.model.Appointment;
import com.is.model.VisitorInfo;
import com.is.util.CommonUtil;
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
			String id= CommonUtil.generateRandomUUID();
			appointment.setId(id);
			VisitorInfo info=intelligenceDao.getVisitorInfoById(visitorInfoId);
			appointment.setInfo(info);
			appointment.setStartTime(startTime);
			appointment.setEndTime(endTime);
			appointment.setContent(content);
			appointment.setCreateAt(new Date());
			appointment.setCreateBy(employeeId);
			cloudDao.add(appointment);
			
			SyncFuture<String> future=AddFuture.setFuture(deviceId,"106_2");
			CheckResponse response=new CheckResponse(deviceId, "106_2",future);
			response.start();
			ServiceDistribution.handleJson106_1(deviceId, employeeId, startTime,endTime, id,visitorInfoId, content);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
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
			
			SyncFuture<String> future=AddFuture.setFuture(deviceId,"106_12");
			CheckResponse response=new CheckResponse(deviceId, "106_12",future);
			response.start();
			ServiceDistribution.handleJson106_11(deviceId, appointment.getCreateBy(), startTime,endTime, appid,appointment.getInfo().getId(), content);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	
	public Boolean deleteAppointment(String appId,String deviceId){
		try {
			Appointment appointment=intelligenceDao.getAppointById(appId);
			if(appointment!=null){
				SyncFuture<String> future=AddFuture.setFuture(deviceId,"106_22");
				CheckResponse response=new CheckResponse(deviceId, "106_22",future);
				response.start();
				boolean state=ServiceDistribution.handleJson106_21(deviceId, appId,appointment.getCreateBy(),appointment.getInfo().getId());
				if(state){
					cloudDao.delete(appointment);
				}
				else {
					return false;
				}
			}
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<Appointment> getAppointmentByVisitor(String visitorId) {
		return intelligenceDao.getAppointmentByVisitor(visitorId);
	}
}
