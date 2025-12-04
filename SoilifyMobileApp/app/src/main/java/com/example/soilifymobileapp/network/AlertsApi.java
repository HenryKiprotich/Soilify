package com.example.soilifymobileapp.network;

import com.example.soilifymobileapp.models.AlertRead;
import com.example.soilifymobileapp.models.AlertSummary;
import com.example.soilifymobileapp.models.FieldOption;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AlertsApi {

    @GET("alerts/fields-dropdown")
    Call<List<FieldOption>> getFieldsForDropdown();

    @GET("alerts")
    Call<List<AlertRead>> getAllAlerts(@Query("field_id") Integer fieldId, @Query("limit") Integer limit);

    @GET("alerts/summary")
    Call<AlertSummary> getAlertsSummary();
}
