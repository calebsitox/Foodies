package com.tfg.app.foodies.entities;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.data.annotation.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PostLoad;
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
	private Double latitude;

	@NotNull
	private Double longitude;

	private Double rating;

	@Column(name = "types")
	private String typesString; // Almacena el formato crudo {a,b,c}
	
	@Transient
	private List<String> typesList;

	@Column(length = 1000) 
	private String photoReference;
	
	@JsonIgnore
	@ManyToMany(mappedBy = "restaurants")
	private Collection<User> users;
	
	
	@PostLoad
	private void convertStringToArray() {
	    if (this.typesString != null) {
	        // Elimina llaves y divide por comas
	        this.typesList = Arrays.asList(
	            this.typesString.replaceAll("[{}]", "").split(",")
	        );
	    }
	}

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

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getRating() {
		return rating;
	}

	public void setRating(Double raiting) {
		this.rating = raiting;
	}

	public String getTypesString() {
		return typesString;
	}

	public void setTypesString(String typesString) {
		this.typesString = typesString;
	}

	public List<String> getTypesList() {
		return typesList;
	}

	public void setTypesList(List<String> typesList) {
		this.typesList = typesList;
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
