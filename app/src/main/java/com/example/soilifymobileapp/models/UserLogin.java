package com.example.soilifymobileapp.models;

import com.google.gson.annotations.SerializedName;

public class UserLogin {
    @SerializedName("email_adress")
    private String emailAddress;

    @SerializedName("phone_number")
    private String phoneNumber;

    @SerializedName("password")
    private String password;

    public UserLogin(String emailAddress, String phoneNumber, String password) {
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    // Getters and Setters
}
