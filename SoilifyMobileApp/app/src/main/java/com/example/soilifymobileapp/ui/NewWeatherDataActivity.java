package com.example.soilifymobileapp.ui;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soilifymobileapp.R;
import com.example.soilifymobileapp.models.FieldRead;
import com.example.soilifymobileapp.models.WeatherDataCreate;
import com.example.soilifymobileapp.models.WeatherDataRead;
import com.example.soilifymobileapp.network.ApiClient;
import com.example.soilifymobileapp.network.FieldsApi;
import com.example.soilifymobileapp.network.WeatherApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewWeatherDataActivity extends AppCompatActivity {

    private Spinner spinnerField;
    private EditText editTextTemperature, editTextRainfall, editTextSoilMoisture;
    private Button buttonSave;
    private List<FieldRead> fieldList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_weather_data);

        spinnerField = findViewById(R.id.spinnerField);
        editTextTemperature = findViewById(R.id.editTextTemperature);
        editTextRainfall = findViewById(R.id.editTextRainfall);
        editTextSoilMoisture = findViewById(R.id.editTextSoilMoisture);
        buttonSave = findViewById(R.id.buttonSave);

        loadFields();

        buttonSave.setOnClickListener(v -> saveWeatherData());
    }

    private void loadFields() {
        FieldsApi apiService = ApiClient.getClient(this).create(FieldsApi.class);
        Call<List<FieldRead>> call = apiService.getAllFields();

        call.enqueue(new Callback<List<FieldRead>>() {
            @Override
            public void onResponse(Call<List<FieldRead>> call, Response<List<FieldRead>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    fieldList = response.body();
                    List<String> fieldNames = new ArrayList<>();
                    for (FieldRead field : fieldList) {
                        fieldNames.add(field.getFieldName());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(NewWeatherDataActivity.this,
                            android.R.layout.simple_spinner_item, fieldNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerField.setAdapter(adapter);
                } else {
                    Toast.makeText(NewWeatherDataActivity.this, "Failed to load fields", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<FieldRead>> call, Throwable t) {
                Toast.makeText(NewWeatherDataActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveWeatherData() {
        int selectedFieldPosition = spinnerField.getSelectedItemPosition();
        if (selectedFieldPosition < 0 || selectedFieldPosition >= fieldList.size()) {
            Toast.makeText(this, "Please select a field", Toast.LENGTH_SHORT).show();
            return;
        }

        int fieldId = fieldList.get(selectedFieldPosition).getId();
        String temperatureStr = editTextTemperature.getText().toString().trim();
        String rainfallStr = editTextRainfall.getText().toString().trim();
        String soilMoistureStr = editTextSoilMoisture.getText().toString().trim();

        if (temperatureStr.isEmpty() || rainfallStr.isEmpty() || soilMoistureStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        float temperature = Float.parseFloat(temperatureStr);
        float rainfall = Float.parseFloat(rainfallStr);
        float soilMoisture = Float.parseFloat(soilMoistureStr);

        WeatherDataCreate weatherDataCreate = new WeatherDataCreate(fieldId, temperature, rainfall, soilMoisture);

        WeatherApi apiService = ApiClient.getClient(this).create(WeatherApi.class);
        Call<WeatherDataRead> call = apiService.createWeatherData(weatherDataCreate);

        call.enqueue(new Callback<WeatherDataRead>() {
            @Override
            public void onResponse(Call<WeatherDataRead> call, Response<WeatherDataRead> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(NewWeatherDataActivity.this, "Weather data created successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(NewWeatherDataActivity.this, "Failed to create weather data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherDataRead> call, Throwable t) {
                Toast.makeText(NewWeatherDataActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
