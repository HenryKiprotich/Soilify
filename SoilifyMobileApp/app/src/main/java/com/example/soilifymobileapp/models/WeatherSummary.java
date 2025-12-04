package com.example.soilifymobileapp.models;

import com.google.gson.annotations.SerializedName;

public class WeatherSummary {
    @SerializedName("total_records")
    private int totalRecords;

    @SerializedName("average_temperature")
    private float averageTemperature;

    @SerializedName("average_rainfall")
    private float averageRainfall;

    @SerializedName("average_soil_moisture")
    private float averageSoilMoisture;

    @SerializedName("max_temperature")
    private float maxTemperature;

    @SerializedName("max_rainfall")
    private float maxRainfall;

    @SerializedName("min_temperature")
    private float minTemperature;

    @SerializedName("fields_monitored")
    private int fieldsMonitored;

    // Getters and Setters
    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
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

    public float getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(float maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public float getMaxRainfall() {
        return maxRainfall;
    }

    public void setMaxRainfall(float maxRainfall) {
        this.maxRainfall = maxRainfall;
    }

    public float getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(float minTemperature) {
        this.minTemperature = minTemperature;
    }

    public int getFieldsMonitored() {
        return fieldsMonitored;
    }

    public void setFieldsMonitored(int fieldsMonitored) {
        this.fieldsMonitored = fieldsMonitored;
    }
}
