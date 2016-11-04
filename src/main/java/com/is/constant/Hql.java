package com.is.constant;

public class Hql {
	public static final String GET_USER_BY_NAME = "from Admin a where a.username = ?";
	
	public static final String GET_EMPLOYEE_LIST = "from Employee em where em.deviceId=?";
	
	public static final String GET_COMPANY_BY_ID = "from Company a where a.companyId = ?";
	
	public static final String GET_COMPANY_LIST = "from Company c";
	
	public static final String GET_CLOCK_LIST = "SELECT a,b.employeeName,b.jobId,b.department.department from ClockRecord a,Employee b where b.employeeId=a.employeeId and a.employeeId is not null";
	
	public static final String GET_NOTIFY_LIST = "from Notification no";
	
	public static final String GET_APPOINT_LIST = "from Appointment app";
	
	public static final String GET_EMPLOYEE_BY_ID="from Employee emp where emp.employeeId=?";
	
	public static final String GET_EMPLOYEE_BY_ADMIN_ID="from Employee a where a.admin.adminId=?";
	
	public static final String GET_NOTIFY_BY_ID="from Notification nof where nof.noteId=?";
	
	public static final String GET_APPOINT_BY_ID="from Appointment appo where appo.apId=?";
	
	public static final String GET_EMPLOYEE_BY_COMPANY="from Employee e where e.admin.authority=3 and e.company.companyId=?";
	
	//public static final String GET_CLOCK_BY_MC="from ClockRecord cr where cr.employee.employeeId=? and cr.startClock like ?%";
	
	//public static final String GET_CLOCK_BY_NC="from ClockRecord cr where cr.employee.employeeId=? and cr.endClock like ?%";
	
	public static final String GET_EMPLOYEE_BY_NAME="from Employee e where e.employeeName=? and e.deviceId=?";
	
	public static final String GET_CLOCK_PHOTO="from ClockPhoto cp";
	
	public static final String GET_EMPLOYEE_BY_MOBILE="select e.employeeName from Employee e where e.telphone=? limit 1";
	
	public static final String GET_CLOCK_BY_EMPLOYEE="SELECT a,b.employeeName,b.jobId,b.department.department from ClockRecord a,Employee b where b.employeeId=a.employeeId and a.employeeId=? and a.startClock>=?";
	
	//public static final String GET_PINGYIN_BY_NAME="from Employee where pingyin like ?%";
	
	public static final String GET_VISITORINFO_BY_ID="from VisitorInfo where id=?";
	
	public static final String GET_DEPARTMENT_BY_COMPANY="from Department where companyId=?";
	
	public static final String GET_DEPARTMENT_BY_ID="from Department where id=?";
	
	public static final String GET_MESSAGE_BY_EMPLOYEE_ID="from Message where employeeId=?";
	
	public static final String GET_COMPANY_BY_DEVICE_ID="from Company a where a.deviceId = ?";
	
	public static final String GET_VISITOR_BY_ID="from Visitor where id=?";
	
	public static final String GET_DEPARTMENT_BY_GRADE="from Department a where a.companyId=? and grade=?";
	
	public static final String GET_CLOCK_APPEAL_BY_EMPLOYEE="select a,b.employeeName,b.department.department from ClockAppeal a,Employee b where a.employeeId=b.employeeId and a.employeeId=?";
	
	public static final String GET_CLOCK_APPEAL_BY_AUDIT="select a,b.employeeName,b.department.department from ClockAppeal a,Employee b where a.employeeId=b.employeeId and a.auditPersonId=?";
	
	public static final String GET_CLOCK_APPEAL_BY_ID="from ClockAppeal where id=?";
	
	public static final String GET_VISITOR_ALL="from Visitor";
	//public static final String GET_AUDIT_PERSON_LIST="select employeeId,employeeName,admin.auditAuth,admin.deviceId from Employee where admin.auditAuth=1 and admin.deviceId=?";

}
