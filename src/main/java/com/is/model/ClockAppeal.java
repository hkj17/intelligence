package com.is.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "ClockAppeal")
@Table(name = "clock_appeal")
public class ClockAppeal {
	
	@Id
	@Column(name = "id")
	private String id;
	
	@Column(name = "employee_id")
	private String employeeId;
	
	@Column(name = "device_id")
	private String deviceId;
	
	@Column(name = "first_clock")
	private String firstClock;
	
	@Column(name = "last_clock")
	private String lastClock;
	
	@Column(name = "appeal_reason")
	private String appealReason;
	
	@Column(name = "appeal_content")
	private String appealContent;
	
	@Column(name = "audit_person_id")
	private String auditPersonId;
	
	@Column(name = "appeal_time")
	private String appealTime;
	
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
	public String getDeviceId() {
		return deviceId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getFirstClock() {
		return firstClock;
	}

	public void setFirstClock(String firstClock) {
		this.firstClock = firstClock;
	}

	public String getLastClock() {
		return lastClock;
	}

	public void setLastClock(String lastClock) {
		this.lastClock = lastClock;
	}

	public String getAppealReason() {
		return appealReason;
	}

	public void setAppealReason(String appealReason) {
		this.appealReason = appealReason;
	}

	public String getAppealContent() {
		return appealContent;
	}

	public void setAppealContent(String appealContent) {
		this.appealContent = appealContent;
	}

	public String getAuditPersonId() {
		return auditPersonId;
	}

	public void setAuditPersonId(String auditPersonId) {
		this.auditPersonId = auditPersonId;
	}

	public String getAppealTime() {
		return appealTime;
	}

	public void setAppealTime(String appealTime) {
		this.appealTime = appealTime;
	}
	
	
	

}
