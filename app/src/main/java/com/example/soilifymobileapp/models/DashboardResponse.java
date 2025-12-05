package com.example.soilifymobileapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DashboardResponse {
    @SerializedName("welcome")
    private UserWelcome welcome;

    @SerializedName("quick_stats")
    private QuickStats quickStats;

    @SerializedName("recent_alerts")
    private List<RecentAlert> recentAlerts;

    @SerializedName("has_fields")
    private boolean hasFields;

    @SerializedName("has_activity")
    private boolean hasActivity;

    // Getters and Setters
    public UserWelcome getWelcome() {
        return welcome;
    }

    public void setWelcome(UserWelcome welcome) {
        this.welcome = welcome;
    }

    public QuickStats getQuickStats() {
        return quickStats;
    }

    public void setQuickStats(QuickStats quickStats) {
        this.quickStats = quickStats;
    }

    public List<RecentAlert> getRecentAlerts() {
        return recentAlerts;
    }

    public void setRecentAlerts(List<RecentAlert> recentAlerts) {
        this.recentAlerts = recentAlerts;
    }

    public boolean isHasFields() {
        return hasFields;
    }

    public void setHasFields(boolean hasFields) {
        this.hasFields = hasFields;
    }

    public boolean isHasActivity() {
        return hasActivity;
    }

    public void setHasActivity(boolean hasActivity) {
        this.hasActivity = hasActivity;
    }
}
