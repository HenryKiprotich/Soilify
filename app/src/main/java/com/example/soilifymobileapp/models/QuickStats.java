package com.example.soilifymobileapp.models;

import com.google.gson.annotations.SerializedName;

public class QuickStats {
    @SerializedName("total_fields")
    private int totalFields;

    @SerializedName("pending_alerts")
    private int pendingAlerts;

    @SerializedName("last_fertilizer_date")
    private String lastFertilizerDate;

    @SerializedName("weather_records_today")
    private int weatherRecordsToday;

    // Getters and Setters
    public int getTotalFields() {
        return totalFields;
    }

    public void setTotalFields(int totalFields) {
        this.totalFields = totalFields;
    }

    public int getPendingAlerts() {
        return pendingAlerts;
    }

    public void setPendingAlerts(int pendingAlerts) {
        this.pendingAlerts = pendingAlerts;
    }

    public String getLastFertilizerDate() {
        return lastFertilizerDate;
    }

    public void setLastFertilizerDate(String lastFertilizerDate) {
        this.lastFertilizerDate = lastFertilizerDate;
    }

    public int getWeatherRecordsToday() {
        return weatherRecordsToday;
    }

    public void setWeatherRecordsToday(int weatherRecordsToday) {
        this.weatherRecordsToday = weatherRecordsToday;
    }
}
