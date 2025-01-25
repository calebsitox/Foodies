package com.tfg.app.foodies.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tfg.app.foodies.service.MailService;
import com.tfg.app.foodies.service.UserService;

@RestController
@RequestMapping("/api/check")
public class MailController {

	@Autowired
	private UserService userService;

	@Autowired
	private MailService mailService;

	@PutMapping("/forgot-password")
	public ResponseEntity<?> forgotPassword(@RequestParam String email) {
		return new ResponseEntity<>(userService.forgotPassword(email), HttpStatus.OK);
	}

	@PostMapping("/confirmation")
	public ResponseEntity<?> confirmation(@RequestParam String email, @RequestBody String inputCode) {
		boolean isConfirmed = mailService.confirmCode(inputCode, email);
		return new ResponseEntity<>(isConfirmed, HttpStatus.OK);
	}

	@PutMapping("/set-password")
	public ResponseEntity<?> setPassword(@RequestParam String email, @RequestParam String inputCode,
			@RequestBody String newPassword) {
		if (mailService.confirmCode(inputCode, email)) {
			return new ResponseEntity<>(userService.setPassword(email, newPassword), HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Invalid confirmation code", HttpStatus.BAD_REQUEST);
		}
	}

}
