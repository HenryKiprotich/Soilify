package com.example.soilifymobileapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.soilifymobileapp.R;

public class DashboardActivity extends AppCompatActivity {

    private LinearLayout btnRecord, btnRecommendations, btnAnalytics, btnAlerts;
    private TextView tvWeatherSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Bind Views
        btnRecord = findViewById(R.id.btnRecord);
        btnRecommendations = findViewById(R.id.btnRecommendations);
        btnAnalytics = findViewById(R.id.btnAnalytics);
        btnAlerts = findViewById(R.id.btnAlerts);
        tvWeatherSummary = findViewById(R.id.tvWeatherSummary);

        // TODO: Load weather from GET /weather or similar
        tvWeatherSummary.setText("24°C, Cloudy. High humidity expected.");

        // Navigation Logic
        btnRecord.setOnClickListener(v -> startActivity(new Intent(this, RecordFertiliserActivity.class)));
        btnRecommendations.setOnClickListener(v -> startActivity(new Intent(this, RecommendationsActivity.class)));
        btnAnalytics.setOnClickListener(v -> startActivity(new Intent(this, AnalyticsActivity.class)));
        btnAlerts.setOnClickListener(v -> startActivity(new Intent(this, AlertsActivity.class)));
    }
}