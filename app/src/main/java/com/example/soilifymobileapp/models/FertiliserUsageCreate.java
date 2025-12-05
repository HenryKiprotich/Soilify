package com.example.soilifymobileapp.models;

import com.google.gson.annotations.SerializedName;

/**
 * Model for creating a new fertiliser usage record.
 * Date should be in YYYY-MM-DD format to match backend expectations.
 */
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
    private String date; // Format: YYYY-MM-DD

    public FertiliserUsageCreate() {}

    public FertiliserUsageCreate(int fieldId, String fertiliserType, float amountKg, String weather, String notes, String date) {
        this.fieldId = fieldId;
        this.fertiliserType = fertiliserType;
        this.amountKg = amountKg;
        this.weather = weather;
        this.notes = notes;
        this.date = date;
    }

    public int getFieldId() {
        return fieldId;
    }

    public void setFieldId(int fieldId) {
        this.fieldId = fieldId;
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
}
