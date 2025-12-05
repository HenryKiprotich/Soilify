package com.example.soilifymobileapp.models;

import com.google.gson.annotations.SerializedName;

/**
 * Model for reading fertiliser usage data from the API.
 * Dates are represented as Strings (YYYY-MM-DD for date, ISO format for created_at).
 */
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
    private String date; // Format: YYYY-MM-DD

    @SerializedName("created_at")
    private String createdAt; // ISO datetime string

    public FertiliserUsageRead() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFarmerId() {
        return farmerId;
    }

    public void setFarmerId(int farmerId) {
        this.farmerId = farmerId;
    }

    public int getFieldId() {
        return fieldId;
    }

    public void setFieldId(int fieldId) {
        this.fieldId = fieldId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFertiliserType() {
        return fertiliserType;
    }

    public void setFertiliserType(String fertiliserType) {
        this.fertiliserType = fertiliserType;
    }

    public float getAmountKg() {
        return amountKg;
    }

    public void setAmountKg(float amountKg) {
        this.amountKg = amountKg;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
