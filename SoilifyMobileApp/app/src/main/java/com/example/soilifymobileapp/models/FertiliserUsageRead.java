package com.example.soilifymobileapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class FertiliserUsageRead {
    @SerializedName("id")
    private int id;

    @SerializedName("farmer_id")
    private int farmerId;

    @SerializedName("field_id")
    private int fieldId;

    @SerializedName("field_name")
    private String fieldName;

    @SerializedName("fertiliser_type")
    private String fertiliserType;

    @SerializedName("amount_kg")
    private float amountKg;

    @SerializedName("weather")
    private String weather;

    @SerializedName("notes")
    private String notes;

    @SerializedName("date")
    private Date date;

    @SerializedName("created_at")
    private Date createdAt;

    // Getters and Setters
}
