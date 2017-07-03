package com.is.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.is.map.DeviceService;
import com.is.map.FutureMap;
import com.is.model.Admin;
import com.is.model.Company;
import com.is.model.Department;
import com.is.model.Employee;
import com.is.system.dao.CloudDao;
import com.is.system.dao.IntelligenceDao;
import com.is.util.CommonUtil;
import com.is.util.PasswordUtil;
import com.is.websocket.AddFuture;
import com.is.websocket.CheckResponse;
import com.is.websocket.ServiceDistribution;
import com.is.websocket.SyncFuture;

import io.netty.channel.ChannelHandlerContext;

/** 
 * @author lishuhuan 
 * @date 2016年4月5日
 * 类说明 
 */
@Transactional
@Component("companyService")
public class CompanyService {
	
	@Autowired
	private IntelligenceDao intelligenceDao;
	
	@Autowired
	private CloudDao cloudDao;
	
	@Autowired
	private AdminService adminService;
	
	
	public Boolean addCompany(String companyName,String address,
			String morningTimeStart,String morningTimeEnd,String nightTimeStart,String nightTimeEnd,
			String adminName,String password,String name,String contact){
		Admin admin=new Admin();
		String id = CommonUtil.generateRandomUUID();
		admin.setAdminId(id);
		admin.setAuthority(2);
		admin.setUsername(adminName);
		admin.setPassword(PasswordUtil.generatePassword(password));
		Company company=new Company();
		company.setAddress(address);
		company.setAdminId(id);
		company.setCompanyName(companyName);
		company.setContact(contact);
		company.setMorningTimeStart(morningTimeStart);
		company.setMorningTimeEnd(morningTimeEnd);
		company.setNightTimeStart(nightTimeStart);
		company.setNightTimeEnd(nightTimeEnd);
		Employee employee=new Employee();
		employee.setAdmin(admin);
		employee.setCompany(company);
		employee.setEmployeeName(name);
		employee.setTelphone(contact);
		cloudDao.add(admin);
		cloudDao.add(company);
		cloudDao.add(employee);
		return true;
	}
	
	public Company getCompanyInfo(String deviceId){
		return intelligenceDao.getCompanyByDeviceId(deviceId);
	}
	
	public Boolean editCompany(String name,String address,String phone,String morningTimeStart,String morningTimeEnd,String nightTimeStart,String nightTimeEnd,String deviceId) throws InterruptedException, ExecutionException, TimeoutException{
			Company company=intelligenceDao.getCompanyByDeviceId(deviceId);
			if(company==null){
				Company companyNew=new Company();
				//companyNew.setDeviceId(deviceId);
				companyNew.setAddress(address);
				companyNew.setCompanyName(name);
				companyNew.setContact(phone);
				companyNew.setMorningTimeStart(morningTimeStart);
				companyNew.setMorningTimeEnd(morningTimeEnd);
				companyNew.setNightTimeStart(nightTimeStart);
				companyNew.setNightTimeEnd(nightTimeEnd);
				cloudDao.add(companyNew);
			}
			else{
				company.setAddress(address);
				company.setCompanyName(name);
				company.setContact(phone);
				company.setMorningTimeStart(morningTimeStart);
				company.setMorningTimeEnd(morningTimeEnd);
				company.setNightTimeStart(nightTimeStart);
				company.setNightTimeEnd(nightTimeEnd);
				cloudDao.update(company);
			}
			
			List<String> list=intelligenceDao.getDeviceListAll(deviceId);
			if(list!=null){
				for(int i=0;i<list.size();i++){
					ServiceDistribution.handleJson115_1(list.get(i), name, address, phone, morningTimeStart,morningTimeEnd,nightTimeStart,nightTimeEnd,i+1);
				}
			}
			return true;
		
	}
	
	
	
	public Boolean deleteCompany(String device,String id){
		Company company=intelligenceDao.getCompanyById(Integer.parseInt(id));
		String employeeId=intelligenceDao.getEmployeeIdByCompany(Integer.parseInt(id));
		cloudDao.delete(company);
		try {
			adminService.deleteUser(device,employeeId);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public List<Department> getDepartmentByCompany(String company){
		return intelligenceDao.getDepartmentByCompany(company);
	}
	
	public List<Department> getDepartmentByGrade(String companyId,String grade){
		return intelligenceDao.getDepartmentByGrade(companyId, Integer.parseInt(grade));
	}
	
	public Map<String, String> getDepartmentOrganization(String departmentId,String grade){
		return intelligenceDao.getDepartmentOrganization(departmentId,grade);
	}
	
	public Boolean insertDepartment(String name,String people,String grade,String parentId,String companyId){
		Department department=new Department();
		String id = CommonUtil.generateRandomUUID();
		department.setId(id);
		department.setCompanyId(companyId);
		department.setDepartment(name);
		department.setGrade(Integer.parseInt(grade));
		department.setLeadingPeople(people);
		department.setParentId(parentId);
		cloudDao.add(department);
		return true;
	}
	
	public Boolean editDepartment(String name,String people,String grade,String parentId,String departmentId){
		Department department=intelligenceDao.getDepartmentById(departmentId);
		department.setDepartment(name);
		department.setGrade(Integer.parseInt(grade));
		department.setLeadingPeople(people);
		department.setParentId(parentId);
		cloudDao.update(department);
		return true;
	}
	
	public Boolean deleteDepartment(String id){
		Department department=intelligenceDao.getDepartmentById(id);
		cloudDao.delete(department);
		return true;
	}

}
