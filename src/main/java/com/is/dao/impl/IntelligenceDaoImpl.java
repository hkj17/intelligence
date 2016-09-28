package com.is.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.is.constant.Hql;
import com.is.model.Admin;
import com.is.model.Appointment;
import com.is.model.ClockPhoto;
import com.is.model.ClockRecord;
import com.is.model.Company;
import com.is.model.Department;
import com.is.model.Employee;
import com.is.model.Message;
import com.is.model.Notification;
import com.is.model.Visitor;
import com.is.model.VisitorInfo;
import com.is.system.dao.CloudDao;
import com.is.system.dao.IntelligenceDao;



/** 
 * @author lishuhuan 
 * @date 2016年3月31日
 * 类说明 
 */
@Repository("intelligenceDao")
public class IntelligenceDaoImpl implements IntelligenceDao {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	public Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	@Autowired
	private CloudDao cloudDao;
	
	@Override
	public Admin getAdminByName(String username){
		List<Admin> list=cloudDao.findByHql(Hql.GET_USER_BY_NAME, username);
		if(list.size()>0){
			return list.get(0);
		}
		else {
			return null;
		}
	}
	
	@Override
	public List<Employee> getEmployeeList(){
		return cloudDao.findByHql(Hql.GET_EMPLOYEE_LIST);
	}
	
	@Override
	public Company getCompanyById(int id){
		return (Company) cloudDao.getByHql(Hql.GET_COMPANY_BY_ID, id);
	}
	
	@Override
	public List<Company> getCompanyList(){
		return cloudDao.findByHql(Hql.GET_COMPANY_LIST);
	}
	
	@Override
	public List<ClockRecord> getClockList(){
		Query query=getSession().createSQLQuery("SELECT a.* from clockrecord a,employee b where b.employee_id=a.employee_id");
		List<ClockRecord> list=((SQLQuery) query).addEntity(ClockRecord.class).list();
		return list;
	}
	
	@Override
	public List<ClockRecord> getClockByWhere(String department,String user,String startClock,String endClock,String rule){
		String sql="select clock.* from clockrecord clock,employee b where clock.employee_id=b.employee_id";
		Map<Integer, String> map=new HashMap<>();
		int i=0;
		if(!user.equals("") && !user.equals(null)){
			sql=sql+" and b.employee_name like ?";
			map.put(i, '%' + user + '%');
			i=i+1;
		}
		if(!startClock.equals("") && !startClock.equals(null)){
			sql+=" and clock.start_clock>=?";
			map.put(i, startClock);
			i=i+1;
		}
		if(!department.equals("") && !department.equals(null)){
			sql+=" and b.department_id=?";
			map.put(i, department);
			i=i+1;
		}
		if(!endClock.equals("") && !endClock.equals(null)){
			sql+=" and clock.end_clock<=?";
			map.put(i, endClock);
		}
		if(Integer.parseInt(rule)==1){
			sql+=" and clock.state!=0";
		}

		Query query = getSession().createSQLQuery(sql);
		for(int p=0;p<map.size();p++){
			query.setParameter(p, map.get(p));
		}
		return ((SQLQuery) query).addEntity(ClockRecord.class).list();
	}
	
	@Override
	public List<Employee> getEmployeeByWhere(String word,String company,String department){
		if(word!=null){
			Query query=getSession().createSQLQuery("select * from employee where company_id=? and (pingyin like ? or pingyin like ? or employee_name like ?)");
			query.setParameter(0, company);
			query.setParameter(1, word + "%");
			query.setParameter(2, "%" +","+word + "%");
			query.setParameter(3, "%" +"%"+word + "%");
			@SuppressWarnings("unchecked")
			List<Employee> list=((SQLQuery) query).addEntity(Employee.class).list();
			return list;
		}
		else{
			Query query=getSession().createSQLQuery("select * from employee where company_id='"+company+"' and department_id='"+department+"'");
			List<Employee> list=((SQLQuery) query).addEntity(Employee.class).list();
			return list;
		}
		
	}
	
	@Override
	public List<Notification> getNotifyList(){
		return cloudDao.findByHql(Hql.GET_NOTIFY_LIST);
	}
	
	@Override
	public List<Appointment> getAppointList(){
		Query query=getSession().createSQLQuery("SELECT a.* from appointment a,employee b where a.employee_id=b.employee_id");
		List<Appointment> list=((SQLQuery) query).addEntity(Appointment.class).list();
		return list;
	}
	
	@Override
	public List<Employee> getEmployeeByAdmin(String adminId){
		Query query=getSession().createSQLQuery("SELECT a.* from employee a,company b,admin c where a.company_id=b.company_id and b.admin_id=c.admin_id and c.admin_id="+"'"+adminId+"'");
		List<Employee> list=((SQLQuery) query).addEntity(Employee.class).list();
		return list;
	}
	
