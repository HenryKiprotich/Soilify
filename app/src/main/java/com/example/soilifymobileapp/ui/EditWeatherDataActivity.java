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
import com.example.soilifymobileapp.models.WeatherDataRead;
import com.example.soilifymobileapp.models.WeatherDataUpdate;
import com.example.soilifymobileapp.network.ApiClient;
import com.example.soilifymobileapp.network.FieldsApi;
import com.example.soilifymobileapp.network.WeatherApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditWeatherDataActivity extends AppCompatActivity {

    private Spinner spinnerField;
    private EditText editTextTemperature, editTextRainfall, editTextSoilMoisture;
    private Button buttonSave;
    private List<FieldRead> fieldList = new ArrayList<>();
    private int weatherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_weather_data);

        spinnerField = findViewById(R.id.spinnerField);
        editTextTemperature = findViewById(R.id.editTextTemperature);
        editTextRainfall = findViewById(R.id.editTextRainfall);
        editTextSoilMoisture = findViewById(R.id.editTextSoilMoisture);
        buttonSave = findViewById(R.id.buttonSave);

        weatherId = getIntent().getIntExtra("WEATHER_ID", -1);
        if (weatherId == -1) {
            Toast.makeText(this, "Error: Weather ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadFieldsAndWeatherData();

        buttonSave.setOnClickListener(v -> saveWeatherData());
    }

    private void loadFieldsAndWeatherData() {
        FieldsApi fieldsApiService = ApiClient.getClient(this).create(FieldsApi.class);
        Call<List<FieldRead>> fieldsCall = fieldsApiService.getAllFields();

        fieldsCall.enqueue(new Callback<List<FieldRead>>() {
            @Override
            public void onResponse(Call<List<FieldRead>> call, Response<List<FieldRead>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    fieldList = response.body();
                    List<String> fieldNames = new ArrayList<>();
                    for (FieldRead field : fieldList) {
                        fieldNames.add(field.getFieldName());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(EditWeatherDataActivity.this,
                            android.R.layout.simple_spinner_item, fieldNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerField.setAdapter(adapter);

                    loadWeatherData();
                } else {
                    Toast.makeText(EditWeatherDataActivity.this, "Failed to load fields", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<FieldRead>> call, Throwable t) {
                Toast.makeText(EditWeatherDataActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadWeatherData() {
        WeatherApi weatherApiService = ApiClient.getClient(this).create(WeatherApi.class);
        Call<WeatherDataRead> weatherCall = weatherApiService.getWeatherData(weatherId);

        weatherCall.enqueue(new Callback<WeatherDataRead>() {
            @Override
            public void onResponse(Call<WeatherDataRead> call, Response<WeatherDataRead> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherDataRead weatherData = response.body();
                    editTextTemperature.setText(String.valueOf(weatherData.getTemperature()));
                    editTextRainfall.setText(String.valueOf(weatherData.getRainfall()));
                    editTextSoilMoisture.setText(String.valueOf(weatherData.getSoilMoisture()));

                    for (int i = 0; i < fieldList.size(); i++) {
                        if (fieldList.get(i).getId() == weatherData.getFieldId()) {
                            spinnerField.setSelection(i);
                            break;
                        }
                    }
                } else {
                    Toast.makeText(EditWeatherDataActivity.this, "Failed to load weather data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherDataRead> call, Throwable t) {
                Toast.makeText(EditWeatherDataActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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

        WeatherDataUpdate weatherDataUpdate = new WeatherDataUpdate(fieldId, temperature, rainfall, soilMoisture);

        WeatherApi apiService = ApiClient.getClient(this).create(WeatherApi.class);
        Call<WeatherDataRead> call = apiService.updateWeatherData(weatherId, weatherDataUpdate);

        call.enqueue(new Callback<WeatherDataRead>() {
            @Override
            public void onResponse(Call<WeatherDataRead> call, Response<WeatherDataRead> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditWeatherDataActivity.this, "Weather data updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditWeatherDataActivity.this, "Failed to update weather data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherDataRead> call, Throwable t) {
                Toast.makeText(EditWeatherDataActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
