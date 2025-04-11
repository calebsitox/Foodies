package com.tfg.app.foodies.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tfg.app.foodies.entities.Restaurant;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long>{

    @Query("SELECT r FROM Restaurant r WHERE r.id = :id")
    Restaurant findRestaurantById(@Param("id") Long id);
}
