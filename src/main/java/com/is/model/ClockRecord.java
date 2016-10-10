package com.is.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;




@Entity(name = "ClockRecord")
@Table(name = "clockrecord")
public class ClockRecord {
	
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
	
	@Transient
	private String name;
	
	@Transient
	private String jobId;
	
	@Transient
	private String department;
	
	
	

	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
