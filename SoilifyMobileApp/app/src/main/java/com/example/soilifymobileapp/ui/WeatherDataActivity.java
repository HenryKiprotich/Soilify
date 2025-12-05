package com.example.soilifymobileapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soilifymobileapp.R;
import com.example.soilifymobileapp.models.FieldOption;
import com.example.soilifymobileapp.models.WeatherDataCreate;
import com.example.soilifymobileapp.models.WeatherDataRead;
import com.example.soilifymobileapp.models.WeatherDataUpdate;
import com.example.soilifymobileapp.network.ApiClient;
import com.example.soilifymobileapp.network.WeatherApi;
import com.example.soilifymobileapp.ui.adapters.WeatherDataAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherDataActivity extends AppCompatActivity {

    private WeatherDataAdapter adapter;
    private final List<WeatherDataRead> weatherDataList = new ArrayList<>();
    private final List<FieldOption> fieldOptions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_data);

        RecyclerView recyclerViewWeather = findViewById(R.id.recyclerViewWeather);
        FloatingActionButton fabAddWeather = findViewById(R.id.fabAddWeather);

        recyclerViewWeather.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WeatherDataAdapter(weatherDataList, new WeatherDataAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(WeatherDataRead weatherData) {
                showAddEditWeatherDialog(weatherData);
            }

            @Override
            public void onDeleteClick(WeatherDataRead weatherData) {
                deleteWeatherData(weatherData.getId());
            }
        });
        recyclerViewWeather.setAdapter(adapter);

        fabAddWeather.setOnClickListener(v -> showAddEditWeatherDialog(null));

        loadWeatherData();
        loadFieldOptions();
    }

    private void loadWeatherData() {
        WeatherApi weatherApi = ApiClient.getClient(this).create(WeatherApi.class);
        Call<List<WeatherDataRead>> call = weatherApi.getAllWeatherData(null);
        call.enqueue(new Callback<List<WeatherDataRead>>() {
            @Override
            public void onResponse(Call<List<WeatherDataRead>> call, Response<List<WeatherDataRead>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    weatherDataList.clear();
                    weatherDataList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(WeatherDataActivity.this, "Failed to load weather data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<WeatherDataRead>> call, Throwable t) {
                Toast.makeText(WeatherDataActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadFieldOptions() {
        WeatherApi weatherApi = ApiClient.getClient(this).create(WeatherApi.class);
        Call<List<FieldOption>> call = weatherApi.getFieldsForDropdown();
        call.enqueue(new Callback<List<FieldOption>>() {
            @Override
            public void onResponse(Call<List<FieldOption>> call, Response<List<FieldOption>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    fieldOptions.clear();
                    fieldOptions.addAll(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<FieldOption>> call, Throwable t) {
                Toast.makeText(WeatherDataActivity.this, "Failed to load fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddEditWeatherDialog(final WeatherDataRead weatherData) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(weatherData == null ? "Add Weather Data" : "Edit Weather Data");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_weather, null);
        final Spinner spField = view.findViewById(R.id.spField);
        final EditText etTemperature = view.findViewById(R.id.etTemperature);
        final EditText etRainfall = view.findViewById(R.id.etRainfall);
        final EditText etSoilMoisture = view.findViewById(R.id.etSoilMoisture);

        ArrayAdapter<FieldOption> fieldAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fieldOptions);
        fieldAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spField.setAdapter(fieldAdapter);

        if (weatherData != null) {
            for (int i = 0; i < fieldOptions.size(); i++) {
                if (fieldOptions.get(i).getId() == weatherData.getFieldId()) {
                    spField.setSelection(i);
                    break;
                }
            }
            etTemperature.setText(String.valueOf(weatherData.getTemperature()));
            etRainfall.setText(String.valueOf(weatherData.getRainfall()));
            etSoilMoisture.setText(String.valueOf(weatherData.getSoilMoisture()));
        }

        builder.setView(view);

        builder.setPositiveButton(weatherData == null ? "Add" : "Update", (dialog, which) -> {
            int selectedFieldId = ((FieldOption) spField.getSelectedItem()).getId();
            float temperature = Float.parseFloat(etTemperature.getText().toString().trim());
            float rainfall = Float.parseFloat(etRainfall.getText().toString().trim());
            float soilMoisture = Float.parseFloat(etSoilMoisture.getText().toString().trim());

            if (weatherData == null) {
                WeatherDataCreate newWeatherData = new WeatherDataCreate(selectedFieldId, temperature, rainfall, soilMoisture);
                createWeatherData(newWeatherData);
            } else {
                WeatherDataUpdate updatedWeatherData = new WeatherDataUpdate(selectedFieldId, temperature, rainfall, soilMoisture);
                updateWeatherData(weatherData.getId(), updatedWeatherData);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void createWeatherData(WeatherDataCreate newWeatherData) {
        WeatherApi weatherApi = ApiClient.getClient(this).create(WeatherApi.class);
        Call<WeatherDataRead> call = weatherApi.createWeatherData(newWeatherData);
        call.enqueue(new Callback<WeatherDataRead>() {
            @Override
            public void onResponse(Call<WeatherDataRead> call, Response<WeatherDataRead> response) {
                if (response.isSuccessful()) {
                    loadWeatherData();
                    Toast.makeText(WeatherDataActivity.this, "Weather data created successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(WeatherDataActivity.this, "Failed to create weather data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherDataRead> call, Throwable t) {
                Toast.makeText(WeatherDataActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateWeatherData(int weatherId, WeatherDataUpdate updatedWeatherData) {
        WeatherApi weatherApi = ApiClient.getClient(this).create(WeatherApi.class);
        Call<WeatherDataRead> call = weatherApi.updateWeatherData(weatherId, updatedWeatherData);
        call.enqueue(new Callback<WeatherDataRead>() {
            @Override
            public void onResponse(Call<WeatherDataRead> call, Response<WeatherDataRead> response) {
                if (response.isSuccessful()) {
                    loadWeatherData();
                    Toast.makeText(WeatherDataActivity.this, "Weather data updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(WeatherDataActivity.this, "Failed to update weather data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherDataRead> call, Throwable t) {
                Toast.makeText(WeatherDataActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteWeatherData(int weatherId) {
        WeatherApi weatherApi = ApiClient.getClient(this).create(WeatherApi.class);
        Call<Void> call = weatherApi.deleteWeatherData(weatherId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    loadWeatherData();
                    Toast.makeText(WeatherDataActivity.this, "Weather data deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(WeatherDataActivity.this, "Failed to delete weather data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(WeatherDataActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
