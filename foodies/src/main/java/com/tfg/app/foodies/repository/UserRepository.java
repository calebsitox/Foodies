package com.tfg.app.foodies.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tfg.app.foodies.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
	 // Buscar un usuario por su nombre de usuario
    Optional<User> findByUsername(String username);

    // Verificar si un usuario con un nombre de usuario específico ya existe
    boolean existsByUsername(String username);
}

