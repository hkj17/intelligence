package com.is.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "Template")
@Table(name = "template")
public class Template implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2316830812074891054L;

	@Id
	@Column(name = "template_id")
	private String templateId;
	
	@Column(name = "employee_id")
	private String employeeId;

	@Column(name = "photo_path")
	private String photoPath;
	
	@Column(name = "template_path")
	private String templatePath;
	
	@Column(name = "created_at")
	private Timestamp createdAt;
	
	@Column(name = "created_by")
	private String createdBy;
	
	public String getTemplateId(){
		return templateId;
	}
	
	public void setTemplateId(String templateId){
		this.templateId = templateId;
	}
	
	public String getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}
	
	public String getPhotoPath() {
		return photoPath;
	}
	
	public void setPhotoPath(String photoPath) {
		this.photoPath = photoPath;
	}
	
	public String getTemplatePath(){
		return templatePath;
	}
	
	public void setTemplatePath(String templatePath){
		this.templatePath = templatePath;
	}
	
	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
}
