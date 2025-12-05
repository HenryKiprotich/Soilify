package com.example.soilifymobileapp.models;

import com.google.gson.annotations.SerializedName;

/**
 * Payload for updating weather data. All fields are optional to allow partial updates.
 */
public class WeatherDataUpdate {
    @SerializedName("field_id")
    private Integer fieldId;

    @SerializedName("temperature")
    private Float temperature;

    @SerializedName("rainfall")
    private Float rainfall;

    @SerializedName("soil_moisture")
    private Float soilMoisture;

    public WeatherDataUpdate() {}

    public WeatherDataUpdate(Integer fieldId, Float temperature, Float rainfall, Float soilMoisture) {
        this.fieldId = fieldId;
        this.temperature = temperature;
        this.rainfall = rainfall;
        this.soilMoisture = soilMoisture;
    }

    public Integer getFieldId() {
        return fieldId;
    }

    public void setFieldId(Integer fieldId) {
        this.fieldId = fieldId;
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
}
