package com.tfg.app.foodies.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tfg.app.foodies.entities.Restaurant;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

	@Query(value = "SELECT * FROM restaurant WHERE " + "6371000 * acos(" + "GREATEST(LEAST("
			+ "cos(radians(:latitude)) * cos(radians(latitude)) * " + "cos(radians(longitude) - radians(:longitude)) + "
			+ "sin(radians(:latitude)) * sin(radians(latitude)), " + "1), -1)" + // Asegura el rango [-1,1] para acos
			") <= 1000", nativeQuery = true)
	List<Restaurant> findNearbyRestaurants(@Param("latitude") double latitude, @Param("longitude") double longitude);

	@Query(value = "SELECT * FROM restaurant r WHERE :type = any(r.types_list) and 6371000 * acos(" + "GREATEST(LEAST("
			+ "cos(radians(:latitude)) * cos(radians(latitude)) * " + "cos(radians(longitude) - radians(:longitude)) + "
			+ "sin(radians(:latitude)) * sin(radians(latitude)), " + "1), -1)" + // Asegura el rango [-1,1] para acos
			") <= 1000", nativeQuery = true)
	List<Restaurant> findNearbyRestaurantsAndType(@Param("type") String type, @Param("latitude") double latitude,
			@Param("longitude") double longitude);

	@Query("SELECT r FROM Restaurant r WHERE r.id = :id")
	Restaurant findRestaurantById(@Param("id") Long id);

	@Query("SELECT r.id FROM Restaurant r WHERE r.name = :restaurantName")
	Long findRestaurantByName(@Param("restaurantName") String restaurantName);

	@Query(value = "SELECT * FROM restaurant r INNER JOIN restaurant_user ru ON ru.restaurant_id = r.id "
			+ "WHERE ru.user_id = :userId", nativeQuery = true)
	List<Restaurant> findLikedRestaurantsByUser(@Param("userId") Long userId);

	@Query(value = "select * from restaurant r where r.latitude = :latitude and r.longitude = :longitude", nativeQuery = true)
	List<Restaurant> findRestaurantsByCoordenates(@Param("latitude") double latitude,
			@Param("longitude") double longitude);
}
