package com.is.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.is.model.Admin;
import com.is.model.Company;
import com.is.model.Department;
import com.is.model.Employee;
import com.is.system.dao.CloudDao;
import com.is.system.dao.IntelligenceDao;

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
	
	
	public Boolean addCompany(String companyName,String address,String startTime,String endTime,String adminName,String password,String name,String contact){
		Admin admin=new Admin();
		String id = UUID.randomUUID().toString().trim().replaceAll("-", "");
		admin.setAdminId(id);
		admin.setAuthority(2);
		admin.setUsername(adminName);
		admin.setPassword(password);
		Company company=new Company();
		company.setAddress(address);
		company.setAdminId(id);
		company.setCompanyName(companyName);
		company.setContact(contact);
		company.setTimeRest(startTime);
		company.setTimeWork(endTime);
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
	
	public Boolean editCompany(String id,String name,String address,String startTime,String endTime){
		Company company=new Company();
		company.setAdminId(id);
		company.setAddress(address);
		company.setCompanyId(Integer.parseInt(id));
		company.setCompanyName(name);
		company.setTimeRest(endTime);
		company.setTimeWork(startTime);
		cloudDao.update(company);
		return true;
	}
	
	public Boolean deleteCompany(String id){
		Company company=intelligenceDao.getCompanyById(Integer.parseInt(id));
		String employeeId=intelligenceDao.getEmployeeIdByCompany(Integer.parseInt(id));
		cloudDao.delete(company);
		adminService.deleteUser(employeeId);
		return true;
	}
	
	public List<Department> getDepartmentByCompany(String company){
		return intelligenceDao.getDepartmentByCompany(company);
	}

}
