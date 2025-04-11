package com.tfg.app.foodies.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.tfg.app.foodies.service.RestaurantService;

@RestController
public class RestaurantController {
	
	private RestaurantService restaurantService;
	
	public ResponseEntity<?> likeRestaurant(@RequestBody Long userId, Long restaurantId,
			@RequestHeader("Authorization") String token) {
		
		restaurantService.likeRestaurantUser(userId, restaurantId, token);
		
				return new ResponseEntity<>("Like done Good", HttpStatus.ACCEPTED);	
	}

}
