package com.tfg.app.foodies.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tfg.app.foodies.entities.Location;
import com.tfg.app.foodies.entities.User;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
	
    Optional<Location> findByLatitudeAndLongitudeAndUser(double latitude, double longitude, User user);
    List<Location> findByUser(User user);
}

