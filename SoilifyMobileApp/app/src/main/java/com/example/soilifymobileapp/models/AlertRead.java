package com.example.soilifymobileapp.models;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class AlertRead {
    @SerializedName("id")
    private int id;

    @SerializedName("farmer_id")
    private int farmerId;

    @SerializedName("farmer_name")
    private String farmerName;

    @SerializedName("field_id")
    private Integer fieldId;

    @SerializedName("field_name")
    private String fieldName;

    @SerializedName("message")
    private String message;

    @SerializedName("created_at")
    private Date createdAt;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public int getFarmerId() {
        return farmerId;
    }

    public String getFarmerName() {
        return farmerName;
    }

    public Integer getFieldId() {
        return fieldId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getMessage() {
        return message;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}
