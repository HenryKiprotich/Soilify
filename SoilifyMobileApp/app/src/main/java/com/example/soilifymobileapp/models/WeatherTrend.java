package com.example.soilifymobileapp.models;

import com.google.gson.annotations.SerializedName;

public class WeatherTrend {
    @SerializedName("date")
    private String date;

    @SerializedName("average_temperature")
    private float averageTemperature;

    @SerializedName("average_rainfall")
    private float averageRainfall;

    @SerializedName("average_soil_moisture")
    private float averageSoilMoisture;

    // Getters and Setters
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getAverageTemperature() {
        return averageTemperature;
    }

    public void setAverageTemperature(float averageTemperature) {
        this.averageTemperature = averageTemperature;
    }

    public float getAverageRainfall() {
        return averageRainfall;
    }

    public void setAverageRainfall(float averageRainfall) {
        this.averageRainfall = averageRainfall;
    }

    public float getAverageSoilMoisture() {
        return averageSoilMoisture;
    }

    public void setAverageSoilMoisture(float averageSoilMoisture) {
        this.averageSoilMoisture = averageSoilMoisture;
    }
}
