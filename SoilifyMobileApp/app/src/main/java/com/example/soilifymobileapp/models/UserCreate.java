package com.example.soilifymobileapp.models;

import com.google.gson.annotations.SerializedName;

public class UserCreate {
    @SerializedName("first_name")
    private String firstName;

    @SerializedName("other_name")
    private String otherName;

    @SerializedName("phone_number")
    private String phoneNumber;

    @SerializedName("email_adress")
    private String emailAddress;

    @SerializedName("password")
    private String password;

    @SerializedName("location")
    private String location;

    public UserCreate(String firstName, String otherName, String phoneNumber, String emailAddress, String password, String location) {
        this.firstName = firstName;
        this.otherName = otherName;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.password = password;
        this.location = location;
    }

    // Getters and Setters
}
