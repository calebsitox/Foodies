package com.tfg.app.foodies.controller;

import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tfg.app.foodies.entities.User;
import com.tfg.app.foodies.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {
	
	@Autowired
	private UserService userservice;
	
	@GetMapping("/email")
	public ResponseEntity<?> confirmation(@RequestParam String username) {
		Optional<User> user  = userservice.findByUsername(username);
		if(Objects.isNull(user)) {
			return new ResponseEntity<>(username, HttpStatus.NOT_FOUND);	
		}
		String email = user.get().getEmail();
		return new ResponseEntity<>(email, HttpStatus.OK);
	}
}
