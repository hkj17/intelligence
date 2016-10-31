package com.is.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.is.model.Employee;
import com.is.model.Visitor;
import com.is.model.VisitorInfo;
import com.is.system.dao.CloudDao;
import com.is.system.dao.IntelligenceDao;
import com.is.websocket.AddFuture;
import com.is.websocket.CheckResponse;
import com.is.websocket.HttpServerInboundHandler;
import com.is.websocket.ServiceDistribution;
import com.is.websocket.SyncFuture;

@Transactional
@Component("visitorService")
public class VisitorService {

	private static Logger logger = Logger.getLogger(VisitorService.class);
	@Autowired
	private IntelligenceDao intelligenceDao;

	@Autowired
	private CloudDao cloudDao;
	
	
	public String addVisitorInfo(String name,String company,String position,
			String telphone,String email,String companyUrl,
			String deviceId,String importance,String birth,String path){
		VisitorInfo info=new VisitorInfo();
		String id = UUID.randomUUID().toString().trim().replaceAll("-", "");
		info.setId(id);
		info.setDeviceId(deviceId);
		info.setName(name);
		info.setCompany(company);
		info.setPosition(position);
		info.setTelphone(telphone);
		info.setEmail(email);
		info.setCompanyUrl(companyUrl);
		info.setImportance(Integer.parseInt(importance));
		info.setBirth(birth);
		info.setPhotoPath(path);
		cloudDao.add(info);
		String strangerId=path==null?null:path.substring(path.lastIndexOf("/")+1,path.lastIndexOf("."));
		SyncFuture<String> future=AddFuture.setFuture(deviceId);
		CheckResponse response=new CheckResponse(deviceId, "103_12",future);
		response.start();
		ServiceDistribution.handleJson103_11(id, strangerId, name, company,position,birth, deviceId);
		return id;
	}
	
	public boolean updateVisitorInfo(String deviceId,String id,String name,String company,String position,
			String telphone,String email,String importance,String birth){
		VisitorInfo visitorInfo=intelligenceDao.getVisitorInfoById(id);
		visitorInfo.setName(name);
		visitorInfo.setCompany(company);
		visitorInfo.setPosition(position);
		visitorInfo.setTelphone(telphone);
		visitorInfo.setEmail(email);
		visitorInfo.setImportance(Integer.parseInt(importance));
		visitorInfo.setBirth(birth);
		cloudDao.update(visitorInfo);
		SyncFuture<String> future=AddFuture.setFuture(deviceId);
		CheckResponse response=new CheckResponse(deviceId, "104_12",future);
		response.start();
		ServiceDistribution.handleJson104_11(deviceId, id, name, company, position, telphone, email, importance, birth);	
		return true;
		
	}
	
	
	public List<Visitor> indexVisitor(String depaertmentId,String name,String startTime,String endTime,String deviceId){
		return intelligenceDao.indexVisitor(depaertmentId,name,startTime, endTime,deviceId);
	}
	
	public VisitorInfo getVisitorById(String id){
		return intelligenceDao.getVisitorInfoById(id);
	}
	
	public Boolean addVisitorLeaveTime(String time,String id) throws ParseException{
		Visitor visitor=intelligenceDao.getVisitorById(id);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		visitor.setEndTime(formatter.parse(time));
		cloudDao.update(visitor);
		return true;
	}
	
	public Boolean deleteVisitorInfo(String deviceId,String visitorId){
		VisitorInfo visitorInfo=intelligenceDao.getVisitorInfoById(visitorId);
		if(visitorInfo!=null){
			cloudDao.delete(visitorInfo);
		}
		SyncFuture<String> future=AddFuture.setFuture(deviceId);
		CheckResponse response=new CheckResponse(deviceId, "105_12",future);
		response.start();
		boolean state=ServiceDistribution.handleJson105_11(deviceId,visitorId);
		return state;
	}
	
	public String updateVisitorInfoByRecord(String name,String company,String position,
			String telphone,String email,String companyUrl,
			String deviceId,String importance,String birth,String visitorId){
		Visitor visitor=intelligenceDao.getVisitorById(visitorId);
		String path=visitor.getPhoto();
		VisitorInfo info=new VisitorInfo();
		String id = UUID.randomUUID().toString().trim().replaceAll("-", "");
		info.setId(id);
		info.setDeviceId(deviceId);
		info.setName(name);
		info.setCompany(company);
		info.setPosition(position);
		info.setTelphone(telphone);
		info.setEmail(email);
		info.setCompanyUrl(companyUrl);
		info.setImportance(Integer.parseInt(importance));
		info.setBirth(birth);
		info.setPhotoPath(path);
		cloudDao.add(info);
		
		visitor.setVisitorInfo(info);
		cloudDao.update(visitor);
		
		String strangerId=path==null?null:path.substring(path.lastIndexOf("/")+1,path.lastIndexOf("."));
		SyncFuture<String> future=AddFuture.setFuture(deviceId);
		CheckResponse response=new CheckResponse(deviceId, "103_12",future);
		response.start();
		ServiceDistribution.handleJson103_11(id, strangerId, name, company,position,birth, deviceId);
		return id;
		
	}
	
	public Boolean deleteVisitorRecord(String id){
		Visitor visitor=intelligenceDao.getVisitorById(id);
		if(visitor!=null){
			cloudDao.delete(visitor);
		}
		return true;
	}
	
	public void updateVisitorTemplate(String id,String path){
		VisitorInfo visitorInfo=intelligenceDao.getVisitorInfoById(id);
		if(visitorInfo!=null){
			visitorInfo.setTemplatePath(path);
			cloudDao.add(visitorInfo);
		}
	}
	
	public void insertVisitor(String deviceId,String infoId,String time,String employeeId,String path){
		Visitor visitor=new Visitor();
		String id = UUID.randomUUID().toString().trim().replaceAll("-", "");
		visitor.setId(id);
		if(infoId!=null){
			VisitorInfo visitorInfo=intelligenceDao.getVisitorInfoById(infoId);
			visitor.setVisitorInfo(visitorInfo);
			visitor.setPhoto(visitorInfo.getPhotoPath());
		}
		if(path!=null){
			visitor.setPhoto(path);
		}
		if(employeeId!=null){
			Employee employee=intelligenceDao.getEmployeeById(employeeId);
			visitor.setEmployee(employee);
		}
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		logger.info(time);
		try {
			if(time!=null){
				visitor.setStartTime(formatter.parse(time));
			}
			else{
				visitor.setStartTime(new Date());
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		visitor.setDeviceId(deviceId);
		cloudDao.add(visitor);
	}

}
