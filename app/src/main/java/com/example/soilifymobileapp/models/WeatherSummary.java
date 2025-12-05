package com.example.soilifymobileapp.models;

import com.google.gson.annotations.SerializedName;

public class WeatherSummary {
    @SerializedName("total_records")
    private int totalRecords;

    @SerializedName("average_temperature")
    private Float averageTemperature;  // Boxed to handle null

    @SerializedName("average_rainfall")
    private Float averageRainfall;  // Boxed to handle null

    @SerializedName("average_soil_moisture")
    private Float averageSoilMoisture;  // Boxed to handle null

    @SerializedName("max_temperature")
    private Float maxTemperature;  // Boxed to handle null

    @SerializedName("max_rainfall")
    private Float maxRainfall;  // Boxed to handle null

    @SerializedName("min_temperature")
    private Float minTemperature;  // Boxed to handle null

    @SerializedName("fields_monitored")
    private int fieldsMonitored;

    // Getters and Setters
    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public Float getAverageTemperature() {
        return averageTemperature;
    }

    public void setAverageTemperature(Float averageTemperature) {
        this.averageTemperature = averageTemperature;
    }

    public Float getAverageRainfall() {
        return averageRainfall;
    }

    public void setAverageRainfall(Float averageRainfall) {
        this.averageRainfall = averageRainfall;
    }

    public Float getAverageSoilMoisture() {
        return averageSoilMoisture;
    }

    public void setAverageSoilMoisture(Float averageSoilMoisture) {
        this.averageSoilMoisture = averageSoilMoisture;
    }

    public Float getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(Float maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public Float getMaxRainfall() {
        return maxRainfall;
    }

    public void setMaxRainfall(Float maxRainfall) {
        this.maxRainfall = maxRainfall;
    }

    public Float getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(Float minTemperature) {
        this.minTemperature = minTemperature;
    }

    public int getFieldsMonitored() {
        return fieldsMonitored;
    }

    public void setFieldsMonitored(int fieldsMonitored) {
        this.fieldsMonitored = fieldsMonitored;
    }
}
