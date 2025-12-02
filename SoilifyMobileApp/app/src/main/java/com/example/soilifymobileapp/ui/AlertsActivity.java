package com.example.soilifymobileapp.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soilifymobileapp.R;
import com.example.soilifymobileapp.adapters.AlertsAdapter;
import com.example.soilifymobileapp.models.Alert;

import java.util.ArrayList;
import java.util.List;

public class AlertsActivity extends AppCompatActivity {

    private RecyclerView rvAlerts;
    private AlertsAdapter adapter;
    private List<Alert> alertList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerts);

        rvAlerts = findViewById(R.id.rvAlerts);
        rvAlerts.setLayoutManager(new LinearLayoutManager(this));

        loadPlaceholderAlerts();

        adapter = new AlertsAdapter(this, alertList);
        rvAlerts.setAdapter(adapter);
    }

    private void loadPlaceholderAlerts() {
        alertList = new ArrayList<>();
        alertList.add(new Alert("Heavy Rain Expected", "Consider delaying fertilizer application. Heavy rain is forecast for the next 24 hours, which could lead to nutrient runoff."));
        alertList.add(new Alert("Pest Advisory", "Aphids have been reported in your area. Inspect your crops for signs of infestation."));
        alertList.add(new Alert("Heat Wave Warning", "Extreme heat is expected this week. Ensure your crops are adequately hydrated to prevent stress."));
    }
}
