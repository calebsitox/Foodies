package com.tfg.app.foodies.google;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfg.app.foodies.dtos.GeocodeRequest;
import com.tfg.app.foodies.entities.Location;
import com.tfg.app.foodies.entities.User;
import com.tfg.app.foodies.repository.LocationRepository;
import com.tfg.app.foodies.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
@RestController
@RequestMapping("/api")
public class GeocodingController {
	
	private LocationRepository locationRepository;
	
	private UserRepository userRepository;
	
	
    private static final Logger LOGGER = LoggerFactory.getLogger(GeocodingController.class);

    private static final String GOOGLE_GEOCODING_API_URL = "https://maps.googleapis.com/maps/api/geocode/json";
    private static final String API_KEY = "AIzaSyC2e5dQEm9LxTLWbeaIcC34HoWL-xlB300";
    
    @PostMapping("/geocode")
    public ResponseEntity<?>getGeoCode(@RequestBody GeocodeRequest request) {
    	
        LOGGER.info("Recibido: Latitud = " + request.getLatitude() + ", Longitud = " + request.getLongitude());

        if (request.getLatitude() == 0.0 || request.getLongitude() == 0.0) {
            return ResponseEntity.badRequest().body("Error: Coordenadas inválidas");
        }
    	String url = GOOGLE_GEOCODING_API_URL + "?latlng=" + request.getLatitude() + ", " + request.getLongitude() + "&key=" + API_KEY;
    	
    	
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
    	return ResponseEntity.ok(parseAddress(response.getBody()));
    }
    
    private String parseAddress(String responseJson) {
    	 try {
             JsonNode root = new ObjectMapper().readTree(responseJson);
             return root.path("results").get(0).path("formatted_address").asText();
         } catch (Exception e) {
             return "Address not found";
         }
    	
    }
    

	@PostMapping("/location/geocode")
	public ResponseEntity<?> getGeocode(@RequestBody GeocodeRequest request, @RequestParam Long userId) {
	    Optional<User> user = userRepository.findById(userId);
	    if (user.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
	    }
	    

	    Optional<Location> existingLocation = locationRepository.findByLatitudeAndLongitudeAndUser(
	            request.getLatitude(), request.getLongitude(), user.get());

	    if (existingLocation.isPresent()) {
	        return ResponseEntity.ok(existingLocation.get().getAddress());
	    }

	    String url = GOOGLE_GEOCODING_API_URL + "?latlng=" + request.getLatitude() + "," + request.getLongitude() + "&key=" + API_KEY;
	    RestTemplate restTemplate = new RestTemplate();
	    ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

	    String address = parseAddress(response.getBody());

	    Location newLocation = new Location();
	    newLocation.setUser(user.get());
	    newLocation.setLatitude(request.getLatitude());
	    newLocation.setLongitude(request.getLongitude());
	    newLocation.setAddress(address);
	    locationRepository.save(newLocation);

	    return ResponseEntity.ok(address);
	}
    
}
