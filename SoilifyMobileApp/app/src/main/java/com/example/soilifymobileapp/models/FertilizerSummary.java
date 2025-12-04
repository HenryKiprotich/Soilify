package com.example.soilifymobileapp.models;

import com.google.gson.annotations.SerializedName;

public class FertilizerSummary {
    @SerializedName("total_applications")
    private int totalApplications;

    @SerializedName("total_amount_kg")
    private float totalAmountKg;

    @SerializedName("average_amount_per_application")
    private float averageAmountPerApplication;

    @SerializedName("most_used_fertilizer")
    private String mostUsedFertilizer;

    @SerializedName("fields_fertilized")
    private int fieldsFertilized;

    // Getters and Setters
    public int getTotalApplications() {
        return totalApplications;
    }

    public void setTotalApplications(int totalApplications) {
        this.totalApplications = totalApplications;
    }

    public float getTotalAmountKg() {
        return totalAmountKg;
    }

    public void setTotalAmountKg(float totalAmountKg) {
        this.totalAmountKg = totalAmountKg;
    }

    public float getAverageAmountPerApplication() {
        return averageAmountPerApplication;
    }

    public void setAverageAmountPerApplication(float averageAmountPerApplication) {
        this.averageAmountPerApplication = averageAmountPerApplication;
    }

    public String getMostUsedFertilizer() {
        return mostUsedFertilizer;
    }

    public void setMostUsedFertilizer(String mostUsedFertilizer) {
        this.mostUsedFertilizer = mostUsedFertilizer;
    }

    public int getFieldsFertilized() {
        return fieldsFertilized;
    }

    public void setFieldsFertilized(int fieldsFertilized) {
        this.fieldsFertilized = fieldsFertilized;
    }
}
