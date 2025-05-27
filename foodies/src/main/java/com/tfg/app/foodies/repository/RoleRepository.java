package com.tfg.app.foodies.repository;


import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tfg.app.foodies.entities.Role;
import com.tfg.app.foodies.entities.User;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{

		Optional<Role> findByName(String name);
		
		@Query(value = "SELECT * FROM roles WHERE id = 1", nativeQuery = true)
		Role findByIdRole();
		
}