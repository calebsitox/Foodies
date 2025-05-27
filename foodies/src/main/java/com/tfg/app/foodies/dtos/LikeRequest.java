package com.tfg.app.foodies.dtos;

import lombok.Data;

@Data
public class LikeRequest {

	private String userName;
	
	private Double lat;
	
	private Double lon;

	public String getUserName() {
		return userName;
	}

	public void seuserName(String userName) {
		this.userName = userName;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLon() {
		return lon;
	}

	public void setLon(Double lon) {
		this.lon = lon;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}
