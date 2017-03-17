package com.is.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "VisitorInfo")
@Table(name = "visitor_info")
public class VisitorInfo {
	
	@Id
	@Column(name = "id")
	private String id;
	
	@Column(name = "company_id")
	private String companyId;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "company")
	private String company;
	
	@Column(name = "position")
	private String position;
	
	@Column(name = "telphone")
	private String telphone;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "company_url")
	private String companyUrl;
	
	@Column(name = "photo_path")
	private String photoPath;
	
	@Column(name = "importance")
	private int importance;
	
	@Column(name = "birth")
	private String birth;
	
	@Column(name = "template_path")
	private String templatePath;
	
	@Column(name = "visitor_fold")
	private String visitorFold;
	
	public void setVisitorFold(String visitorFold) {
		this.visitorFold = visitorFold;
	}
	
	public String getVisitorFold() {
		return visitorFold;
	}
	
	public String getTemplatePath() {
		return templatePath;
	}
	
	public void setTemplatePath(String templatePath) {
		this.templatePath = templatePath;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	
	public String getCompanyId() {
		return companyId;
	}

	public int getImportance() {
		return importance;
	}

	public void setImportance(int importance) {
		this.importance = importance;
	}

	public String getBirth() {
		return birth;
	}

	public void setBirth(String birth) {
		this.birth = birth;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getTelphone() {
		return telphone;
	}

	public void setTelphone(String telphone) {
		this.telphone = telphone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCompanyUrl() {
		return companyUrl;
	}

	public void setCompanyUrl(String companyUrl) {
		this.companyUrl = companyUrl;
	}

	public String getPhotoPath() {
		return photoPath;
	}

	public void setPhotoPath(String photoPath) {
		this.photoPath = photoPath;
	}
	
	

}
