package com.example.soilifymobileapp.models;

import com.google.gson.annotations.SerializedName;

public class UserWelcome {
    @SerializedName("user_id")
    private int userId;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("location")
    private String location;

    @SerializedName("greeting_message")
    private String greetingMessage;

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getGreetingMessage() {
        return greetingMessage;
    }

    public void setGreetingMessage(String greetingMessage) {
        this.greetingMessage = greetingMessage;
    }
}
