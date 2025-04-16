package com.tfg.app.foodies.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tfg.app.foodies.dtos.GeocodeRequest;
import com.tfg.app.foodies.entities.Restaurant;
import com.tfg.app.foodies.service.RestaurantService;

@RestController
@RequestMapping("/api")
public class RestaurantController {

	@Autowired
	private RestaurantService restaurantService;

	@PostMapping("likeRestuarant")
	public ResponseEntity<?> likeRestaurant(@RequestBody Long userId, Long restaurantId,
			@RequestHeader("Authorization") String token) {
		restaurantService.likeRestaurantUser(userId, restaurantId, token);
		return new ResponseEntity<>("Like done Good", HttpStatus.ACCEPTED);
	}

	@GetMapping("/nearby")
	public List<Restaurant> getNearbyRestaurants(@RequestBody GeocodeRequest geocodeRequest,
			@RequestHeader("Authorization") String token) {
		return restaurantService.locateResaturantByCoordinates(geocodeRequest, token);
	}

}
