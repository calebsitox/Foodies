package com.tfg.app.foodies.google;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tfg.app.foodies.config.JwtService;
import com.tfg.app.foodies.repository.LocationRepository;
import com.tfg.app.foodies.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PhotoController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GeocodingController.class);


	@Value("${google.api.key}")
	private String apiKey;
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private UserRepository userRepository;
	
	
	@PostMapping("/photo")
	public String obtenerUrlFoto(@RequestHeader("Authorization")  String token, @RequestBody String photoReference) {
		LOGGER.info("Sending photo info  = {}", photoReference);
	    return "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" 
	           + photoReference + "&key=" + apiKey;
	}

}
