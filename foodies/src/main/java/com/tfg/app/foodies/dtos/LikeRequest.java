package com.tfg.app.foodies.dtos;

import lombok.Data;

@Data
public class LikeRequest {

	private String userName;
	
	private String restaurantName;

	public String getUserName() {
		return userName;
	}

	public void seuserName(String userName) {
		this.userName = userName;
	}

	public String getRestaurantname() {
		return restaurantName;
	}

	public void setRestaurantId(String restaurantName) {
		this.restaurantName = restaurantName;
	}
}
