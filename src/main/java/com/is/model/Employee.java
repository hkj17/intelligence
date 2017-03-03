package com.is.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity(name = "Employee")
@Table(name = "employee")
@XmlRootElement
public class Employee implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8452577535288156957L;
	
	public Employee() {
		// TODO Auto-generated constructor stub
	}

	@Id
	@Column(name = "employee_id")
	private String employeeId;
	
	@Column(name = "employee_name")
	private String employeeName;
	
	@Column(name = "sex")
	private int sex;
	
	@Column(name = "birth")
	private String birth;
	
	@Column(name = "entry_time")
	private String entryTime;
	
	@Column(name = "telphone")
	private String telphone;
	
	@Column(name = "wechat")
	private String wechat;
	
	@Column(name = "device_id")
	private String deviceId;
	
	@ManyToOne
	@JoinColumn(name = "admin_id")
	@NotFound(action=NotFoundAction.IGNORE) 
	private Admin admin;

	
	@Column(name = "content")
	private String content;
	
	@Column(name = "photo_path")
	private String photoPath;
	
	@Column(name="position")
	private String position;
	
	
	@Column(name="job_id")
	private String jobId;
	
	@Column(name="address")
	private String address;
	
	@Column(name="email")
	private String email;
	
	@Column(name="id_card")
	private String idCard;
	
	@Column(name="pingyin")
	private String pingyin;
	
	@Column(name = "work_pos")
	private String workPos;
	
	@Column(name = "isDuty")
	private int isDuty;
	
	@Column(name = "template_path")
	private String templatePath;
	
	@ManyToOne
	@JoinColumn(name="department_id")
	@NotFound(action=NotFoundAction.IGNORE)  
	private Department department;
	
	@ManyToOne
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name = "company_id")
	private Company company;
	
	public void setTemplatePath(String templatePath) {
		this.templatePath = templatePath;
	}
	
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
	public String getDeviceId() {
		return deviceId;
	}
	
	
	public int getIsDuty() {
		return isDuty;
	}
	
	public void setIsDuty(int isDuty) {
		this.isDuty = isDuty;
	}
	
	public String getTemplatePath() {
		return templatePath;
	}
	
	public String getWorkPos() {
		return workPos;
	}
	
	public void setWorkPos(String workPos) {
		this.workPos = workPos;
	}
	
	public void setDepartment(Department department) {
		this.department = department;
	}
	
	public Department getDepartment() {
		return department;
	}
	
	public void setPingyin(String pingyin) {
		this.pingyin = pingyin;
	}
	
	public String getPingyin() {
		return pingyin;
	}
	
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getIdCard() {
		return idCard;
	}
	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}
	
	public String getPhotoPath() {
		return photoPath;
	}
	public void setPhotoPath(String photoPath) {
		this.photoPath = photoPath;
	}
	public Admin getAdmin() {
		return admin;
	}
	public void setAdmin(Admin admin) {
		this.admin = admin;
	}
	public Company getCompany() {
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}
	public String getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public String getBirth() {
		return birth;
	}
	public void setBirth(String birth) {
		this.birth = birth;
	}
	public String getEntryTime() {
		return entryTime;
	}
	public void setEntryTime(String entryTime) {
		this.entryTime = entryTime;
	}
	public String getTelphone() {
		return telphone;
	}
	public void setTelphone(String telphone) {
		this.telphone = telphone;
	}
	public String getWechat() {
		return wechat;
	}
	public void setWechat(String wechat) {
		this.wechat = wechat;
	}


	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	

}
