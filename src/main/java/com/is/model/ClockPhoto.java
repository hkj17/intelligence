package com.is.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity(name = "ClockPhoto")
@Table(name = "clockphoto")
public class ClockPhoto {
	
	@Id
	@Column(name = "cp_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int cpId;
	
	@Column(name = "clock_time")
	private String clockTime;
	
	@ManyToOne
	@JoinColumn(name = "employee_id") 
	private Employee employee;
	
	@Column(name = "photo")
	private String photo;

	public int getCpId() {
		return cpId;
	}

	public void setCpId(int cpId) {
		this.cpId = cpId;
	}
	
	



	public String getClockTime() {
		return clockTime;
	}

	public void setClockTime(String clockTime) {
		this.clockTime = clockTime;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}
	
	

}
