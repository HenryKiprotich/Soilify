package com.example.soilifymobileapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class FertiliserUsageUpdate {
    @SerializedName("field_id")
    private Integer fieldId;

    @SerializedName("fertiliser_type")
    private String fertiliserType;

    @SerializedName("amount_kg")
    private Float amountKg;

    @SerializedName("weather")
    private String weather;

    @SerializedName("notes")
    private String notes;

    @SerializedName("date")
    private Date date;

    public FertiliserUsageUpdate(Integer fieldId, String fertiliserType, Float amountKg, String weather, String notes, Date date) {
        this.fieldId = fieldId;
        this.fertiliserType = fertiliserType;
        this.amountKg = amountKg;
        this.weather = weather;
        this.notes = notes;
        this.date = date;
    }

    // Getters and Setters
}
