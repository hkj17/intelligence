package com.is.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "CollectionPhoto")
@Table(name = "collection_photo")
public class CollectionPhoto {

	@Id
	@Column(name = "id")
	private String id;
	
	@Column(name = "photo")
	private String photo;
	
	@Column(name = "time")
	private String time;
	
	@Column(name = "stranger_id")
	private String strangerId;
	
	@Column(name = "device_id")
	private String deviceId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getStrangerId() {
		return strangerId;
	}

	public void setStrangerId(String strangerId) {
		this.strangerId = strangerId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
	
}
