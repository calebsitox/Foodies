package com.tfg.app.foodies.dtos;

import lombok.Data;

@Data
public class LikeRequest {

	private Long userId;
	
	private Long restaurantId;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(Long restaurantId) {
		this.restaurantId = restaurantId;
	}
}
