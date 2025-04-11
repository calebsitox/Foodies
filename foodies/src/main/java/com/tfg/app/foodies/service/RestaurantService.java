package com.tfg.app.foodies.service;

import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.tfg.app.foodies.entities.Restaurant;
import com.tfg.app.foodies.entities.User;
import com.tfg.app.foodies.repository.RestaurantRepository;
import com.tfg.app.foodies.repository.UserRepository;

@Service
public class RestaurantService {
	
	private UserRepository userRepository;
	
	private RestaurantRepository restaurantRepository;
	
	@Transactional
	public void likeRestaurantUser(@RequestBody Long userId, Long restaurantId,
			@RequestHeader("Authorization") String token) {
		
		Optional<User> user = userRepository.findUserByUserId(userId);
		
		Restaurant restaurant = restaurantRepository.findRestaurantById(restaurantId);
		
		if(Objects.nonNull(user.get())) {
			user.get().getRestaurants().add(restaurant);
			restaurant.getUsers().add(user.get());
		}
		
	}

}
