package com.example.soilifymobileapp.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soilifymobileapp.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;

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
        loadChartData();
        loadSummaryData();
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

    private void loadChartData() {
        // Create sample data entries for the bar chart
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 40f)); // Nitrogen
        entries.add(new BarEntry(1, 65f)); // Phosphorus
        entries.add(new BarEntry(2, 55f)); // Potassium

        // Define labels for the X-axis
        final String[] labels = new String[]{"Nitrogen", "Phosphorus", "Potassium"};

        // Create a data set from the entries
        BarDataSet dataSet = new BarDataSet(entries, "Nutrient Levels");
        dataSet.setColors(Color.rgb(104, 241, 175), Color.rgb(255, 208, 140), Color.rgb(140, 234, 255));
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        // Create BarData object and set it to the chart
        BarData barData = new BarData(dataSet);
        barChart.setData(barData);

        // Apply the labels to the X-axis
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));

        // Refresh the chart to display the new data
        barChart.invalidate();
    }

    private void loadSummaryData() {
        // This is a placeholder for where you might calculate or fetch summary data.
        // For example, you could average the nutrient levels.
        String summary = "Phosphorus levels are optimal. Nitrogen is slightly low. Consider adding a nitrogen-rich fertilizer.";
        tvSummaryData.setText(summary);
    }
}
