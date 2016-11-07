package com.is.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.is.constant.ResponseCode;



@Entity(name = "Admin")
@Table(name = "admin")
public class Admin implements Serializable {
	
	@Id
	@Column(name = "admin_id")
	private String adminId;
	
	@Column(name = "device_id")
	private String deviceId;
	
	@Column(name = "username")
	private String username;
	
	@Column(name = "password")
	private String password;
	
	@Column(name = "authority")
	private int authority;
	
	@Column(name = "audit_auth")
	private int auditAuth;
	
	@Transient
	private String employeeName;
	
	@Transient
	private ResponseCode responseCode;
	
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	
	public String getEmployeeName() {
		return employeeName;
	}
	
	public int getAuditAuth() {
		return auditAuth;
	}
	
	public void setAuditAuth(int auditAuth) {
		this.auditAuth = auditAuth;
	}
	
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
	public String getDeviceId() {
		return deviceId;
	}
	
	public ResponseCode getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(ResponseCode responseCode) {
		this.responseCode = responseCode;
	}
	public String getAdminId() {
		return adminId;
	}
	public void setAdminId(String adminId) {
		this.adminId = adminId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getAuthority() {
		return authority;
	}
	public void setAuthority(int authority) {
		this.authority = authority;
	}
	
	

}
