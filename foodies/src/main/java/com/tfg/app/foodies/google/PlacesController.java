package com.tfg.app.foodies.google;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfg.app.foodies.entities.Restaurant;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PlacesController {

	private static final Logger LOGGER = LoggerFactory.getLogger(AutocompleteController.class);
	
	private static final String GOOGLE_PLACES_API_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";
	
	@Value("${google.api.key}")
	private String apiKey;

	@GetMapping("/places")
	public ResponseEntity<String> getNearbyRestaurants(@RequestParam double latitude, @RequestParam double longitude) {
		String url = GOOGLE_PLACES_API_URL + "?location=" + latitude + "," + longitude
				+ "&radius=1000&type=restaurant&key=" + apiKey;

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

		return response;
	}

	@GetMapping("/places/name/directions")
	public ResponseEntity<List<Map<String, String>>> getNearbyRestaurantsNameDirections(@RequestParam double latitude,
			@RequestParam double longitude) throws JsonMappingException, JsonProcessingException {
		String url = GOOGLE_PLACES_API_URL + "?location=" + latitude + "," + longitude
				+ "&radius=1000&type=restaurant&key=" + apiKey;

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

		// Parsear JSON
		JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
		LOGGER.info("JSON response parsed successfully.");
		List<Map<String, String>> placesList = new ArrayList<>();

		for (JsonNode result : jsonNode.path("results")) {
		    LOGGER.info("Processing a new restaurant entry.");
		    List<String> typesList = new ArrayList<>();
		    Restaurant restaurant = new Restaurant();
		    Map<String, String> place = new HashMap<>();
		    
		    String name = result.path("name").asText();
		    LOGGER.info("Restaurant name: " + name);
		    place.put("name", name);
		    restaurant.setName(name);
		    
		    String address = result.path("vicinity").asText();
		    LOGGER.info("Restaurant address: " + address);
		    place.put("address", address);
		    restaurant.setAddress(address);
		    
		    JsonNode locationNode = result.path("geometry").path("location");
		    double lat = locationNode.path("lat").asDouble();
		    double lon = locationNode.path("lng").asDouble();
		    LOGGER.info("Latitude: " + lat + ", Longitude: " + lon);
		    restaurant.setLatitude(lat);
		    restaurant.setLongitude(lon);
		    
		    JsonNode firstPhoto = result.path("photos").get(0);
		    if (firstPhoto != null) {
		        String photoReference = firstPhoto.path("photo_reference").asText();
		        LOGGER.info("Photo reference: " + photoReference);
		        restaurant.setPhothoReference(photoReference);
		    } else {
		        LOGGER.info("No photo available for this restaurant.");
		    }
		    
		    double rating = result.path("rating").asDouble();
		    LOGGER.info("Rating: " + rating);
		    restaurant.setRating(rating);
		    
		    // Extraemos y asignamos los tipos de establecimiento
		    if (result.has("types") && result.path("types").isArray()) {
		        for (JsonNode typeNode : result.path("types")) {
		            typesList.add(typeNode.asText());
		        }
		        LOGGER.info("Types: " + typesList);
		        restaurant.setTypes(typesList);
		    } else {
		        LOGGER.info("No hay tipos disponibles.");
		    }
		    
		    // Agregamos la información al listado de lugares que se enviará al frontend
		    placesList.add(place);
		    LOGGER.info("Restaurant added to the list.");
		}

		return ResponseEntity.ok(placesList);
	}

	

}
