package com.example.soilifymobileapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class FieldRead {
    @SerializedName("id")
    private int id;

    @SerializedName("farmer_id")
    private int farmerId;

    @SerializedName("field_name")
    private String fieldName;

    @SerializedName("soil_type")
    private String soilType;

    @SerializedName("crop_type")
    private String cropType;

    @SerializedName("size_hectares")
    private float sizeHectares;

    @SerializedName("created_at")
    private Date createdAt;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFarmerId() {
        return farmerId;
    }

    public void setFarmerId(int farmerId) {
        this.farmerId = farmerId;
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
