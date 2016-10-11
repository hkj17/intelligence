package com.is.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity(name = "Visitor")
@Table(name = "visitor")
public class Visitor implements CloudEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7684930970802656164L;

	@Id
	@Column(name = "id")
	private String id;
	
	@JoinColumn(name = "visitor_id")
	@ManyToOne
	@NotFound(action=NotFoundAction.IGNORE)  
	private VisitorInfo visitorInfo;
	
	@Column(name = "start_time")
	private Date startTime;
	
	@Column(name = "end_time")
	private Date endTime;
	
	@Column(name = "employee_id")
	private String employeeId;
	
	@Column(name = "device_id")
	private String deviceId;
	
	@Transient
	private String employeeName;
	
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
	public String getDeviceId() {
		return deviceId;
	}
	
	
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	
	public String getEmployeeName() {
		return employeeName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setVisitorInfo(VisitorInfo visitorInfo) {
		this.visitorInfo = visitorInfo;
	}
	
	public VisitorInfo getVisitorInfo() {
		return visitorInfo;
	}
	

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}


	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}
	
	public String getEmployeeId() {
		return employeeId;
	}
	
	
}
