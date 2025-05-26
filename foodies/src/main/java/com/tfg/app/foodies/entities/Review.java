package com.tfg.app.foodies.entities;

public class Review {

    private String authorName;
    private String text;
    private double rating;

    // Constructor
    public Review(String authorName, String text, double rating) {
        this.authorName = authorName;
        this.text = text;
        this.rating = rating;
    }

    // Getters and setters
    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
