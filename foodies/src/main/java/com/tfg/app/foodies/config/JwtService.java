package com.tfg.app.foodies.config;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;


@Service
public class JwtService {
	
//	@Autowired
//	private UserDetailsService userDetailsService;

	
    private static final long EXPIRATION_TIME = 10 * 60 * 60 * 1000; // 10 horas
	private final SecretKey secretKey;

	public JwtService() {
		this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(secretKey) // Usa SecretKey aquí
				.build().parseClaimsJws(token).getBody();
	}

	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}
	
//	public Boolean validateToken1(String token) {
//	    String username = extractUsername(token);
//	    UserDetails userDetails = userDetailsService.loadUserByUsername(username); // Aquí obtienes el UserDetails
//	    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
//	}


	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities()); // Agregar roles como claim
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
            .setClaims(claims) // Claims personalizados
            .setSubject(subject) // Subject (usuario)
            .setIssuedAt(new Date()) // Fecha de emisión
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Expira en 10 horas
            .signWith(secretKey, SignatureAlgorithm.HS256) // Firma usando la clave secreta
            .compact();
    }
	
}
