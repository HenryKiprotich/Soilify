package com.example.soilifymobileapp.models;

import com.google.gson.annotations.SerializedName;

public class RecentAlert {
    @SerializedName("id")
    private int id;

    @SerializedName("message")
    private String message;

    @SerializedName("field_id")
    private Integer fieldId;

    @SerializedName("field_name")
    private String fieldName;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("time_ago")
    private String timeAgo;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }
}
