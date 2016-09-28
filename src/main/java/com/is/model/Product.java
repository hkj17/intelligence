package com.is.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity(name = "Product")
@Table(name = "product")
public class Product {
	
	
	@Id
	@Column(name = "product_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int productId;
	
	@Column(name = "company_name")
	private String companyName;
	
	@Column(name = "adminname")
	private String adminname;
	
	@Column(name = "update_time")
	private Date updateTime;
	
	@Column(name = "transport_time")
	private Date transportTime;
	
	
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getAdminname() {
		return adminname;
	}
	public void setAdminname(String adminname) {
		this.adminname = adminname;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public Date getTransportTime() {
		return transportTime;
	}
	public void setTransportTime(Date transportTime) {
		this.transportTime = transportTime;
	}
	
	

}
