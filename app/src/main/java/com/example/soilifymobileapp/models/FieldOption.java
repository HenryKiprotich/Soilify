package com.example.soilifymobileapp.models;

import com.google.gson.annotations.SerializedName;

public class FieldOption {
    @SerializedName("id")
    private int id;

    @SerializedName("field_name")
    private String fieldName;

    @SerializedName("soil_type")
    private String soilType;

    @SerializedName("crop_type")
    private String cropType;

    @SerializedName("size_hectares")
    private float sizeHectares;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    // toString() is important for ArrayAdapter
    @Override
    public String toString() {
        return fieldName;
    }
}
