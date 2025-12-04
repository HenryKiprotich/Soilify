package com.example.soilifymobileapp.models;

import com.google.gson.annotations.SerializedName;

public class WeatherDataUpdate {
    @SerializedName("field_id")
    private Integer fieldId;

    @SerializedName("temperature")
    private Float temperature;

    @SerializedName("rainfall")
    private Float rainfall;

    @SerializedName("soil_moisture")
    private Float soilMoisture;

    public WeatherDataUpdate(Integer fieldId, Float temperature, Float rainfall, Float soilMoisture) {
        this.fieldId = fieldId;
        this.temperature = temperature;
        this.rainfall = rainfall;
        this.soilMoisture = soilMoisture;
    }

    // Getters and Setters
}
