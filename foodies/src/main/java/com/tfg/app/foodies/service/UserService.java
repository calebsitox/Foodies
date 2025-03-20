package com.tfg.app.foodies.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.tfg.app.foodies.entities.User;
import com.tfg.app.foodies.repository.UserRepository;

import jakarta.mail.MessagingException;

@Service
public class UserService {

	@Autowired
	private MailService mailService;

	private User user;


	@Autowired
	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public Optional<User> findByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	public void saveUser(User user) {
		userRepository.save(user);
	}

	public String forgotPassword(String email) {
		userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User Not find with this Email: " + email));
		try {
			this.mailService.sentSetPassword(email);
		} catch (MessagingException e) {
			throw new RuntimeException("Unable to sent cahnge password email please try again");
		}
		return "Please check your emial to set new Passwordyo yoir account";
	}

	public Object setPassword(String email, String newPassword) {
		String encodedPassword = new BCryptPasswordEncoder().encode(newPassword);
		user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User Not find with this Email: " + email));
		user.setPassword(encodedPassword);
		userRepository.save(user);
		return "New password Changed Succefully log in with new password";
	}
}
