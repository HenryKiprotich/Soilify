package com.example.soilifymobileapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class FertiliserUsageCreate {
    @SerializedName("field_id")
    private int fieldId;

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

    public FertiliserUsageCreate(int fieldId, String fertiliserType, float amountKg, String weather, String notes, Date date) {
        this.fieldId = fieldId;
        this.fertiliserType = fertiliserType;
        this.amountKg = amountKg;
        this.weather = weather;
        this.notes = notes;
        this.date = date;
    }

    // Getters and Setters
}
