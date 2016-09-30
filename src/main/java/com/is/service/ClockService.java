package com.is.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.is.model.ClockPhoto;
import com.is.model.ClockRecord;
import com.is.model.Company;
import com.is.model.Employee;
import com.is.system.dao.CloudDao;
import com.is.system.dao.IntelligenceDao;

/** 
 * @author lishuhuan 
 * @date 2016年4月6日
 * 类说明 
 */
@SuppressWarnings("deprecation")
@Transactional
@Component("clockService")
public class ClockService {
	
	@Autowired
	private IntelligenceDao intelligenceDao;
	
	@Autowired
	private CloudDao cloudDao;
	
	
	public List<ClockRecord> getClockList() throws ParseException{
		List<ClockRecord> clockList=intelligenceDao.getClockList();
		List<ClockRecord> newClock=new ArrayList<>();
		for(int i=0;i<clockList.size();i++){
			ClockRecord clockRecord=clockList.get(i);
			if(clockRecord.getState()==null || "".equals(clockRecord.getState())){
				SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
				Company company=intelligenceDao.getCompanyByEmployee(clockRecord.getEmployeeId());
				if(null==company){
					continue;
				}
				String start=company.getTimeWork();
				String end=company.getTimeRest();
				Date startTime=sdf.parse(start);
				Date endTime=sdf.parse(end);
				
				if(clockRecord.getStartClock()==null || "".equals(clockRecord.getStartClock()) || clockRecord.getEndClock()==null || "".equals(clockRecord.getEndClock())){
					clockRecord.setState("4");
					newClock.add(clockRecord);
					continue;
				}

				
				Date startT=sdf.parse(clockRecord.getStartClock().substring(11, 19));
				Date enDate=sdf.parse(clockRecord.getEndClock().substring(11, 19));
				
				if(startT.getTime()>startTime.getTime() && enDate.getTime()<endTime.getTime()){
					clockRecord.setState("3");
					newClock.add(clockRecord);
					continue;
				}
				if(startT.getTime()>startTime.getTime()){
					clockRecord.setState("1");
					newClock.add(clockRecord);
					continue;
				}
				if(enDate.getTime()<endTime.getTime()){
					clockRecord.setState("2");
					newClock.add(clockRecord);
					continue;
				}
				else{
					clockRecord.setState("0");
					newClock.add(clockRecord);
				}
				
			}
			else{
				newClock.add(clockRecord);
			}
		}
		
		return newClock;
	}
	
	public List<ClockRecord> getClockByWhere(String department,String user,String stratClock,String endClock,String rule){
		return intelligenceDao.getClockByWhere(department,user, stratClock, endClock,rule);
	}
	
	public List<Employee> getEmployeeByCompany(String companyId){
		return intelligenceDao.getEmployeeByCompany(Integer.parseInt(companyId));
	}
	
	public Boolean addClock(String employeeId,String morningClock,String nightClock){
		ClockRecord clockRecord=new ClockRecord();
		clockRecord.setEmployeeId(employeeId);
		if(!"".equals(morningClock) && null!=morningClock){
			clockRecord.setStartClock(morningClock);
		}
		if(!"".equals(nightClock) && null!=nightClock){
			clockRecord.setEndClock(nightClock);
		}
		cloudDao.add(clockRecord);
		return true;
	}
	
	public ClockRecord getClockByMc(int id,String morningClock){
		return intelligenceDao.getClockByMc(id, morningClock);
	}
	
	
	public ClockRecord getClockByNc(int id,String nightClock){
		return intelligenceDao.getClockByNc(id, nightClock);
	}

	public Boolean updateClock(String crId,String employeeId,String morningClock,String nightClock){
		ClockRecord clockRecord=new ClockRecord();
		//Employee employee=intelligenceDao.getEmployeeById(employeeId);
		clockRecord.setEmployeeId(employeeId);
		if(!"".equals(morningClock) && null!=morningClock){
			clockRecord.setStartClock(morningClock);
		}
		if(!"".equals(nightClock) && null!=nightClock){
			clockRecord.setEndClock(nightClock);
		}
		clockRecord.setCrId(Integer.parseInt(crId));
		cloudDao.merge(clockRecord);
		return true;
	}
	
	public Boolean addClockMobile(String id,String time,String path){
		ClockPhoto clockPhoto=new ClockPhoto();
		Employee employee=intelligenceDao.getEmployeeById(id);
		clockPhoto.setEmployee(employee);
		clockPhoto.setClockTime(time);
		clockPhoto.setPhoto("./images/clock_record_photo/" + id+time.substring(0, 10)+time.substring(11).replace(":", "")+".jpg");
		cloudDao.add(clockPhoto);
		
		ClockRecord clockRecord=getClockByMc(Integer.parseInt(id), time.substring(0,10));
		 try {
	            
			 if(null==clockRecord){
					addClock(id, time, null);
					return true;
				}
				else{
					int crId=clockRecord.getCrId();
					String morningClock=clockRecord.getStartClock();			
					updateClock(String.valueOf(crId), id, morningClock, time);
					return true;
				}
			 
		} catch (Exception e) {
			return false;
		}
		
	}
	
	public List<ClockPhoto> getClockPhoto(){
		return intelligenceDao.getClockPhoto();
	}
	
	public List<ClockRecord> getClockByEmployee(String id){
		return intelligenceDao.getClockByEmployee(id);
		
	}
}
