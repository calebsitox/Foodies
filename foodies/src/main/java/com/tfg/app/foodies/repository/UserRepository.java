package com.tfg.app.foodies.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tfg.app.foodies.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	 // Buscar un usuario por su nombre de usuario
	Optional<User> findByUsername(String username);
    
    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findUserByUserId(@Param("id") Long id);
    
    
    Optional<User> findByEmail(String email);

    // Verificar si un usuario con un nombre de usuario específico ya existe
    boolean existsByUsername(String username);
}

