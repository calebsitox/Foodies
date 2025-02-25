package com.tfg.app.foodies.google;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

@RestController
@RequestMapping("/api")
public class PlacesController {
	
	
    private static final String GOOGLE_PLACES_API_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";
    private static final String API_KEY = "TU_API_KEY";

    @GetMapping("/places")
    public ResponseEntity<String> getNearbyRestaurants(@RequestParam double latitude, @RequestParam double longitude) {
        String url = GOOGLE_PLACES_API_URL + "?location=" + latitude + "," + longitude + 
                     "&radius=1000&type=restaurant&key=" + API_KEY;

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        return response;
    }
    
    @GetMapping("/places/name/directions")
    public ResponseEntity<List<Map<String, String>>> getNearbyRestaurantsNameDirections(@RequestParam double latitude, @RequestParam double longitude) throws JsonMappingException, JsonProcessingException {
        String url = GOOGLE_PLACES_API_URL + "?location=" + latitude + "," + longitude + 
                     "&radius=1000&type=restaurant&key=" + API_KEY;

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        // Parsear JSON
        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        List<Map<String, String>> placesList = new ArrayList<>();

        for (JsonNode result : jsonNode.path("results")) {
            Map<String, String> place = new HashMap<>();
            place.put("name", result.path("name").asText());
            place.put("address", result.path("vicinity").asText());
            placesList.add(place);
        }

        return ResponseEntity.ok(placesList);
    }
    
    
}
