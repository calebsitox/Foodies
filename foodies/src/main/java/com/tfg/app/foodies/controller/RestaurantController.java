package com.tfg.app.foodies.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
	private JwtService jwtService;

	@Autowired
	private RestaurantService restaurantService;

	@PostMapping("likeRestuarant")
	public ResponseEntity<?> likeRestaurant(@RequestBody LikeRequest likeRequest,
			@RequestHeader("Authorization") String token) {

		restaurantService.likeRestaurantUser(likeRequest);
		return new ResponseEntity<>("Like done Good", HttpStatus.ACCEPTED);
	}

	@PostMapping("/nearby")
	public List<Restaurant> getNearbyRestaurants(@RequestBody GeocodeRequest restaurantRequest,
			@RequestHeader("Authorization") String token) {
		return restaurantService.locateResaturantByCoordinates(restaurantRequest, token);
	}

	@PostMapping("/nearby/type")
	public List<Restaurant> getNearbyRestaurants(@RequestHeader String type,
			@RequestBody GeocodeRequest restaurantRequest, @RequestHeader("Authorization") String token) {
		return restaurantService.locateResaturantByCoordinatesAndTypes(type, restaurantRequest, token);
	}

	@GetMapping("/likedRestaurant")
	public ResponseEntity<List<Map<?, ?>>> getlikedRestaurantbyUser(@RequestParam String username,
			@RequestHeader("Authorization") String token) {

		Boolean validate = jwtService.validateToken1(token);

		if (Boolean.FALSE.equals(validate)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null); // ✅ Mejor que devolver null
		}

		List<Restaurant> likedRestaurants = restaurantService.filterByLikedrestaurants(username);
		List<Map<?, ?>> placesList = new ArrayList<>();
		for (Restaurant result : likedRestaurants) {
			Map<String, Object> placeMap = new HashMap<>();

			// Populate the map with restaurant details
			placeMap.put("name", result.getName());
			placeMap.put("address", result.getAddress());
			placeMap.put("latitude", result.getLatitude().toString());
			placeMap.put("longitude", result.getLongitude().toString());
			placeMap.put("photoReference", result.getPhotoReference());
			placeMap.put("rating", result.getRating().toString());
			placeMap.put("types", result.getTypesString()); // Add any additional attributes as needed

			// Add the map to the list
			placesList.add(placeMap);
		}
		return ResponseEntity.ok(placesList);

	}

}
