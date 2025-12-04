package com.example.soilifymobileapp.models;

import com.google.gson.annotations.SerializedName;

public class WeatherDataCreate {
    @SerializedName("field_id")
    private int fieldId;

    @SerializedName("temperature")
    private float temperature;

    @SerializedName("rainfall")
    private float rainfall;

    @SerializedName("soil_moisture")
    private float soilMoisture;

    public WeatherDataCreate(int fieldId, float temperature, float rainfall, float soilMoisture) {
        this.fieldId = fieldId;
        this.temperature = temperature;
        this.rainfall = rainfall;
        this.soilMoisture = soilMoisture;
    }

    // Getters and Setters
}
