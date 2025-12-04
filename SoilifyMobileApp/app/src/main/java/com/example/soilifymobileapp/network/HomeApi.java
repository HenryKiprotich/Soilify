package com.example.soilifymobileapp.network;

import com.example.soilifymobileapp.models.DashboardResponse;
import com.example.soilifymobileapp.models.QuickStats;
import com.example.soilifymobileapp.models.UserWelcome;

import retrofit2.Call;
import retrofit2.http.GET;

public interface HomeApi {
    @GET("api/home/dashboard")
    Call<DashboardResponse> getDashboard();

    @GET("api/home/quick-stats")
    Call<QuickStats> getQuickStats();

    @GET("api/home/welcome")
    Call<UserWelcome> getWelcome();
}
