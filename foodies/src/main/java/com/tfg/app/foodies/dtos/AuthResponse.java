package com.tfg.app.foodies.dtos;

public class AuthResponse {
    private String token;

    // Constructor
    public AuthResponse(String token) {
        this.token = token;
    }

    // Getter
    public String getToken() {
        return token;
    }

    // Setter (opcional, si necesitas modificar el token después)
    public void setToken(String token) {
        this.token = token;
    }
}
