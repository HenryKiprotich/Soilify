package com.example.soilifymobileapp.models;

import com.google.gson.annotations.SerializedName;

public class FarmOverview {
    @SerializedName("total_fields")
    private int totalFields;

    @SerializedName("total_area_hectares")
    private float totalAreaHectares;

    @SerializedName("fertilizer_summary")
    private FertilizerSummary fertilizerSummary;

    @SerializedName("weather_summary")
    private WeatherSummary weatherSummary;

    @SerializedName("total_alerts")
    private int totalAlerts;

    @SerializedName("alerts_this_week")
    private int alertsThisWeek;

    @SerializedName("alerts_today")
    private int alertsToday;

    @SerializedName("last_fertilizer_application")
    private String lastFertilizerApplication;

    @SerializedName("last_weather_record")
    private String lastWeatherRecord;

    @SerializedName("last_alert")
    private String lastAlert;

    // Getters and Setters
    public int getTotalFields() {
        return totalFields;
    }

    public void setTotalFields(int totalFields) {
        this.totalFields = totalFields;
    }

    public float getTotalAreaHectares() {
        return totalAreaHectares;
    }

    public void setTotalAreaHectares(float totalAreaHectares) {
        this.totalAreaHectares = totalAreaHectares;
    }

    public FertilizerSummary getFertilizerSummary() {
        return fertilizerSummary;
    }

    public void setFertilizerSummary(FertilizerSummary fertilizerSummary) {
        this.fertilizerSummary = fertilizerSummary;
    }

    public WeatherSummary getWeatherSummary() {
        return weatherSummary;
    }

    public void setWeatherSummary(WeatherSummary weatherSummary) {
        this.weatherSummary = weatherSummary;
    }

    public int getTotalAlerts() {
        return totalAlerts;
    }

    public void setTotalAlerts(int totalAlerts) {
        this.totalAlerts = totalAlerts;
    }

    public int getAlertsThisWeek() {
        return alertsThisWeek;
    }

    public void setAlertsThisWeek(int alertsThisWeek) {
        this.alertsThisWeek = alertsThisWeek;
    }

    public int getAlertsToday() {
        return alertsToday;
    }

    public void setAlertsToday(int alertsToday) {
        this.alertsToday = alertsToday;
    }

    public String getLastFertilizerApplication() {
        return lastFertilizerApplication;
    }

    public void setLastFertilizerApplication(String lastFertilizerApplication) {
        this.lastFertilizerApplication = lastFertilizerApplication;
    }

    public String getLastWeatherRecord() {
        return lastWeatherRecord;
    }

    public void setLastWeatherRecord(String lastWeatherRecord) {
        this.lastWeatherRecord = lastWeatherRecord;
    }

    public String getLastAlert() {
        return lastAlert;
    }

    public void setLastAlert(String lastAlert) {
        this.lastAlert = lastAlert;
    }
}
