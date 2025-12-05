package com.example.soilifymobileapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Represents the Weather data returned from the API.
 * Fields are nullable to match the backend schema where values may be absent.
 */
public class WeatherDataRead {
    @SerializedName("id")
    private Integer id;

    @SerializedName("field_id")
    private Integer fieldId;

    @SerializedName("field_name")
    private String fieldName;

    @SerializedName("temperature")
    private Float temperature;

    @SerializedName("rainfall")
    private Float rainfall;

    @SerializedName("soil_moisture")
    private Float soilMoisture;

    @SerializedName("created_at")
    private Date createdAt; // ISO datetime as string; parse if needed

    public WeatherDataRead() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFieldId() {
        return fieldId;
    }

    public void setFieldId(Integer fieldId) {
        this.fieldId = fieldId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Float getTemperature() {
        return temperature;
    }

    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }

    public Float getRainfall() {
        return rainfall;
    }

    public void setRainfall(Float rainfall) {
        this.rainfall = rainfall;
    }

    public Float getSoilMoisture() {
        return soilMoisture;
    }

    public void setSoilMoisture(Float soilMoisture) {
        this.soilMoisture = soilMoisture;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
