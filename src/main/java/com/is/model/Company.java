package com.is.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity(name = "Company")
@Table(name = "company")
public class Company {
	
	@Id
	@Column(name = "company_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int companyId;
	
	@Column(name = "device_id")
	private String deviceId;
	
	@Column(name = "company_name")
	private String companyName;
	
	@Column(name = "address")
	private String address;
	
	@Column(name = "contact")
	private String contact;
	
	@Column(name = "admin_id")
	private String adminId;
	
	@Column(name = "morning_time_start")
	private String morningTimeStart;
	
	@Column(name = "morning_time_end")
	private String morningTimeEnd;
	
	@Column(name = "night_time_start")
	private String nightTimeStart;
	
	@Column(name = "night_time_end")
	private String nightTimeEnd;
	
	
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
	public String getDeviceId() {
		return deviceId;
	}
	
	public int getCompanyId() {
		return companyId;
	}
	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	
	public String getAdminId() {
		return adminId;
	}
	
	public void setAdminId(String adminId) {
		this.adminId = adminId;
	}

	public String getMorningTimeStart() {
		return morningTimeStart;
	}

	public void setMorningTimeStart(String morningTimeStart) {
		this.morningTimeStart = morningTimeStart;
	}

	public String getMorningTimeEnd() {
		return morningTimeEnd;
	}

	public void setMorningTimeEnd(String morningTimeEnd) {
		this.morningTimeEnd = morningTimeEnd;
	}

	public String getNightTimeStart() {
		return nightTimeStart;
	}

	public void setNightTimeStart(String nightTimeStart) {
		this.nightTimeStart = nightTimeStart;
	}

	public String getNightTimeEnd() {
		return nightTimeEnd;
	}

	public void setNightTimeEnd(String nightTimeEnd) {
		this.nightTimeEnd = nightTimeEnd;
	}
	
	
	

}
