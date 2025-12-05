package com.example.soilifymobileapp.models;

import com.google.gson.annotations.SerializedName;

public class FertilizerByType {
    @SerializedName("fertiliser_type")
    private String fertiliserType;

    @SerializedName("total_amount_kg")
    private float totalAmountKg;

    @SerializedName("application_count")
    private int applicationCount;

    @SerializedName("percentage_of_total")
    private float percentageOfTotal;

    // Getters and Setters
    public String getFertiliserType() {
        return fertiliserType;
    }

    public void setFertiliserType(String fertiliserType) {
        this.fertiliserType = fertiliserType;
    }

    public float getTotalAmountKg() {
        return totalAmountKg;
    }

    public void setTotalAmountKg(float totalAmountKg) {
        this.totalAmountKg = totalAmountKg;
    }

    public int getApplicationCount() {
        return applicationCount;
    }

    public void setApplicationCount(int applicationCount) {
        this.applicationCount = applicationCount;
    }

    public float getPercentageOfTotal() {
        return percentageOfTotal;
    }

    public void setPercentageOfTotal(float percentageOfTotal) {
        this.percentageOfTotal = percentageOfTotal;
    }
}
