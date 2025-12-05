package com.example.soilifymobileapp.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soilifymobileapp.R;
import com.example.soilifymobileapp.adapters.AlertsAdapter;
import com.example.soilifymobileapp.models.Alert;
import com.example.soilifymobileapp.models.AlertRead;
import com.example.soilifymobileapp.network.ApiClient;
import com.example.soilifymobileapp.network.AlertsApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlertsActivity extends AppCompatActivity {

    private AlertsAdapter adapter;
    private final List<Alert> alertList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerts);

        RecyclerView rvAlerts = findViewById(R.id.rvAlerts);
        rvAlerts.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AlertsAdapter(this, alertList);
        rvAlerts.setAdapter(adapter);

        loadAlerts();
    }

    private void loadAlerts() {
        AlertsApi alertsApi = ApiClient.getClient(this).create(AlertsApi.class);
        Call<List<AlertRead>> call = alertsApi.getAllAlerts(null, 50);
        call.enqueue(new Callback<List<AlertRead>>() {
            @Override
            public void onResponse(Call<List<AlertRead>> call, Response<List<AlertRead>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    alertList.clear();
                    for (AlertRead alertRead : response.body()) {
                        alertList.add(new Alert(alertRead.getMessage(), alertRead.getFieldName()));
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(AlertsActivity.this, "Failed to load alerts", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<AlertRead>> call, Throwable t) {
                Toast.makeText(AlertsActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
