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

import com.tfg.app.foodies.config.JwtService;
import com.tfg.app.foodies.dtos.GeocodeRequest;
import com.tfg.app.foodies.dtos.LikeRequest;
import com.tfg.app.foodies.entities.Restaurant;
import com.tfg.app.foodies.service.RestaurantService;

@RestController
@RequestMapping("/api")
public class RestaurantController {
	
	@Autowired
	private JwtService JwtService;


	@Autowired
	private RestaurantService restaurantService;
	

	@PostMapping("likeRestuarant")
	public ResponseEntity<?> likeRestaurant(@RequestBody LikeRequest likeRequest,
			@RequestHeader("Authorization") String token) {
		
		restaurantService.likeRestaurantUser(likeRequest);
		return new ResponseEntity<>("Like done Good", HttpStatus.ACCEPTED);
	}

	@PostMapping("/nearby")
	public List<Restaurant> getNearbyRestaurants(@RequestBody GeocodeRequest restaurantRequest ,
			@RequestHeader("Authorization") String token) {
		return restaurantService.locateResaturantByCoordinates(restaurantRequest, token);
	}

	
}
