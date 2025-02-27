package com.tfg.app.foodies.google;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AutocompleteController {

	private static final Logger LOGGER = LoggerFactory.getLogger(AutocompleteController.class);

	private static final String GOOGLE_AUTOCOMPLETE_API_URL = "https://maps.googleapis.com/maps/api/place/autocomplete/json";

	@Value("${google.api.key}")
	private String apiKey;

	@GetMapping("/autocomplete")
	public ResponseEntity<JsonNode> getAutocomplete(@RequestParam("input") String input) {
		try {
			// Codificar la dirección correctamente en la URL
			String url = GOOGLE_AUTOCOMPLETE_API_URL + "?input=" + URLEncoder.encode(input, StandardCharsets.UTF_8)
					+ "&key=" + apiKey;

			// Realizar la solicitud GET a la API de Google
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

			// Convertir la respuesta a JsonNode para facilitar su manejo
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonResponse = mapper.readTree(response.getBody());

			return ResponseEntity.ok(jsonResponse);
		} catch (Exception e) {
			LOGGER.error("Error al obtener sugerencias de autocompletado", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}
}
