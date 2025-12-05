package com.example.soilifymobileapp.models;

import com.google.gson.annotations.SerializedName;

public class WeatherDataCreate {
    @SerializedName("field_id")
    private Integer fieldId; // boxed to allow null when not set

    @SerializedName("temperature")
    private Float temperature;

    @SerializedName("rainfall")
    private Float rainfall;

    @SerializedName("soil_moisture")
    private Float soilMoisture;

    public WeatherDataCreate() {
        // empty constructor for serialization
    }

    public WeatherDataCreate(Integer fieldId, Float temperature, Float rainfall, Float soilMoisture) {
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
