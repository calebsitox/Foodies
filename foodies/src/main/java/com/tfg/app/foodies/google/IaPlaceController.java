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

	@GetMapping("/reviews/place")
	public ResponseEntity<String> placeReviews(@RequestHeader("Authorization") String token,
			@RequestBody GeocodeRequest geocodeRequest) throws JsonMappingException, JsonProcessingException {

//		Boolean validate = jwtService.validateToken1(token);
//		
//		if (Boolean.FALSE.equals(validate)) {
//			return null;
//		}
		RestTemplate restTemplate = new RestTemplate();

		String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + geocodeRequest.getLatitude() + ","
				+ geocodeRequest.getLongitude() + "&key=" + apiKey;

		ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

		// Parsear JSON
		JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
		LOGGER.info("JSON response parsed successfully.");

		JsonNode results = jsonNode.path("results");

		String placeId = results.get(0).path("place_id").asText();

		if (placeId == null) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Respuesta inesperada de Geocoding API");
		}

		String placesUrl = "https://places.googleapis.com/v1/places/" + placeId;

		HttpHeaders headers = new HttpHeaders();
		headers.set("X-Goog-Api-Key", apiKey);
		headers.set("X-Goog-FieldMask", "displayName,reviewSummary,reviews");
		HttpEntity<Void> entity = new HttpEntity<>(headers);

		ResponseEntity<JsonNode> placesResponse = restTemplate.exchange(placesUrl, HttpMethod.GET, entity,
				JsonNode.class);

		JsonNode place = placesResponse.getBody();
		if (place == null || !place.has("reviews")) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Este lugar no tiene reseñas disponibles.");
		}

		JsonNode reviews = place.path("reviews");

		// Opcional: formatear reseñas como String
		StringBuilder sb = new StringBuilder();
		for (JsonNode review : reviews) {
			String author = review.path("authorAttribution").path("displayName").asText();
			double rating = review.path("rating").asDouble();
			String text = review.path("text").asText();
			sb.append("").append(rating).append(" - ").append(author).append("\n");
			sb.append(text).append("\n\n");
		}

		return ResponseEntity.ok(sb.toString());

	}

	@GetMapping("/summaries/place")
	public ResponseEntity<String> placeSummarie(@RequestHeader("Authorization") String token,
			@RequestBody GeocodeRequest geocodeRequest) throws JsonMappingException, JsonProcessingException {

//		Boolean validate = jwtService.validateToken1(token);
//		
//		if (Boolean.FALSE.equals(validate)) {
//			return null;
//		}
		RestTemplate restTemplate = new RestTemplate();

		String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + geocodeRequest.getLatitude() + ","
				+ geocodeRequest.getLongitude() + "&key=" + apiKey;

		ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

		// Parsear JSON
		JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
		LOGGER.info("JSON response parsed successfully.");

		JsonNode results = jsonNode.path("results");

		String placeId = results.get(0).path("place_id").asText();

		if (placeId == null) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Respuesta inesperada de Geocoding API");
		}

		String placesUrl = "https://places.googleapis.com/v1/places/" + placeId;

		HttpHeaders headers = new HttpHeaders();
		headers.set("X-Goog-Api-Key", apiKey);
		headers.set("X-Goog-FieldMask", "id,displayName,neighborhoodSummary");

		HttpEntity<Void> entity = new HttpEntity<>(headers);

		ResponseEntity<JsonNode> placesResponse = restTemplate.exchange(placesUrl, HttpMethod.GET, entity,
				JsonNode.class);

		JsonNode placeBody = placesResponse.getBody();
		if (placeBody == null || !placeBody.has("generativeSummary")) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT)
					.body("Este lugar no tiene resumen generado (generativeSummary).");
		}

		String summary = placeBody.path("generativeSummary").path("text").asText();
		return ResponseEntity.ok(summary);
	}
}
