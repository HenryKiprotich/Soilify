package com.example.soilifymobileapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soilifymobileapp.R;
import com.example.soilifymobileapp.adapters.AlertsAdapter;
import com.example.soilifymobileapp.models.Alert;
import com.example.soilifymobileapp.models.DashboardResponse;
import com.example.soilifymobileapp.models.RecentAlert;
import com.example.soilifymobileapp.network.ApiClient;
import com.example.soilifymobileapp.network.HomeApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {

    private TextView textGreeting, textUserName, textLocation, textTotalFields, textPendingAlerts, textWeatherToday;
    private RecyclerView recyclerAlerts;
    private CardView layoutEmptyState;
    private Button btnMyFields, btnFertilizerUsage, btnWeatherData, btnAlerts, btnAIAdvisor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Bind Views
        textGreeting = findViewById(R.id.textGreeting);
        textUserName = findViewById(R.id.textUserName);
        textLocation = findViewById(R.id.textLocation);
        textTotalFields = findViewById(R.id.textTotalFields);
        textPendingAlerts = findViewById(R.id.textPendingAlerts);
        textWeatherToday = findViewById(R.id.textWeatherToday);
        recyclerAlerts = findViewById(R.id.recyclerAlerts);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        btnMyFields = findViewById(R.id.btnMyFields);
        btnFertilizerUsage = findViewById(R.id.btnFertilizerUsage);
        btnWeatherData = findViewById(R.id.btnWeatherData);
        btnAlerts = findViewById(R.id.btnAlerts);
        btnAIAdvisor = findViewById(R.id.btnAIAdvisor);

        // Setup RecyclerView
        recyclerAlerts.setLayoutManager(new LinearLayoutManager(this));

        // Load dashboard data
        loadDashboard();

        // Button listeners
        btnMyFields.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, AnalyticsActivity.class));
        });
        btnFertilizerUsage.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, RecordFertiliserActivity.class));
        });
        btnWeatherData.setOnClickListener(v -> {
            // There is no WeatherDataActivity yet, so this button does nothing.
        });
        btnAlerts.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, AlertsActivity.class));
        });
        btnAIAdvisor.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, RecommendationsActivity.class));
        });
    }

    private void loadDashboard() {
        // Use getClient(context) to automatically include authentication token
        HomeApi apiService = ApiClient.getClient(this).create(HomeApi.class);
        Call<DashboardResponse> call = apiService.getDashboard();
        call.enqueue(new Callback<DashboardResponse>() {
            @Override
            public void onResponse(Call<DashboardResponse> call, Response<DashboardResponse> response) {
                if (response.isSuccessful()) {
                    DashboardResponse dashboard = response.body();
                    if (dashboard != null) {
                        // Update UI
                        textGreeting.setText(dashboard.getWelcome().getGreetingMessage());
                        textUserName.setText(dashboard.getWelcome().getFullName());
                        textLocation.setText(dashboard.getWelcome().getLocation());

                        textTotalFields.setText(String.valueOf(dashboard.getQuickStats().getTotalFields()));
                        textPendingAlerts.setText(String.valueOf(dashboard.getQuickStats().getPendingAlerts()));
                        textWeatherToday.setText(String.valueOf(dashboard.getQuickStats().getWeatherRecordsToday()));

                        setupAlertsList(dashboard.getRecentAlerts());

                        layoutEmptyState.setVisibility(dashboard.isHasFields() ? View.GONE : View.VISIBLE);
                    }
                } else {
                    // Handle error - could be 401 Unauthorized
                    Toast.makeText(DashboardActivity.this, 
                        "Failed to load dashboard: " + response.code(), 
                        Toast.LENGTH_SHORT).show();
                    
                    // If 401, redirect to login
                    if (response.code() == 401) {
                        redirectToLogin();
                    }
                }
            }

            @Override
            public void onFailure(Call<DashboardResponse> call, Throwable t) {
                // Handle network exception
                Toast.makeText(DashboardActivity.this, 
                    "Network error: " + t.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void redirectToLogin() {
        // Clear saved token
        getSharedPreferences("auth", MODE_PRIVATE).edit().clear().apply();
        // Redirect to login
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setupAlertsList(List<RecentAlert> recentAlerts) {
        List<Alert> alerts = new ArrayList<>();
        for (RecentAlert recentAlert : recentAlerts) {
            alerts.add(new Alert(recentAlert.getMessage(), recentAlert.getFieldName()));
        }
        AlertsAdapter adapter = new AlertsAdapter(this, alerts);
        recyclerAlerts.setAdapter(adapter);
    }
}
