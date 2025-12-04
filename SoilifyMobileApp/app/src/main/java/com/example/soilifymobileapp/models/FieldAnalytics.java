package com.example.soilifymobileapp.models;

import com.google.gson.annotations.SerializedName;

public class FieldAnalytics {
    @SerializedName("field_id")
    private int fieldId;

    @SerializedName("field_name")
    private String fieldName;

    @SerializedName("soil_type")
    private String soilType;

    @SerializedName("crop_type")
    private String cropType;

    @SerializedName("size_hectares")
    private float sizeHectares;

    @SerializedName("total_fertilizer_kg")
    private float totalFertilizerKg;

    @SerializedName("fertilizer_applications")
    private int fertilizerApplications;

    @SerializedName("last_fertilizer_date")
    private String lastFertilizerDate;

    @SerializedName("weather_records")
    private int weatherRecords;

    @SerializedName("avg_temperature")
    private float avgTemperature;

    @SerializedName("avg_rainfall")
    private float avgRainfall;

    @SerializedName("avg_soil_moisture")
    private float avgSoilMoisture;

    @SerializedName("total_alerts")
    private int totalAlerts;

    @SerializedName("recent_alerts")
    private int recentAlerts;

    // Getters and Setters
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

    public String getSoilType() {
        return soilType;
    }

    public void setSoilType(String soilType) {
        this.soilType = soilType;
    }

    public String getCropType() {
        return cropType;
    }

    public void setCropType(String cropType) {
        this.cropType = cropType;
    }

    public float getSizeHectares() {
        return sizeHectares;
    }

    public void setSizeHectares(float sizeHectares) {
        this.sizeHectares = sizeHectares;
    }

    public float getTotalFertilizerKg() {
        return totalFertilizerKg;
    }

    public void setTotalFertilizerKg(float totalFertilizerKg) {
        this.totalFertilizerKg = totalFertilizerKg;
    }

    public int getFertilizerApplications() {
        return fertilizerApplications;
    }

    public void setFertilizerApplications(int fertilizerApplications) {
        this.fertilizerApplications = fertilizerApplications;
    }

    public String getLastFertilizerDate() {
        return lastFertilizerDate;
    }

    public void setLastFertilizerDate(String lastFertilizerDate) {
        this.lastFertilizerDate = lastFertilizerDate;
    }

    public int getWeatherRecords() {
        return weatherRecords;
    }

    public void setWeatherRecords(int weatherRecords) {
        this.weatherRecords = weatherRecords;
    }

    public float getAvgTemperature() {
        return avgTemperature;
    }

    public void setAvgTemperature(float avgTemperature) {
        this.avgTemperature = avgTemperature;
    }

    public float getAvgRainfall() {
        return avgRainfall;
    }

    public void setAvgRainfall(float avgRainfall) {
        this.avgRainfall = avgRainfall;
    }

    public float getAvgSoilMoisture() {
        return avgSoilMoisture;
    }

    public void setAvgSoilMoisture(float avgSoilMoisture) {
        this.avgSoilMoisture = avgSoilMoisture;
    }

    public int getTotalAlerts() {
        return totalAlerts;
    }

    public void setTotalAlerts(int totalAlerts) {
        this.totalAlerts = totalAlerts;
    }

    public int getRecentAlerts() {
        return recentAlerts;
    }

    public void setRecentAlerts(int recentAlerts) {
        this.recentAlerts = recentAlerts;
    }
}
