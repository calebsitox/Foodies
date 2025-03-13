package com.tfg.app.foodies.google;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfg.app.foodies.dtos.GeocodeRequest;
import com.tfg.app.foodies.dtos.GeocodeRequestUser;
import com.tfg.app.foodies.entities.Location;
import com.tfg.app.foodies.entities.User;
import com.tfg.app.foodies.repository.LocationRepository;
import com.tfg.app.foodies.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GeocodingController {

	private static final Logger LOGGER = LoggerFactory.getLogger(GeocodingController.class);

	private static final String GOOGLE_GEOCODING_API_URL = "https://maps.googleapis.com/maps/api/geocode/json";

	private static final String API_KEY = "AIzaSyCNSEbqAUraUirf4YqRBbdxflyysTWWx6c";

	@Value("${google.api.key}")
	private String apiKey;
	
	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private UserRepository userRepository;

	@PostMapping("/geocode")
	public ResponseEntity<?> getGeoCode(@RequestBody GeocodeRequest request) {

		LOGGER.info("Recibido: Latitud = " + request.getLatitude() + ", Longitud = " + request.getLongitude());

		if (request.getLatitude() == 0.0 || request.getLongitude() == 0.0) {
			return ResponseEntity.badRequest().body("Error: Coordenadas inválidas");
		}
		String url = GOOGLE_GEOCODING_API_URL + "?latlng=" + request.getLatitude() + ", " + request.getLongitude()
				+ "&key=" + API_KEY;

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

	@GetMapping("/geocode/addressToCoordinates")
	public ResponseEntity<?> addressToCordinates(@RequestParam String address) {
		try {
			if (address == null || address.isBlank()) {
				return ResponseEntity.badRequest().body("Error: No address provided");
			}

			// Construir la URL codificando el parámetro 'address'
			String url = GOOGLE_GEOCODING_API_URL + "?address=" + URLEncoder.encode(address, StandardCharsets.UTF_8)
					+ "&key=" + API_KEY;

			// Realizar la solicitud GET a la API de Google
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

			// Convertir la respuesta a JsonNode
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(response.getBody());

			// Verificar que existan resultados y extraer las coordenadas
			if (root.has("results") && root.get("results").size() > 0) {
				JsonNode location = root.get("results").get(0).get("geometry").get("location");
				double lat = location.get("lat").asDouble();
				double lng = location.get("lng").asDouble();
				Map<String, Double> coordinates = Map.of("latitude", lat, "longitude", lng);
				return ResponseEntity.ok(coordinates);
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body("No se encontraron coordenadas para la dirección proporcionada");
			}
		} catch (Exception e) {
			LOGGER.error("Error al obtener las coordenadas", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener las coordenadas");
		}
	}

	@PostMapping("/location/geocode")
	public ResponseEntity<?> getGeocode(@RequestBody GeocodeRequestUser request, @RequestHeader("Authorization")  String token) {
		
		Optional<User> user = userRepository.findUserByUserId(request.getUserId());
		if (user.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
		}


		String url = GOOGLE_GEOCODING_API_URL + "?latlng=" + request.getLatitude() + "," + request.getLongitude()
				+ "&key=" + API_KEY;
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
