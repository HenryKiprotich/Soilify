package com.example.soilifymobileapp.models;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class AlertSummary {
    @SerializedName("total_alerts")
    private int totalAlerts;

    @SerializedName("alerts_today")
    private int alertsToday;

    @SerializedName("alerts_this_week")
    private int alertsThisWeek;

    @SerializedName("alerts_by_field")
    private Map<String, Integer> alertsByField;

    // Getters and Setters
    public int getTotalAlerts() {
        return totalAlerts;
    }

    public int getAlertsToday() {
        return alertsToday;
    }

    public int getAlertsThisWeek() {
        return alertsThisWeek;
    }

    public Map<String, Integer> getAlertsByField() {
        return alertsByField;
    }
}
