package com.example.soilifymobileapp.network;

import com.example.soilifymobileapp.models.FertilizerByType;
import com.example.soilifymobileapp.models.FarmOverview;
import com.example.soilifymobileapp.models.WeatherTrend;
import com.example.soilifymobileapp.models.FieldAnalytics;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AnalyticsApi {
    @GET("api/analytics/overview")
    Call<FarmOverview> getFarmOverview();

    @GET("api/analytics/fertilizer/by-type")
    Call<List<FertilizerByType>> getFertilizerByType();

    @GET("api/analytics/weather/trends")
    Call<List<WeatherTrend>> getWeatherTrends(@Query("days") int days, @Query("field_id") Integer fieldId);

    @GET("api/analytics/field/{field_id}")
    Call<FieldAnalytics> getFieldAnalytics(@Path("field_id") int fieldId);
}
