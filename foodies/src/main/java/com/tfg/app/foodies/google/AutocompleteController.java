package com.tfg.app.foodies.google;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfg.app.foodies.config.JwtService;


@RestController
@RequestMapping("/api/autocomplete")
public class AutocompleteController {
	
	
	private JwtService jwtService;
    private static final Logger LOGGER = LoggerFactory.getLogger(AutocompleteController.class);
    private static final String GOOGLE_AUTOCOMPLETE_API_URL = "https://maps.googleapis.com/maps/api/place/autocomplete/json";
    private static final String API_KEY = "AIzaSyCNSEbqAUraUirf4YqRBbdxflyysTWWx6c"; // Reemplaza con tu API key

    @PostMapping
    public ResponseEntity<JsonNode> getAutocomplete(@RequestParam Map<String, Object> input) {
        try {
            // Construir la URL con la API key
            String url = GOOGLE_AUTOCOMPLETE_API_URL + "?key=" + API_KEY;

            // Configurar los encabezados de la solicitud
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Crear la entidad de la solicitud con el cuerpo y los encabezados
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(input, headers);

            // Realizar la solicitud POST a la API de Google
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            // Convertir la respuesta a JsonNode para facilitar su manejo
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonResponse = mapper.readTree(response.getBody());

            return ResponseEntity.ok(jsonResponse);
        } catch (Exception e) {
            LOGGER.error("Error al obtener sugerencias de autocompletado", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

