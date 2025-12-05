package com.example.soilifymobileapp.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soilifymobileapp.R;
import com.example.soilifymobileapp.models.FarmOverview;
import com.example.soilifymobileapp.models.FertilizerByType;
import com.example.soilifymobileapp.models.FertilizerSummary;
import com.example.soilifymobileapp.network.AnalyticsApi;
import com.example.soilifymobileapp.network.ApiClient;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnalyticsActivity extends AppCompatActivity {

    private BarChart barChart;
    private TextView tvSummaryData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        // Initialize UI components from the layout
        barChart = findViewById(R.id.barChart);
        tvSummaryData = findViewById(R.id.tvSummaryData);

        // Setup the chart and load data
        setupBarChart();
        loadAnalyticsData();
    }

    private void setupBarChart() {
        // Customize the chart appearance
        barChart.getDescription().setEnabled(false); // No description text
        barChart.setDrawGridBackground(false); // No grid background
        barChart.setDrawBarShadow(false); // No bar shadows
        barChart.setFitBars(true); // Make the bars fit inside the chart area
        barChart.animateY(1000); // Animate bars on the Y-axis

        // Customize X-axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setGranularity(1f); // One label per bar

        // Customize Y-axis (left)
        barChart.getAxisLeft().setAxisMinimum(0f); // Start at 0
        barChart.getAxisLeft().setTextColor(Color.BLACK);

        // Hide Y-axis (right)
        barChart.getAxisRight().setEnabled(false);

        // Hide legend
        barChart.getLegend().setEnabled(false);
    }

    private void loadAnalyticsData() {
        AnalyticsApi apiService = ApiClient.getClient(this).create(AnalyticsApi.class);

        // Fetch farm overview
        Call<FarmOverview> farmOverviewCall = apiService.getFarmOverview();
        farmOverviewCall.enqueue(new Callback<FarmOverview>() {
            @Override
            public void onResponse(Call<FarmOverview> call, Response<FarmOverview> response) {
                if (response.isSuccessful() && response.body() != null) {
                    FarmOverview overview = response.body();
                    StringBuilder summary = new StringBuilder();
                    summary.append("Total Fields: ").append(overview.getTotalFields()).append("\n");
                    summary.append("Total Area: ").append(String.format("%.2f", overview.getTotalAreaHectares())).append(" hectares\n");
                    
                    FertilizerSummary fertSummary = overview.getFertilizerSummary();
                    if (fertSummary != null) {
                        String mostUsed = fertSummary.getMostUsedFertilizer();
                        summary.append("Most Used Fertilizer: ").append(mostUsed != null ? mostUsed : "N/A").append("\n");
                        summary.append("Total Applications: ").append(fertSummary.getTotalApplications()).append("\n");
                        summary.append("Total Fertilizer: ").append(String.format("%.2f", fertSummary.getTotalAmountKg())).append(" kg");
                    } else {
                        summary.append("No fertilizer data available");
                    }
                    tvSummaryData.setText(summary.toString());
                } else {
                    tvSummaryData.setText("No summary data available");
                }
            }

            @Override
            public void onFailure(Call<FarmOverview> call, Throwable t) {
                tvSummaryData.setText("Failed to load summary: " + t.getMessage());
            }
        });

        // Fetch fertilizer by type
        Call<List<FertilizerByType>> fertilizerByTypeCall = apiService.getFertilizerByType();
        fertilizerByTypeCall.enqueue(new Callback<List<FertilizerByType>>() {
            @Override
            public void onResponse(Call<List<FertilizerByType>> call, Response<List<FertilizerByType>> response) {
                if (response.isSuccessful()) {
                    List<FertilizerByType> fertilizerData = response.body();
                    if (fertilizerData != null) {
                        updateBarChart(fertilizerData);
                    }
                } else {
                    // Handle error
                }
            }

            @Override
            public void onFailure(Call<List<FertilizerByType>> call, Throwable t) {
                // Handle exception
            }
        });
    }

    private void updateBarChart(List<FertilizerByType> fertilizerData) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        for (int i = 0; i < fertilizerData.size(); i++) {
            entries.add(new BarEntry(i, fertilizerData.get(i).getTotalAmountKg()));
            labels.add(fertilizerData.get(i).getFertiliserType());
        }

        BarDataSet dataSet = new BarDataSet(entries, "Fertilizer Usage (kg)");
        dataSet.setColors(Color.rgb(104, 241, 175), Color.rgb(255, 208, 140), Color.rgb(140, 234, 255));
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));

        barChart.invalidate();
    }
}
