package com.tfg.app.foodies.service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.tfg.app.foodies.dtos.GeocodeRequest;
import com.tfg.app.foodies.entities.Restaurant;
import com.tfg.app.foodies.entities.User;
import com.tfg.app.foodies.repository.RestaurantRepository;
import com.tfg.app.foodies.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RestaurantService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RestaurantRepository restaurantRepository;

	@Transactional
	public void likeRestaurantUser(@RequestBody Long userId, Long restaurantId,
			@RequestHeader("Authorization") String token) {

		Optional<User> user = userRepository.findUserByUserId(userId);

		Restaurant restaurant = restaurantRepository.findRestaurantById(restaurantId);

		if (Objects.nonNull(user.get())) {
			user.get().getRestaurants().add(restaurant);
			restaurant.getUsers().add(user.get());
		}

	}

	@Transactional
	public List<Restaurant> locateResaturantByCoordinates(@RequestBody GeocodeRequest geocodeRequest,
			@RequestHeader("Authorization") String token) {
		
		if (Objects.isNull(geocodeRequest.getLatitude())|| Objects.isNull(geocodeRequest.getLongitude())) {
		    throw new IllegalArgumentException("GeocodeRequest inválido: faltan coordenadas.");
		}

		List<Restaurant> restaurants = restaurantRepository.findNearbyRestaurants(geocodeRequest.getLatitude(),
				geocodeRequest.getLongitude());

		if (restaurants.isEmpty()) {
		    return Collections.emptyList();
		}
		return restaurants;

	}

}
