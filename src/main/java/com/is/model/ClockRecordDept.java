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

@Entity(name = "ClockRecordDept")
@Table(name = "clockrecorddept")
public class ClockRecordDept {
	
	@Id
	@Column(name = "employee_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String employee_id;
	
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
	
	public String employee_id() {
		return employee_id;
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

	
}
