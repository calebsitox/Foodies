package com.tfg.app.foodies.service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tfg.app.foodies.dtos.GeocodeRequest;
import com.tfg.app.foodies.dtos.LikeRequest;
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
	public void likeRestaurantUser(LikeRequest likeRequest) {

		Optional<User> user = userRepository.findByUsername(likeRequest.getUserName());

		List<Restaurant> resturantList= restaurantRepository.findRestaurantsByCoordenates(likeRequest.getLat(), likeRequest.getLon());

		Restaurant restaurant = resturantList.get(0);

		if (user.isPresent()) {
			user.get().getRestaurants().add(restaurant);
			restaurant.getUsers().add(user.get());
		}

	}

	@Transactional
	public List<Restaurant> locateResaturantByCoordinates(GeocodeRequest request, String token) {

		if (Objects.isNull(request) || Objects.isNull(request)) {
			throw new IllegalArgumentException("GeocodeRequest inválido: faltan coordenadas.");
		}

		List<Restaurant> restaurants = restaurantRepository.findNearbyRestaurants(request.getLatitude(),
				request.getLongitude());

		if (restaurants.isEmpty()) {
			return Collections.emptyList();
		}
		return restaurants;

	}

	@Transactional
	public List<Restaurant> filterByLikedrestaurants(LikeRequest likeRequest) {

		return Collections.emptyList();
	}

	public Long getRestaurantId(String resaturantName) {
		if (Objects.nonNull(resaturantName)) {
			Long resturantId = restaurantRepository.findRestaurantByName(resaturantName);
			return resturantId;
		}
		return null;
	}

}
