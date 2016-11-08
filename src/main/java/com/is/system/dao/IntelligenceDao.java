package com.is.system.dao;

import java.util.List;
import java.util.Map;

import com.is.model.Admin;
import com.is.model.Appointment;
import com.is.model.ClockAbnormal;
import com.is.model.ClockAppeal;
import com.is.model.ClockTime;
import com.is.model.CollectionPhoto;
import com.is.model.ClockRecord;
import com.is.model.Company;
import com.is.model.Department;
import com.is.model.Employee;
import com.is.model.Message;
import com.is.model.Notification;
import com.is.model.Visitor;
import com.is.model.VisitorInfo;
import com.is.util.Page;

import net.sf.json.JSONObject;

public interface IntelligenceDao {
	
	public Admin getAdminByName(String username);
	
	public List<Employee> getEmployeeList(String deviceId);
	
	public Company getCompanyById(int id);
	
	public List<Company> getCompanyList();
	
	public List<ClockRecord> getClockList();
	
	public List<ClockRecord> getClockByWhere(String department,String user,String stratClock,String endClock,String rule,String deviceId);
	
	public List<Notification> getNotifyList();
	
	public List<Appointment> getAppointList();
	
	public Employee getEmployeeByAdmin(String adminId);
	
	public Employee getEmployeeById(String id);
	
	public CollectionPhoto getCollectionPhotoById(String id);
	
	public List<Visitor> indexVisitorPath(String depaertmentId,String name,String startTime,
			String endTime,String deviceId,int first);
	
	public Page getCollectionPhotoList(String startTime,String endTime,String tag,String deviceId);
	
	public int indexVisitorCount(String departmentId, String name, String startTime,String endTime,String deviceId);
	
	public Notification getNotifyById(int id);
	
	public Appointment getAppointById(int id);
	
	public List<Employee> getEmployeeByCompany(int id);
	
	public ClockRecord getClockByMc(String id,String morningClock);
	
	public ClockRecord getClockByNc(int id,String nightClock);
	
	public List<ClockAppeal> getClockTimeAppealByEmployee(String employeeId);
	
	public List<ClockAppeal> getClockAuditList(String auditId);
	
	public ClockAppeal getClockAppealById(String id);
	
	public ClockAbnormal getHandClockById(String id);
	
	public Admin getAdminById(String id);
	
	public String getEmployeeIdByCompany(int id);
	
	public List<ClockTime> getClockPhoto();
	
	public List<Employee> getEmployeeByName(String name,String deviceId);
	
	public String getEmployeeByMobile(String mobile);
	
	public Company getCompanyByEmployee(String id);
	
	public List<ClockRecord> getClockByEmployee(String id);
	
	public List<ClockTime> getDetailClock(String employeeId);
	
	public List<ClockAbnormal> getHandClockList(String startTime,String endTime,String deviceId);
	
	public List<Employee> getEmployeeByWhere(String word,String department,String deviceId);
	
	public VisitorInfo getVisitorInfoById(String id);

	public List<Department> getDepartmentByCompany(String company);
	
	public List<Visitor> indexVisitor(String depaertmentId,String name,String startTime,String endTime,String deviceId);
	
	public Department getDepartmentById(String id);
	
	public List<Message> getMessageByEmployee(String employeeId);
	
	public Company getCompanyByDeviceId(String deviceId);
	
	
	public List<Admin> searchAdmin(String name,String auth,String deviceId); 
	
	public Visitor getVisitorById(String id);
	
	public List<VisitorInfo> getVisitorInfoByWhere(String deviceId,String name);
	
	public List<Employee> getAuditPersonList(String deviceId);
	
	public List<Department> getDepartmentByGrade(String companyId,int grade);
	
	public Map<String, String> getDepartmentOrganization(String departmentId,String grade);
	
	public List<Visitor> getVisitorAll();
	
}
