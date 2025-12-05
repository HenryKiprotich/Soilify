package com.example.soilifymobileapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soilifymobileapp.R;
import com.example.soilifymobileapp.models.WeatherDataRead;
import com.example.soilifymobileapp.network.ApiClient;
import com.example.soilifymobileapp.network.WeatherApi;
import com.example.soilifymobileapp.ui.adapters.WeatherDataAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherDataActivity extends AppCompatActivity implements WeatherDataAdapter.OnItemClickListener {

    private RecyclerView recyclerViewWeatherData;
    private WeatherDataAdapter weatherDataAdapter;
    private List<WeatherDataRead> weatherDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_data);

        recyclerViewWeatherData = findViewById(R.id.recyclerViewWeatherData);
        FloatingActionButton fabAddWeatherData = findViewById(R.id.fabAddWeatherData);

        recyclerViewWeatherData.setLayoutManager(new LinearLayoutManager(this));
        weatherDataAdapter = new WeatherDataAdapter(weatherDataList, this);
        recyclerViewWeatherData.setAdapter(weatherDataAdapter);

        fabAddWeatherData.setOnClickListener(view -> {
            Intent intent = new Intent(WeatherDataActivity.this, NewWeatherDataActivity.class);
            startActivity(intent);
        });

        loadWeatherData();
    }

    private void loadWeatherData() {
        WeatherApi apiService = ApiClient.getClient(this).create(WeatherApi.class);
        Call<List<WeatherDataRead>> call = apiService.getAllWeatherData();

        call.enqueue(new Callback<List<WeatherDataRead>>() {
            @Override
            public void onResponse(Call<List<WeatherDataRead>> call, Response<List<WeatherDataRead>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    weatherDataList = response.body();
                    weatherDataAdapter.setWeatherDataList(weatherDataList);
                } else {
                    Toast.makeText(WeatherDataActivity.this, "Failed to load weather data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<WeatherDataRead>> call, Throwable t) {
                Toast.makeText(WeatherDataActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onEditClick(WeatherDataRead weatherData) {
        Intent intent = new Intent(this, EditWeatherDataActivity.class);
        intent.putExtra("WEATHER_ID", weatherData.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(WeatherDataRead weatherData) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Weather Data")
                .setMessage("Are you sure you want to delete this weather data entry?")
                .setPositiveButton("Delete", (dialog, which) -> deleteWeatherData(weatherData.getId()))
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteWeatherData(int weatherId) {
        WeatherApi apiService = ApiClient.getClient(this).create(WeatherApi.class);
        Call<Void> call = apiService.deleteWeatherData(weatherId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(WeatherDataActivity.this, "Weather data deleted successfully", Toast.LENGTH_SHORT).show();
                    loadWeatherData(); // Refresh the list
                } else {
                    Toast.makeText(WeatherDataActivity.this, "Failed to delete weather data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(WeatherDataActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadWeatherData(); // Refresh weather data when returning to the activity
    }
}
