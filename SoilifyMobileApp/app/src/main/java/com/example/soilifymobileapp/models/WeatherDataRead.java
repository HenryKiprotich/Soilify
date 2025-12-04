package com.example.soilifymobileapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class WeatherDataRead {
    @SerializedName("id")
    private int id;

    @SerializedName("field_id")
    private int fieldId;

    @SerializedName("field_name")
    private String fieldName;

    @SerializedName("temperature")
    private float temperature;

    @SerializedName("rainfall")
    private float rainfall;

    @SerializedName("soil_moisture")
    private float soilMoisture;

    @SerializedName("created_at")
    private Date createdAt;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public int getFieldId() {
        return fieldId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public float getTemperature() {
        return temperature;
    }

    public float getRainfall() {
        return rainfall;
    }

    public float getSoilMoisture() {
        return soilMoisture;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}
