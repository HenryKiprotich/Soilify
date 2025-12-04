package com.example.soilifymobileapp.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soilifymobileapp.R;
import com.example.soilifymobileapp.models.AlertRead;
import com.example.soilifymobileapp.network.ApiClient;
import com.example.soilifymobileapp.network.AlertsApi;
import com.example.soilifymobileapp.ui.adapters.AlertsAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlertsActivity extends AppCompatActivity {

    private RecyclerView rvAlerts;
    private AlertsAdapter adapter;
    private List<AlertRead> alertList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerts);

        rvAlerts = findViewById(R.id.rvAlerts);
        rvAlerts.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AlertsAdapter(this, alertList);
        rvAlerts.setAdapter(adapter);

        loadAlerts();
    }

    private void loadAlerts() {
        AlertsApi alertsApi = ApiClient.getClient(getToken()).create(AlertsApi.class);
        Call<List<AlertRead>> call = alertsApi.getAllAlerts(null, 50);
        call.enqueue(new Callback<List<AlertRead>>() {
            @Override
            public void onResponse(Call<List<AlertRead>> call, Response<List<AlertRead>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    alertList.clear();
                    alertList.addAll(response.body());
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

    private String getToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("auth", Context.MODE_PRIVATE);
        return sharedPreferences.getString("token", null);
    }
}
