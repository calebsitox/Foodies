package com.tfg.app.foodies.config;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
	
    private static final long EXPIRATION_TIME = 10 * 60 * 60 * 1000; // 10 horas
	private final SecretKey secretKey;

	public JwtService() {
		// Genera una clave segura para HS256
		this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
	}

	// Extraer el username del token
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

	// Validar si el token es correcto
	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	// Comprobar si el token ha expirado
	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	// Generar un nuevo token
    // Generar un token con UserDetails
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities()); // Agregar roles como claim
        return createToken(claims, userDetails.getUsername());
    }

    // Generar un token con claims personalizados
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
