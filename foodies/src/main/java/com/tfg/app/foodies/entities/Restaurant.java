package com.tfg.app.foodies.entities;

import java.util.Collection;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "restaurant", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "latitude", "longitude", "address" }, name = "unique_location") })
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "restaurant_seq")
    @SequenceGenerator(name = "restaurant_seq", sequenceName = "my_sequence", allocationSize = 1)
	private Long id;
	
	@NotNull
	private String name;


	@NotNull
	private String address;

	@NotNull
	private double latitude;

	@NotNull
	private double longitude;

	private double rating;

	private List<String> types;

	@Column(length = 1000) 
	private String photoReference;

	@ManyToMany(mappedBy = "restaurants")
	private Collection<User> users;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double raiting) {
		this.rating = raiting;
	}

	public List<String> getTypes() {
		return types;
	}

	public void setTypes(List<String> types) {
		this.types = types;
	}

	public String getPhotoReference() {
		return photoReference;
	}

	public void setPhotoReference(String photoReference) {
		this.photoReference = photoReference;
	}

	public Collection<User> getUsers() {
		return users;
	}

	public void setUsers(Collection<User> users) {
		this.users = users;
	}

}
