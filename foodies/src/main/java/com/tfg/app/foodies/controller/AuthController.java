package com.tfg.app.foodies.controller;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
import com.tfg.app.foodies.entities.Role;
import com.tfg.app.foodies.entities.User;
import com.tfg.app.foodies.repository.RoleRepository;
import com.tfg.app.foodies.repository.UserRepository;
import com.tfg.app.foodies.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private RoleRepository roleRepository;

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
	public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
		System.out.println("Attempting login for: " + loginRequest.getUsername());
		try {
			// Autenticar al usuario con sus credenciales
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

			// Cargar detalles del usuario y generar el token JWT
			UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
			String token = jwtService.generateToken(userDetails);
			
	        HttpSession session = request.getSession();
	        String sessionId = session.getId();
	        
	        User user = userRepository.findByUsername(loginRequest.getUsername())
	                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
	        
	        user.setSessionId(sessionId);  // Asume que tienes un campo sessionId en tu entidad User
	        userRepository.save(user);
			
			return ResponseEntity.ok(new AuthResponse(token));
		} catch (AuthenticationException e) {
			// Responder con FORBIDDEN si la autenticación falla (contraseña incorrecta)
			System.out.println("Authentication failed: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid username or password");
		}
	}

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest, HttpServletRequest request) {
		 Role role = this.roleRepository.findByIdRole();
		 
		// Verificar si el usuario ya existe
		if (userDetailsService.userExists(registerRequest.getUsername())) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body("Username '" + registerRequest.getUsername() + "' already exists");
		}

		// Crear el nuevo usuario con contraseña codificada
		String encodedPassword = new BCryptPasswordEncoder().encode(registerRequest.getPassword());
		User newUser = new User(); // Asegúrate de que tienes una clase User con los campos necesarios
		newUser.setUsername(registerRequest.getUsername());
		newUser.setPassword(encodedPassword);
		newUser.setEmail(registerRequest.getEmail()); // Establece el email del usuario
		newUser.setRoles(Collections.singletonList(role));
        HttpSession session = request.getSession();
        String sessionId = session.getId();
        

        newUser.setSessionId(sessionId); 
		// Guardar el usuario en la base de datos
		userRepository.save(newUser);

		return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
	}
	
	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpServletRequest request) {
	    String authHeader = request.getHeader("Authorization");
	    if (authHeader != null && authHeader.startsWith("Bearer ")) {
	        String jwt = authHeader.substring(7);
	        String sessionId = jwtService.extractSessionId(jwt);

//	        userService.closeSession(sessionId);
	        return ResponseEntity.ok("Sesión cerrada");
	    }
	    return ResponseEntity.badRequest().body("Token inválido");
	}


}