	@Override
	public Employee getEmployeeById(String id){
		return (Employee) cloudDao.getByHql(Hql.GET_EMPLOYEE_BY_ID, id);
	}
	
	@Override
	public Notification getNotifyById(int id){
		return (Notification) cloudDao.getByHql(Hql.GET_NOTIFY_BY_ID, id);
	}
	
	@Override
	public Appointment getAppointById(int id){
		return (Appointment) cloudDao.getByHql(Hql.GET_APPOINT_BY_ID, id);
	}
	
	@Override
	public List<Employee> getEmployeeByCompany(int id){
		return cloudDao.findByHql(Hql.GET_EMPLOYEE_BY_COMPANY, id);
	}
	
	@Override
	public ClockRecord getClockByMc(int id,String morningClock){
		Query query=getSession().createSQLQuery("select * from clockrecord a where a.employee_id="+"'"+id+"'" +" and a.start_clock like "+"'%"+morningClock+"%'");
		ClockRecord clockRecord=(ClockRecord) ((SQLQuery) query).addEntity(ClockRecord.class).uniqueResult();
		return clockRecord;
	}
	
	@Override
	public ClockRecord getClockByNc(int id,String nightClock){
		Query query=getSession().createSQLQuery("select * from clockrecord a where a.employee_id="+"'"+id+"'" +" and a.end_clock like "+"'%"+nightClock+"%'");
		ClockRecord clockRecord=(ClockRecord) ((SQLQuery) query).addEntity(ClockRecord.class).uniqueResult();
		return clockRecord;
	}
	
	@Override
	public List<Employee> getEmployeeByName(String name){
		return cloudDao.findByHql(Hql.GET_EMPLOYEE_BY_NAME,name);
	}
	
	@Override
	public Admin getAdminById(int id){
		Query query=getSession().createSQLQuery("select a.* from admin a,company b where b.admin_id=a.admin_id and b.company_id="+"'"+id+"'");
		Admin admin=(Admin) ((SQLQuery) query).addEntity(Admin.class).uniqueResult();
		return admin;
	}
	
	@Override
	public String getEmployeeIdByCompany(int id){
		Query query=getSession().createSQLQuery("SELECT a.employee_id from employee a,company c where a.admin_id=c.admin_id and c.company_id="+"'"+id+"'");
		String  employeeId=query.uniqueResult().toString(); 
		return employeeId;
	}
	
	@Override
	public List<ClockPhoto> getClockPhoto(){
		return cloudDao.findByHql(Hql.GET_CLOCK_PHOTO);
	}
	
	@Override
	public String getEmployeeByMobile(String mobile){
		Query query=getSession().createSQLQuery("select DISTINCT e.employee_name from employee e where e.telphone='"+mobile+"' LIMIT 1");
		String name=(String) ((SQLQuery) query).uniqueResult();
		return name;
	}
	
	@Override
	public Company getCompanyByEmployee(String id){
		Query query=getSession().createSQLQuery("SELECT a.* from company a LEFT JOIN employee b on b.company_id=a.company_id where b.employee_id='"+id+"'");
		Company company=(Company) ((SQLQuery) query).addEntity(Company.class).uniqueResult();
		return company;
	}
	
	@Override
	public List<ClockRecord> getClockByEmployee(String id){
		return cloudDao.findByHql(Hql.GET_CLOCK_BY_EMPLOYEE, id);
	}
	
	@Override
	public VisitorInfo getVisitorInfoById(String id){
		return (VisitorInfo) cloudDao.getByHql(Hql.GET_VISITORINFO_BY_ID, id);
	}
	
	@Override
	public List<Department> getDepartmentByCompany(String company){
		return cloudDao.findByHql(Hql.GET_DEPARTMENT_BY_COMPANY, company);
	}
	
	@Override
	public List<Visitor> indexVisitor(String startTime,String endTime){
		Query query=getSession().createSQLQuery("select a.*,b.employee_name as employeeName from visitor a LEFT JOIN employee b on a.employee_id=b.employee_id where a.start_time>='"+startTime+"' and a.start_time<='"+endTime+"'");
		List<Visitor> list=((SQLQuery) query).addEntity(Visitor.class).list();
		return list;
	}
	
	@Override
	public Department getDepartmentById(String id){
		return (Department) cloudDao.getByHql(Hql.GET_DEPARTMENT_BY_ID, id);
	}
	
	@Override
	public List<Message> getMessageByEmployee(String employeeId){
		return cloudDao.findByHql(Hql.GET_MESSAGE_BY_EMPLOYEE_ID, employeeId);
	}

	@Override
	public Company getCompanyByDeviceId(String deviceId) {
		return (Company) cloudDao.getByHql(Hql.GET_COMPANY_BY_DEVICE_ID, deviceId);
		// TODO Auto-generated method stub
	}
	

}
