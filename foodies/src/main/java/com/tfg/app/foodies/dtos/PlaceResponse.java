package com.tfg.app.foodies.dtos;

import lombok.Data;

@Data
public class PlaceResponse {
    private String displayName;
    private String generativeSummary;
    // ...
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getGenerativeSummary() {
		return generativeSummary;
	}
	public void setGenerativeSummary(String generativeSummary) {
		this.generativeSummary = generativeSummary;
	}

}