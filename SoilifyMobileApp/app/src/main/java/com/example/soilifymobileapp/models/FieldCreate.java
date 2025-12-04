package com.example.soilifymobileapp.models;

import com.google.gson.annotations.SerializedName;

public class FieldCreate {
    @SerializedName("field_name")
    private String fieldName;

    @SerializedName("soil_type")
    private String soilType;

    @SerializedName("crop_type")
    private String cropType;

    @SerializedName("size_hectares")
    private float sizeHectares;

    public FieldCreate(String fieldName, String soilType, String cropType, float sizeHectares) {
        this.fieldName = fieldName;
        this.soilType = soilType;
        this.cropType = cropType;
        this.sizeHectares = sizeHectares;
    }

    // Getters and Setters
}
