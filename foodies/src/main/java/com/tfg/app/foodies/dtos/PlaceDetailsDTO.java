package com.tfg.app.foodies.dtos;

import java.util.List;

import com.tfg.app.foodies.entities.Review;

public class PlaceDetailsDTO {

	private String name;
	private String formattedAddress;
	private String rating;
	private int priceLevel;
	private String phoneNumber;
	private String website;
	private List<String> photos;
	private List<Review> reviews; // Nueva lista para las reviews

	// Constructor
	public PlaceDetailsDTO(String name, String formattedAddress, String rating, int priceLevel, String phoneNumber,
			String website, List<String> photos, List<Review> reviews) {
		this.name = name;
		this.formattedAddress = formattedAddress;
		this.rating = rating;
		this.priceLevel = priceLevel;
		this.phoneNumber = phoneNumber;
		this.website = website;
		this.photos = photos;
		this.reviews = reviews;
	}

	public List<Review> getReviews() {
		return reviews;
	}

	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}

	// Getters and setters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFormattedAddress() {
		return formattedAddress;
	}

	public void setFormattedAddress(String formattedAddress) {
		this.formattedAddress = formattedAddress;
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public int getPriceLevel() {
		return priceLevel;
	}

	public void setPriceLevel(int priceLevel) {
		this.priceLevel = priceLevel;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public List<String> getPhotos() {
		return photos;
	}

	public void setPhotos(List<String> photos) {
		this.photos = photos;
	}
}