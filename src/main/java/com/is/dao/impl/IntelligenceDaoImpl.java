package com.is.dao.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.is.constant.Hql;
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
import com.is.system.dao.CloudDao;
import com.is.system.dao.IntelligenceDao;
import com.is.util.Page;

/**
 * @author lishuhuan
 * @date 2016年3月31日 类说明
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
	public Admin getAdminByName(String username) {
		List<Admin> list = cloudDao.findByHql(Hql.GET_USER_BY_NAME, username);
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@Override
	public List<Employee> getEmployeeList(String deviceId) {
		return cloudDao.findByHql(Hql.GET_EMPLOYEE_LIST, deviceId);
	}

	@Override
	public Company getCompanyById(int id) {
		return (Company) cloudDao.getByHql(Hql.GET_COMPANY_BY_ID, id);
	}

	@Override
	public List<Company> getCompanyList() {
		return cloudDao.findByHql(Hql.GET_COMPANY_LIST);
	}

	@Override
	public List<ClockRecord> getClockList() {
		return cloudDao.findByHql(Hql.GET_CLOCK_LIST);
	}

	@Override
	public List<ClockRecord> getClockByWhere(String department, String user, String startClock, String endClock,
			String rule, String deviceId) {
		String sql = "select clock,b.employeeName,b.jobId,b.department.department from ClockRecord clock,Employee b where clock.employeeId=b.employeeId and b.deviceId='"
				+ deviceId + "'";
		Map<Integer, String> map = new HashMap<>();
		endClock = endClock + " 23:59:59";
		int i = 0;
		if (user != null && !"".equals(user)) {
			sql = sql + " and (b.pingyin like ? or b.pingyin like ? or b.employeeName like ?)";
			map.put(i, user + '%');
			map.put(i + 1, '%' + "," + user + '%');
			map.put(i + 2, '%' + user + '%');
			i = i + 3;
		}
		if (startClock != null && !"".equals(startClock)) {
			sql += " and clock.startClock>=?";
			map.put(i, startClock);
			i = i + 1;
		}
		if (department != null && !"".equals(department)) {
			sql += " and b.department.id=?";
			map.put(i, department);
			i = i + 1;
		}
		if (endClock != null && !"".equals(endClock)) {
			sql += " and clock.endClock<=?";
			map.put(i, endClock);
		}
		if ("1".equals(rule)) {
			sql += " and clock.state!=0";
		}

		Query query = getSession().createQuery(sql);
		for (int p = 0; p < map.size(); p++) {
			query.setParameter(p, map.get(p));
		}
		return query.list();
	}

	@Override
	public List<Employee> getEmployeeByWhere(String word, String department, String deviceId) {
		if (word != null) {
			Query query = getSession().createQuery(
					"select a from Employee a where a.department.id=? and (a.pingyin like ? or a.pingyin like ? or a.employeeName like ?) and a.deviceId=?");
			query.setParameter(0, department);
			query.setParameter(1, word + "%");
			query.setParameter(2, "%" + "," + word + "%");
			query.setParameter(3, "%" + "%" + word + "%");
			query.setParameter(4, deviceId);
			return query.list();
		} else {
			Query query = getSession().createQuery("select a from Employee a where a.department.id=? and a.deviceId=?");
			query.setParameter(0, department);
			query.setParameter(1, deviceId);
			return query.list();
		}

	}

	@Override
	public List<Notification> getNotifyList() {
		return cloudDao.findByHql(Hql.GET_NOTIFY_LIST);
	}

	@Override
	public List<Appointment> getAppointList() {
		Query query = getSession()
				.createSQLQuery("SELECT a.* from appointment a,employee b where a.employee_id=b.employee_id");
		List<Appointment> list = ((SQLQuery) query).addEntity(Appointment.class).list();
		return list;
	}

	@Override
	public Employee getEmployeeByAdmin(String adminId) {
		Employee employee = (Employee) cloudDao.getByHql(Hql.GET_EMPLOYEE_BY_ADMIN_ID, adminId);
		return employee;
	}

	@Override
	public Employee getEmployeeById(String id) {
		return (Employee) cloudDao.getByHql(Hql.GET_EMPLOYEE_BY_ID, id);
	}

	@Override
	public Notification getNotifyById(int id) {
		return (Notification) cloudDao.getByHql(Hql.GET_NOTIFY_BY_ID, id);
	}

	@Override
	public Appointment getAppointById(int id) {
		return (Appointment) cloudDao.getByHql(Hql.GET_APPOINT_BY_ID, id);
	}

	@Override
	public List<Employee> getEmployeeByCompany(int id) {
		return cloudDao.findByHql(Hql.GET_EMPLOYEE_BY_COMPANY, id);
	}

	@Override
	public ClockRecord getClockByMc(String id, String morningClock) {
		Query query = getSession().createSQLQuery("select * from clockrecord a where a.employee_id=" + "'" + id + "'"
				+ " and a.start_clock like " + "'%" + morningClock + "%'");
		ClockRecord clockRecord = (ClockRecord) ((SQLQuery) query).addEntity(ClockRecord.class).uniqueResult();
		return clockRecord;
	}

	@Override
	public ClockRecord getClockByNc(int id, String nightClock) {
		Query query = getSession().createSQLQuery("select * from clockrecord a where a.employee_id=" + "'" + id + "'"
				+ " and a.end_clock like " + "'%" + nightClock + "%'");
		ClockRecord clockRecord = (ClockRecord) ((SQLQuery) query).addEntity(ClockRecord.class).uniqueResult();
		return clockRecord;
	}

	@Override
	public List<Employee> getEmployeeByName(String name, String deviceId) {
		return cloudDao.findByHql(Hql.GET_EMPLOYEE_BY_NAME, name, deviceId);
	}

	@Override
	public Admin getAdminById(String id) {
		Query query = getSession().createSQLQuery("select a.* from admin a where a.admin_id='" + id + "'");
		Admin admin = (Admin) ((SQLQuery) query).addEntity(Admin.class).uniqueResult();
		return admin;
	}

	@Override
	public String getEmployeeIdByCompany(int id) {
		Query query = getSession().createSQLQuery(
				"SELECT a.employee_id from employee a,company c where a.admin_id=c.admin_id and c.company_id=" + "'"
						+ id + "'");
		String employeeId = query.uniqueResult().toString();
		return employeeId;
	}

	@Override
	public List<ClockTime> getClockPhoto() {
		return cloudDao.findByHql(Hql.GET_CLOCK_PHOTO);
	}

	@Override
	public String getEmployeeByMobile(String mobile) {
		Query query = getSession().createSQLQuery(
				"select DISTINCT e.employee_name from employee e where e.telphone='" + mobile + "' LIMIT 1");
		String name = (String) ((SQLQuery) query).uniqueResult();
		return name;
	}

	@Override
	public Company getCompanyByEmployee(String id) {
		Query query = getSession().createSQLQuery(
				"SELECT a.* from company a LEFT JOIN employee b on b.company_id=a.company_id where b.employee_id='" + id
						+ "'");
		Company company = (Company) ((SQLQuery) query).addEntity(Company.class).uniqueResult();
		return company;
	}

	@Override
	public List<ClockRecord> getClockByEmployee(String id) {
		Calendar now = Calendar.getInstance();
		now.set(Calendar.DATE, now.get(Calendar.DATE) - 10);
		Date startTime = now.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String start = sdf.format(startTime);
		return cloudDao.findByHql(Hql.GET_CLOCK_BY_EMPLOYEE, id, start);
	}

	@Override
	public VisitorInfo getVisitorInfoById(String id) {
		return (VisitorInfo) cloudDao.getByHql(Hql.GET_VISITORINFO_BY_ID, id);
	}

	@Override
	public List<Department> getDepartmentByCompany(String company) {
		return cloudDao.findByHql(Hql.GET_DEPARTMENT_BY_COMPANY, company);
	}

	@Override
	public List<Visitor> indexVisitor(String depaertmentId, String name, String startTime, String endTime,
			String deviceId) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		String sql = "select a from Visitor a where a.deviceId='" + deviceId + "' ";
		Map<Integer, Object> map = new HashMap<>();
		int i = 0;
		if (depaertmentId != null && !"".equals(depaertmentId)) {
			sql = sql + " and a.employee.department.id=?";
			map.put(i, depaertmentId);
			i = i + 1;
		}
		if (name != null && !"".equals(name)) {
			sql += " and (a.employee.pingyin like ? or a.employee.pingyin like ? or a.employee.employeeName like ?)";
			map.put(i, name + "%");
			map.put(i + 1, "%" + "," + name + "%");
			map.put(i + 2, "%" + name + "%");
			i = i + 3;
		}
		if (startTime != null && !"".equals(startTime)) {
			sql += " and a.startTime>=?";
			try {
				map.put(i, sdf.parse(startTime));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i = i + 1;
		}
		if (endTime != null && !"".equals(endTime)) {
			sql += " and a.startTime<=?";
			try {
				map.put(i, sdf.parse(endTime));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		Query query = getSession().createQuery(sql);
		for (int p = 0; p < map.size(); p++) {
			query.setParameter(p, map.get(p));
		}
		List<Visitor> list = query.list();
		return list;
	}

	@Override
	public List<Visitor> indexVisitorPath(String depaertmentId, String name, String startTime, String endTime,
			String deviceId, int firstResult) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		String sql = "select a.* from visitor a left join employee b on a.employee_id=b.employee_id where a.device_id='"
				+ deviceId + "' ";
		Map<Integer, Object> map = new HashMap<>();
		int i = 0;
		if (depaertmentId != null && !"".equals(depaertmentId)) {
			sql = sql + " and b.department_id=?";
			map.put(i, depaertmentId);
			i = i + 1;
		}
		if (name != null && !"".equals(name)) {
			sql += " and (b.pingyin like ? or b.pingyin like ? or b.employee_name like ?)";
			map.put(i, name + "%");
			map.put(i + 1, "%" + "," + name + "%");
			map.put(i + 2, "%" + name + "%");
			i = i + 3;
		}
		if (startTime != null && !"".equals(startTime)) {
			sql += " and a.start_time>=?";
			try {
				map.put(i, sdf.parse(startTime));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i = i + 1;
		}
		if (endTime != null && !"".equals(endTime)) {
			sql += " and a.start_time<=?";
			try {
				map.put(i, sdf.parse(endTime));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		sql += " order by a.start_time desc";
		Query query = getSession().createSQLQuery(sql);
		for (int p = 0; p < map.size(); p++) {
			query.setParameter(p, map.get(p));
		}
		query.setFirstResult(firstResult);
		query.setMaxResults(25);
		List<Visitor> list = ((SQLQuery) query).addEntity(Visitor.class).list();
		return list;
	}

	@Override
	public int indexVisitorCount(String depaertmentId, String name, String startTime, String endTime, String deviceId) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		String sql = "select count(*) from visitor a left join employee b on a.employee_id=b.employee_id where a.device_id='"
				+ deviceId + "' ";
		Map<Integer, Object> map = new HashMap<>();
		int i = 0;
		if (depaertmentId != null && !"".equals(depaertmentId)) {
			sql = sql + " and b.department_id=?";
			map.put(i, depaertmentId);
			i = i + 1;
		}
		if (name != null && !"".equals(name)) {
			sql += " and (b.pingyin like ? or b.pingyin like ? or b.employee_name like ?)";
			map.put(i, name + "%");
			map.put(i + 1, "%" + "," + name + "%");
			map.put(i + 2, "%" + name + "%");
			i = i + 3;
		}
		if (startTime != null && !"".equals(startTime)) {
			sql += " and a.start_time>=?";
			try {
				map.put(i, sdf.parse(startTime));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i = i + 1;
		}
		if (endTime != null && !"".equals(endTime)) {
			sql += " and a.start_time<=?";
			try {
				map.put(i, sdf.parse(endTime));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Query query = getSession().createSQLQuery(sql);
		for (int p = 0; p < map.size(); p++) {
			query.setParameter(p, map.get(p));
		}
		Object count = query.uniqueResult();
		return Integer.parseInt(count.toString());
	}

	@Override
	public Department getDepartmentById(String id) {
		return (Department) cloudDao.getByHql(Hql.GET_DEPARTMENT_BY_ID, id);
	}

	@Override
	public List<Message> getMessageByEmployee(String employeeId) {
		return cloudDao.findByHql(Hql.GET_MESSAGE_BY_EMPLOYEE_ID, employeeId);
	}

	@Override
	public Company getCompanyByDeviceId(String deviceId) {
		return (Company) cloudDao.getByHql(Hql.GET_COMPANY_BY_DEVICE_ID, deviceId);
		// TODO Auto-generated method stub
	}

	@Override
	public List<Admin> searchAdmin(String name, String auth, String deviceId) {
		String sql = "select a.admin,a.employeeName from Employee a where a.admin.deviceId='" + deviceId + "'";
		Map<Integer, String> map = new HashMap<>();
		int i = 0;
		if (name != null && !"".equals(name)) {
			sql = sql + " and a.admin.username like ?";
			map.put(i, '%' + name + '%');
			i = i + 1;
		}
		if (auth != null && !"".equals(auth)) {
			sql += " and a.admin.authority=?";
			map.put(i, auth);
		}

		Query query = getSession().createQuery(sql);
		for (int p = 0; p < map.size(); p++) {
			query.setParameter(p, map.get(p));
		}
		List<Admin> list = query.list();
		return list;
	}

	@Override
	public Visitor getVisitorById(String id) {
		// TODO Auto-generated method stub
		Visitor visitor = (Visitor) cloudDao.getByHql(Hql.GET_VISITOR_BY_ID, id);
		return visitor;
	}

	@Override
	public List<Employee> getAuditPersonList(String deviceId) {
		Query query = getSession().createSQLQuery(
				"select a.* from employee a LEFT JOIN admin b on b.admin_id=a.admin_id where b.device_id='" + deviceId
						+ "' and b.audit_auth=1");
		List<Employee> list = ((SQLQuery) query).addEntity(Employee.class).list();
		return list;
	}

	@Override
	public List<Department> getDepartmentByGrade(String companyId, int grade) {
		return cloudDao.findByHql(Hql.GET_DEPARTMENT_BY_GRADE, companyId, grade);
	}

	@Override
	public List<ClockAppeal> getClockTimeAppealByEmployee(String employeeId) {
		// TODO Auto-generated method stub
		return cloudDao.findByHql(Hql.GET_CLOCK_APPEAL_BY_EMPLOYEE, employeeId);
	}

	@Override
	public ClockAppeal getClockAppealById(String id) {
		// TODO Auto-generated method stub
		return (ClockAppeal) cloudDao.getByHql(Hql.GET_CLOCK_APPEAL_BY_ID, id);
	}

	@Override
	public Map<String, String> getDepartmentOrganization(String departmentId, String grade) {
		// TODO Auto-generated method stub
		String sql = null;
		if (grade.equals("1")) {
			sql = "select a.department as one from department a where a.id='" + departmentId + "'";
		} else if (grade.equals("2")) {
			sql = "select a.department as one,b.department as two from department a,department b where b.parent_id=a.id and b.id='"
					+ departmentId + "'";
		} else if (grade.equals("3")) {
			sql = "select a.department as one,b.department as two,c.department as three from department a,department b,department c where b.parent_id=a.id and c.parent_id=b.id and c.id='"
					+ departmentId + "'";
		} else if (grade.equals("4")) {
			sql = "select a.department as one,b.department as two,c.department as three,d.department as four from department a,department b,department c,department d where b.parent_id=a.id and c.parent_id=b.id and d.parent_id=c.id and d.id='"
					+ departmentId + "'";
		} else {
			sql = "select a.department as one,b.department as two,c.department as three,d.department as four,e.department as five from department a,department b,department c,department d,department e where b.parent_id=a.id and c.parent_id=b.id and d.parent_id=c.id and e.parent_id=d.id and e.id='"
					+ departmentId + "'";
		}
		Query query = getSession().createSQLQuery(sql);
		return (Map) query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).uniqueResult();
	}

	@Override
	public List<ClockAppeal> getClockAuditList(String auditId) {
		// TODO Auto-generated method stub
		return cloudDao.findByHql(Hql.GET_CLOCK_APPEAL_BY_AUDIT, auditId);
	}

	@Override
	public List<ClockTime> getDetailClock(String employeeId) {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String time = sdf.format(new Date());

		String sql = "SELECT a,b.employeeName,b.jobId,b.department.department from ClockTime a,Employee b where b.employeeId=a.employeeId and a.employeeId=? and a.clockTime like ?";
		Query query = getSession().createQuery(sql);
		query.setParameter(0, employeeId);
		query.setParameter(1, '%' + time + '%');

		List<ClockTime> list = query.list();
		return list;
	}

	@Override
	public List<Visitor> getVisitorAll() {
		return cloudDao.findByHql(Hql.GET_VISITOR_ALL);
	}

	@Override
	public List<ClockAbnormal> getHandClockList(String startTime, String endTime, String deviceId) {
		String sql = "select a,b.employeeName from ClockAbnormal a,Employee b where a.employeeId=b.employeeId and a.deviceId='"
				+ deviceId + "'";
		Map<Integer, String> map = new HashMap<>();
		int i = 0;
		if (startTime != null && !"".equals(startTime)) {
			sql += " and a.clockTime>=?";
			map.put(i, startTime);
			i = i + 1;
		}
		if (endTime != null && !"".equals(endTime)) {
			sql += " and a.clockTime<=?";
			map.put(i, endTime);
		}

		Query query = getSession().createQuery(sql);
		for (int p = 0; p < map.size(); p++) {
			query.setParameter(p, map.get(p));
		}
		return query.list();
	}

	@Override
	public ClockAbnormal getHandClockById(String id) {
		// TODO Auto-generated method stub
		return (ClockAbnormal) cloudDao.getByHql(Hql.GET_CLOCK_ABNORMAL_BY_ID, id);
	}

	@Override
	public List<VisitorInfo> getVisitorInfoByWhere(String deviceId, String name) {
		// TODO Auto-generated method stub
		if (name == null || "".equals(name)) {
			return cloudDao.findByHql(Hql.GET_VISITOR_INFO_LIST, deviceId);
		} else {
			return cloudDao.findByHql(Hql.GET_VISITOR_INFO_BY_NAME, deviceId, name);
		}
	}

	@Override
	public Page getCollectionPhotoList(String startTime, String endTime, String tag, String deviceId) {
		Page page=new Page();
		String sql = "select * from collection_photo where device_id='"
				+ deviceId + "' ";
		Map<Integer, Object> map = new HashMap<>();
		int i = 0;
		if (startTime != null && !"".equals(startTime)) {
			sql += " and time>=?";
			map.put(i, startTime);
			i = i + 1;
		}
		if (endTime != null && !"".equals(endTime)) {
			endTime=endTime+" 23:59:59";
			sql += " and time<=?";
			map.put(i, endTime);
		}
		sql += " order by time desc";
		Query query = getSession().createSQLQuery(sql);
		for (int p = 0; p < map.size(); p++) {
			query.setParameter(p, map.get(p));
		}
		int count=query.list().size();
		page.setPageTotal(count/25+1);
		
		query.setFirstResult((Integer.parseInt(tag)-1)*25);
		query.setMaxResults(25);
		List<CollectionPhoto> list = ((SQLQuery) query).addEntity(CollectionPhoto.class).list();
		page.setList(list);
		return page;
	}

	@Override
	public CollectionPhoto getCollectionPhotoById(String id) {
		// TODO Auto-generated method stub
		return (CollectionPhoto) cloudDao.getByHql(Hql.GET_COLLECTION_PHOTO_BY_ID, id);
	}

}
