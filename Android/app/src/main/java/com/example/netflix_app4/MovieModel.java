package com.example.netflix_app4;

public class MovieModel {
    private String title;
    private String description;
    private double rating;
    private int length;
    private String director;
    private int intId;
    private String[] categories;
    private String language;
    private String releaseDate;
    private String thumbnail;
    private String videoUrl;

    // Getters
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public double getRating() {
        return rating;
    }

    public int getLength() {
        return length;
    }

    public String getDirector() {
        return director;
    }

    public int getIntId() {
        return intId;
    }

    public String[] getCategories() {
        return categories;
    }

    public String getLanguage() {
        return language;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getVideoUrl() {
        return videoUrl;
    }
}
