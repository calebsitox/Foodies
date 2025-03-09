package com.tfg.app.foodies.dtos;

import lombok.Data;

@Data
public class GeocodeRequestUser {
	private double latitude;
	private double longitude;
	private long userId;

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public long getUserId() {
		return userId;
	}

	public void setuserId(long userId) {
		this.userId = userId;
	}
}
