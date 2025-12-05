package com.example.soilifymobileapp.network;

import com.example.soilifymobileapp.models.WeatherDataCreate;
import com.example.soilifymobileapp.models.WeatherDataRead;
import com.example.soilifymobileapp.models.WeatherDataUpdate;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface WeatherApi {

    @GET("api/weather-data")
    Call<List<WeatherDataRead>> getAllWeatherData();

    @GET("api/weather-data/{weather_id}")
    Call<WeatherDataRead> getWeatherData(@Path("weather_id") int weatherId);

    @POST("api/weather-data")
    Call<WeatherDataRead> createWeatherData(@Body WeatherDataCreate weatherData);

    @PUT("api/weather-data/{weather_id}")
    Call<WeatherDataRead> updateWeatherData(@Path("weather_id") int weatherId, @Body WeatherDataUpdate weatherData);

    @DELETE("api/weather-data/{weather_id}")
    Call<Void> deleteWeatherData(@Path("weather_id") int weatherId);
}
