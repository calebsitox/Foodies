package com.tfg.app.foodies.google;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfg.app.foodies.dtos.GeocodeRequest;
import com.tfg.app.foodies.dtos.PlaceDetailsDTO;
import com.tfg.app.foodies.dtos.RestaurantRequest;
import com.tfg.app.foodies.entities.Restaurant;
import com.tfg.app.foodies.entities.Review;
import com.tfg.app.foodies.repository.RestaurantRepository;
import com.tfg.app.foodies.service.RestaurantService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PlacesController {

	private static final Logger LOGGER = LoggerFactory.getLogger(AutocompleteController.class);

	private static final String GOOGLE_PLACES_API_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";

	@Autowired
	private RestaurantService restaurantService;

	@Autowired
	private RestaurantRepository restaurantRepository;

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

	@PostMapping("/places/name/directions")
	public ResponseEntity<List<Map<?, ?>>> getNearbyRestaurantsNameDirections(@RequestBody GeocodeRequest request,
			@RequestHeader("Authorization") String token) throws JsonMappingException, JsonProcessingException {
		String url = GOOGLE_PLACES_API_URL + "?location=" + request.getLatitude() + "," + request.getLongitude()
				+ "&radius=1000&type=restaurant&key=" + apiKey;

		RestTemplate restTemplate = new RestTemplate();

		List<Restaurant> restaurantByQuery = restaurantService.locateResaturantByCoordinates(request, token);
		List<Map<?, ?>> placesList = new ArrayList<>();

		if (restaurantByQuery.size() > 6) {
			for (Restaurant result : restaurantByQuery) {
				Map<String, Object> placeMap = new HashMap<>();

				// Populate the map with restaurant details
				placeMap.put("name", result.getName());
				placeMap.put("address", result.getAddress());
				placeMap.put("latitude", result.getLatitude());
				placeMap.put("longitude", result.getLongitude());
				placeMap.put("photoReference", result.getPhotoReference());
				placeMap.put("rating", result.getRating());
				placeMap.put("types", result.getTypesString()); // Add any additional attributes as needed

				// Add the map to the list
				placesList.add(placeMap);
			}
			return ResponseEntity.ok(placesList);
		}
		ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

		// Parsear JSON
		JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
		LOGGER.info("JSON response parsed successfully.");

		for (JsonNode result : jsonNode.path("results")) {
			StringBuilder sb = new StringBuilder();
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
				restaurant.setPhotoReference(photoReference);
				place.put("photoReference", photoReference);
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
					sb.append(typeNode);
				}
				LOGGER.info("Types: " + typesList);
				restaurant.setTypesList(typesList);
				place.put("Types", sb.toString().trim());
			} else {
				LOGGER.info("No hay tipos disponibles.");
			}
			List<Restaurant> existing = this.restaurantRepository.findRestaurantsByCoordenates(lat, lon);
			if (existing.isEmpty()) {
				LOGGER.info("Restaurant added to the list.");
				restaurantRepository.save(restaurant);
			}
			// Agregamos la información al listado de lugares que se enviará al frontend
			placesList.add(place);
		}

		return ResponseEntity.ok(placesList);
	}

	@PostMapping("/place/detail")
	public ResponseEntity<PlaceDetailsDTO> placeDetail(@RequestHeader("Authorization") String token,
			@RequestBody GeocodeRequest geocodeRequest) throws JsonMappingException, JsonProcessingException {
		RestTemplate restTemplate = new RestTemplate();

		String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + geocodeRequest.getLatitude() + ","
				+ geocodeRequest.getLongitude() + "&key=" + apiKey;

		ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

		// Parsear JSON
		JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
		LOGGER.info("JSON response parsed successfully.");

		JsonNode results = jsonNode.path("results");

		String placeId = results.get(0).path("place_id").asText();

		String urlPlaceDetails = "https://maps.googleapis.com/maps/api/place/details/json?place_id=" + placeId
				+ "&fields=name,formatted_address,geometry,formatted_phone_number,website,opening_hours,price_level,rating,user_ratings_total,photos,reviews,types&key="
				+ apiKey;

		ResponseEntity<String> placeDetailsResponse = restTemplate.getForEntity(urlPlaceDetails, String.class);

		JsonNode placeDetailsJsonNode = null;
		try {
			placeDetailsJsonNode = new ObjectMapper().readTree(placeDetailsResponse.getBody());
		} catch (Exception e) {
			e.printStackTrace();
		}

		JsonNode placeDetails = placeDetailsJsonNode.path("result");

		// Obtener los datos deseados
		String name = placeDetails.path("name").asText();
		String formattedAddress = placeDetails.path("formatted_address").asText();
		String rating = placeDetails.path("rating").asText();
		int priceLevel = placeDetails.path("price_level").asInt();
		String phoneNumber = placeDetails.path("international_phone_number").asText();
		String website = placeDetails.path("website").asText();

		List<String> photos = new ArrayList<>();
		JsonNode photosNode = placeDetails.path("photos");
		for (JsonNode photo : photosNode) {
			String photoReference = photo.path("photo_reference").asText();
			String photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="
					+ photoReference + "&key=" + apiKey;
			photos.add(photoUrl);
		}

		List<Review> reviews = new ArrayList<>();
		JsonNode reviewsNode = placeDetails.path("reviews");
		for (JsonNode reviewNode : reviewsNode) {
			String authorName = reviewNode.path("author_name").asText();
			String text = reviewNode.path("text").asText();
			double reviewRating = reviewNode.path("rating").asDouble();
			reviews.add(new Review(authorName, text, reviewRating));
		}

		// Crear el objeto de respuesta que contiene todos los detalles
		PlaceDetailsDTO placeDetailsResponseObject = new PlaceDetailsDTO(name, formattedAddress, rating, priceLevel,
				phoneNumber, website, photos, reviews);

		return ResponseEntity.ok(placeDetailsResponseObject);
	}

}
