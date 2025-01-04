package com.tfg.app.foodies.controller;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.*;
import com.tfg.app.foodies.dtos.LocationRequest;

@RestController
@RequestMapping("/api")
public class LocationController {

	private static final String API_KEY = "YOUR_API_KEY";

	@PostMapping("/location")
	public String getNearbyRestaurants(@RequestBody LocationRequest locationRequest) throws Exception {
		double latitude = locationRequest.getLatitude();
		double longitude = locationRequest.getLongitude();
		String url = String.format(
				"https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%s,%s&radius=5000&type=restaurant&key=%s",
				latitude, longitude, API_KEY);

		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			HttpGet request = new HttpGet(url);
			try (CloseableHttpResponse response = httpClient.execute(request)) {
				String json = EntityUtils.toString(response.getEntity());
				return json; // You might want to parse this JSON and return a formatted response.
			}
		}
	}
}
