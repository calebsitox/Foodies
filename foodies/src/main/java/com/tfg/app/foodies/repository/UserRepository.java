package com.tfg.app.foodies.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tfg.app.foodies.entities.User;

import jakarta.transaction.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	 // Buscar un usuario por su nombre de usuario
	Optional<User> findByUsername(String username);
    
    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findUserByUserId(@Param("id") Long id);
    
    Optional<User> findByEmail(String email);
    
    boolean existsByUsername(String username);
    
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.sessionId = :sessionId")
    boolean existBySessionId(@Param("sessionId") String sessiondId);
    
//    @Modifying
//    @Transactional
//    @Query("UPDATE User SET session_id = NULL WHERE session_id = :sessionId ")
//    void deleteBySessionId(@Param("sessionId") String sessionId);
}

