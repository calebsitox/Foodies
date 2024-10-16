package com.tfg.app.foodies.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tfg.app.foodies.config.CustomUserDetailsService;
import com.tfg.app.foodies.config.JwtService;
import com.tfg.app.foodies.dtos.AuthResponse;
import com.tfg.app.foodies.dtos.LoginRequest;
import com.tfg.app.foodies.dtos.RegisterRequest;
import com.tfg.app.foodies.entities.User;
import com.tfg.app.foodies.repository.UserRepository;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private UserRepository userRepository;

	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService; // Servicio para manejar JWT
	private final CustomUserDetailsService userDetailsService; // Para cargar detalles del usuario

	public AuthController(AuthenticationManager authenticationManager, JwtService jwtService,
			CustomUserDetailsService userDetailsService) {
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
		System.out.println("Attempting login for: " + loginRequest.getUsername());
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

			UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
			String token = jwtService.generateToken(userDetails);

			return ResponseEntity.ok(new AuthResponse(token));
		} catch (AuthenticationException e) {
			System.out.println("Authentication failed: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
		}
	}

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {

		// Verificar si el usuario ya existe
		if (userDetailsService.userExists(registerRequest.getUsername())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
		}

		// Crear el nuevo usuario
		String encodedPassword = new BCryptPasswordEncoder().encode(registerRequest.getPassword());
		User newUser = new User(); // Asegúrate de que tienes una clase User
		newUser.setUsername(registerRequest.getUsername());
		newUser.setPassword(encodedPassword);

		// Guardar el usuario en la base de datos
		userRepository.save(newUser); // Asegúrate de que esto esté configurado correctamente

		return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
	}
}