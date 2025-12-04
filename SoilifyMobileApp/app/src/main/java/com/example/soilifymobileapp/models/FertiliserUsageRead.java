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

    public int getId() {
        return id;
    }

    public int getFarmerId() {
        return farmerId;
    }

    public int getFieldId() {
        return fieldId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFertiliserType() {
        return fertiliserType;
    }

    public float getAmountKg() {
        return amountKg;
    }

    public String getWeather() {
        return weather;
    }

    public String getNotes() {
        return notes;
    }

    public Date getDate() {
        return date;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}
