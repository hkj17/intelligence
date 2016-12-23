package com.is.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;




@Entity(name = "ClockRecordSelect")
@Table(name = "clockrecord")
public class ClockRecordSelect {
	
	@Id
	@Column(name = "cr_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int crId;
	
	@Column(name = "start_clock")
	private String startClock;
	
	@Column(name = "end_clock")
	private String endClock;
	
	@Column(name = "employee_id") 
	private String employeeId;
	
	@Column(name = "state")
	private String state;
	
	private String employeeName;
	
	private String jobId;
	
	private String department;
	
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	
	public String getEmployeeName() {
		return employeeName;
	}
	
	public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public int getCrId() {
		return crId;
	}
	public void setCrId(int crId) {
		this.crId = crId;
	}
	
	

	public String getStartClock() {
		return startClock;
	}
	public void setStartClock(String startClock) {
		this.startClock = startClock;
	}
	public String getEndClock() {
		return endClock;
	}
	public void setEndClock(String endClock) {
		this.endClock = endClock;
	}
	public String getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}


	
	
	

	
	

}
