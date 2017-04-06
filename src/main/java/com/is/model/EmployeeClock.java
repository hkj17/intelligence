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




@Entity(name = "EmployeeClock")
@Table(name = "employeeclock")
public class EmployeeClock {
	
	@Id
	@Column(name = "employee_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String employee_id;
	
	/**
	 * @return the employee_name
	 */
	public String getEmployee_name() {
		return employee_name;
	}

	/**
	 * @param employee_name the employee_name to set
	 */
	public void setEmployee_name(String employee_name) {
		this.employee_name = employee_name;
	}

	/**
	 * @return the department
	 */
	public String getDepartment() {
		return department;
	}

	/**
	 * @param department the department to set
	 */
	public void setDepartment(String department) {
		this.department = department;
	}

	/**
	 * @return the total
	 */
	public int getTotal() {
		return Total;
	}

	/**
	 * @param total the total to set
	 */
	public void setTotal(int total) {
		Total = total;
	}

	/**
	 * @return the early
	 */
	public int getEarly() {
		return Early;
	}

	/**
	 * @param early the early to set
	 */
	public void setEarly(int early) {
		Early = early;
	}

	/**
	 * @return the late
	 */
	public int getLate() {
		return Late;
	}

	/**
	 * @param late the late to set
	 */
	public void setLate(int late) {
		Late = late;
	}

	/**
	 * @return the total_days
	 */
	public String getTotal_days() {
		return Total_days;
	}

	/**
	 * @param string the total_days to set
	 */
	public void setTotal_days(String string) {
		Total_days = string;
	}

	/**
	 * @return the early_days
	 */
	public String getEarly_days() {
		return Early_days;
	}

	/**
	 * @param early_days the early_days to set
	 */
	public void setEarly_days(String early_days) {
		Early_days = early_days;
	}

	/**
	 * @return the late_days
	 */
	public String getLate_days() {
		return Late_days;
	}

	/**
	 * @param late_days the late_days to set
	 */
	public void setLate_days(String late_days) {
		Late_days = late_days;
	}

	/**
	 * @return the employee_id
	 */
	public String getEmployee_id() {
		return employee_id;
	}

	@Column(name = "employee_name")
	private String employee_name;
	
	@Column(name = "department")
	private String department;
	
	@Transient
	private int Total;
	
	@Transient
	private int Early;
	
	@Transient
	private int Late;
	
	@Transient
	private String Total_days = "";
	
	@Transient
	private String Early_days = "";
	
	@Transient
	private String Late_days = "";
}
