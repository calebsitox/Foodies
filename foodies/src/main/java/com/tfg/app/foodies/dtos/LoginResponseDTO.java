package com.tfg.app.foodies.dtos;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class LoginResponseDTO {

    private String token;
    private String username;
}