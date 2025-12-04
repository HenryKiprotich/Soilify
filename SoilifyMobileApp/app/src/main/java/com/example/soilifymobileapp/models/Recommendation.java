package com.example.soilifymobileapp.models;

public class Recommendation {
    private String sender;
    private String message;

    public Recommendation(String sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }
}
