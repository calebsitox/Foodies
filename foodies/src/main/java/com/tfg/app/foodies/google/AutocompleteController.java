package com.tfg.app.foodies.google;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfg.app.foodies.config.JwtService;

@RestController
@RequestMapping("/api/autocomplete")
public class AutocompleteController {
	
	
	private JwtService jwtService;

    private static final String GOOGLE_AUTOCOMPLETE_API_URL = "https://maps.googleapis.com/maps/api/place/autocomplete/json";
    private static final String API_KEY = "AIzaSyCNSEbqAUraUirf4YqRBbdxflyysTWWx6c"; // Reemplaza con tu API key

    @PostMapping
    public ResponseEntity<JsonNode> getAutocomplete(@RequestHeader(value = "Authorization", required = false) String authHeader, @RequestBody Map<String, Object> requestBody) throws Exception {
    	
    	//String token = authHeader.replace("Bearer ", "");
        // Agregar la API key en la URL
        String url = GOOGLE_AUTOCOMPLETE_API_URL + "?key=" + API_KEY;
        
        // Configuramos los headers para indicar que se enviará JSON
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        

        // Se crea el entity con el cuerpo de la petición
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        
        // Se hace la llamada POST a la API de Google
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        
        // Convertimos la respuesta a JsonNode para mayor facilidad
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonResponse = mapper.readTree(response.getBody());
        
        return ResponseEntity.ok(jsonResponse);
    }
}
