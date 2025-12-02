package com.example.soilifymobileapp.models;

public class Recommendation {
    private String title;
    private String description;

    public Recommendation(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
