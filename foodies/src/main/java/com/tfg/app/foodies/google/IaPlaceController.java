package com.tfg.app.foodies.google;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfg.app.foodies.config.JwtService;
import com.tfg.app.foodies.dtos.GeocodeRequest;
import com.tfg.app.foodies.dtos.PlaceResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class IaPlaceController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IaPlaceController.class);

	@Value("${google.api.key}")
	private String apiKey;
	
	@Autowired
	private JwtService jwtService;
	
	
	@GetMapping("/summaries/place")
	public ResponseEntity<String>  obtenerUrlFoto(@RequestHeader("Authorization")  String token, @RequestBody GeocodeRequest geocodeRequest) throws JsonMappingException, JsonProcessingException {
		
//		Boolean validate = jwtService.validateToken1(token);
//		
//		if (Boolean.FALSE.equals(validate)) {
//			return null;
//		}
		RestTemplate restTemplate = new RestTemplate();


		String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" +  geocodeRequest.getLatitude() + "," + geocodeRequest.getLongitude() + "&key=" + apiKey;
		
		ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

		// Parsear JSON
		JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
		LOGGER.info("JSON response parsed successfully.");
		
		JsonNode result = jsonNode.path("results");
		
		String placeId = result.path("id").asText();
		
		 if (placeId == null) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                                 .body("Respuesta inesperada de Geocoding API");
	        }
		
		String placesUrl = "https://places.googleapis.com/v1/places/" + placeId ;

		HttpHeaders headers = new HttpHeaders();
        headers.set("X-Goog-Api-Key", apiKey);
        headers.set("X-Goog-FieldMask", "displayName,generativeSummary");
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        
        ResponseEntity<PlaceResponse> placeResp = restTemplate.exchange(
                placesUrl,
                HttpMethod.GET,
                entity,
                PlaceResponse.class
            );
            PlaceResponse body = placeResp.getBody();
            if (body == null || body.getGenerativeSummary() == null) {
                throw new IllegalStateException("El lugar no tiene generativeSummary disponible");
            }

            String summary = body.getGenerativeSummary();
            return ResponseEntity.ok(summary);
	   
	}
}
