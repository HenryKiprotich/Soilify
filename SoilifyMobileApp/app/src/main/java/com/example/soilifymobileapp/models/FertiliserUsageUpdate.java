package com.example.soilifymobileapp.models;

import com.google.gson.annotations.SerializedName;

/**
 * Model for updating a fertiliser usage record.
 * All fields are optional. Date should be in YYYY-MM-DD format.
 */
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
    private String date; // Format: YYYY-MM-DD

    public FertiliserUsageUpdate() {}

    public FertiliserUsageUpdate(Integer fieldId, String fertiliserType, Float amountKg, String weather, String notes, String date) {
        this.fieldId = fieldId;
        this.fertiliserType = fertiliserType;
        this.amountKg = amountKg;
        this.weather = weather;
        this.notes = notes;
        this.date = date;
    }

    public Integer getFieldId() {
        return fieldId;
    }

    public void setFieldId(Integer fieldId) {
        this.fieldId = fieldId;
    }

    public String getFertiliserType() {
        return fertiliserType;
    }

    public void setFertiliserType(String fertiliserType) {
        this.fertiliserType = fertiliserType;
    }

    public Float getAmountKg() {
        return amountKg;
    }

    public void setAmountKg(Float amountKg) {
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
