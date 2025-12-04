package com.example.soilifymobileapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class WeatherDataStats {
    @SerializedName("field_id")
    private int fieldId;

    @SerializedName("field_name")
    private String fieldName;

    @SerializedName("avg_temperature")
    private float avgTemperature;

    @SerializedName("avg_rainfall")
    private float avgRainfall;

    @SerializedName("avg_soil_moisture")
    private float avgSoilMoisture;

    @SerializedName("total_readings")
    private int totalReadings;

    @SerializedName("latest_reading_date")
    private Date latestReadingDate;

    // Getters and Setters
}
