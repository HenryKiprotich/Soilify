package com.example.soilifymobileapp.network;

import com.example.soilifymobileapp.models.FieldOption;
import com.example.soilifymobileapp.models.WeatherDataCreate;
import com.example.soilifymobileapp.models.WeatherDataRead;
import com.example.soilifymobileapp.models.WeatherDataStats;
import com.example.soilifymobileapp.models.WeatherDataUpdate;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WeatherApi {

    @GET("weather-data/fields-dropdown")
    Call<List<FieldOption>> getFieldsForDropdown();

    @GET("weather-data")
    Call<List<WeatherDataRead>> getAllWeatherData(@Query("field_id") Integer fieldId);

    @GET("weather-data/stats")
    Call<List<WeatherDataStats>> getWeatherStatistics();

    @GET("weather-data/{weather_id}")
    Call<WeatherDataRead> getWeatherData(@Path("weather_id") int weatherId);

    @POST("weather-data")
    Call<WeatherDataRead> createWeatherData(@Body WeatherDataCreate weather);

    @PUT("weather-data/{weather_id}")
    Call<WeatherDataRead> updateWeatherData(@Path("weather_id") int weatherId, @Body WeatherDataUpdate weatherUpdate);

    @DELETE("weather-data/{weather_id}")
    Call<Void> deleteWeatherData(@Path("weather_id") int weatherId);
}
