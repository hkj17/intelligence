package com.is.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.gson.annotations.SerializedName;

@Entity(name = "Department")
@Table(name = "department")
public class Department {

	@Id
	@Column(name = "id")
	private String id;
	
	@Column(name = "parent_id")
	private String parentId;
	
	@Column(name = "company_id")
	private String companyId;
	
	@Column(name = "department")
	private String department;
	
	@Column(name = "grade")
	private int grade;
	
	@Column(name = "leading_people")
	private String leadingPeople;
	
	
	public String getLeadingPeople() {
		return leadingPeople;
	}
	
	public void setLeadingPeople(String leadingPeople) {
		this.leadingPeople = leadingPeople;
	}
	
	public int getGrade() {
		return grade;
	}
	
	public void setGrade(int grade) {
		this.grade = grade;
	}
	
	public String getParentId() {
		return parentId;
	}
	
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}
	
	
}
