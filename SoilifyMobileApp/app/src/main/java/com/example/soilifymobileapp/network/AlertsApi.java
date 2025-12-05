package com.example.soilifymobileapp.network;

import com.example.soilifymobileapp.models.AlertRead;
import com.example.soilifymobileapp.models.AlertSummary;
import com.example.soilifymobileapp.models.FieldOption;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AlertsApi {

    @GET("api/alerts/fields-dropdown")
    Call<List<FieldOption>> getFieldsForDropdown();

    @GET("api/alerts")
    Call<List<AlertRead>> getAllAlerts(@Query("field_id") Integer fieldId, @Query("limit") Integer limit);

    @GET("api/alerts/summary")
    Call<AlertSummary> getAlertsSummary();
}
