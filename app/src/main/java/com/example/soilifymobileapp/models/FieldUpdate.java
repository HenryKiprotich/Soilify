package com.example.soilifymobileapp.models;

import com.google.gson.annotations.SerializedName;

public class FieldUpdate {
    @SerializedName("field_name")
    private String fieldName;

    @SerializedName("soil_type")
    private String soilType;

    @SerializedName("crop_type")
    private String cropType;

    @SerializedName("size_hectares")
    private Float sizeHectares;

    public FieldUpdate(String fieldName, String soilType, String cropType, Float sizeHectares) {
        this.fieldName = fieldName;
        this.soilType = soilType;
        this.cropType = cropType;
        this.sizeHectares = sizeHectares;
    }

    // Getters and Setters
}
