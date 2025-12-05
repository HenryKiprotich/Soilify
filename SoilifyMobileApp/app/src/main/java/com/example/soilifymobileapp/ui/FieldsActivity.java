package com.example.soilifymobileapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soilifymobileapp.R;
import com.example.soilifymobileapp.models.FieldRead;
import com.example.soilifymobileapp.network.ApiClient;
import com.example.soilifymobileapp.network.FieldsApi;
import com.example.soilifymobileapp.ui.adapters.FieldsAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FieldsActivity extends AppCompatActivity implements FieldsAdapter.OnItemClickListener {

    private RecyclerView recyclerViewFields;
    private FieldsAdapter fieldsAdapter;
    private List<FieldRead> fieldList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fields);

        recyclerViewFields = findViewById(R.id.recyclerViewFields);
        FloatingActionButton fabAddField = findViewById(R.id.fabAddField);

        recyclerViewFields.setLayoutManager(new LinearLayoutManager(this));
        fieldsAdapter = new FieldsAdapter(fieldList, this);
        recyclerViewFields.setAdapter(fieldsAdapter);

        fabAddField.setOnClickListener(view -> {
            Intent intent = new Intent(FieldsActivity.this, NewFieldActivity.class);
            startActivity(intent);
        });

        loadFields();
    }

    private void loadFields() {
    FieldsApi apiService = ApiClient.getClient(this).create(FieldsApi.class);
    Call<List<FieldRead>> call = apiService.getAllFields();

        call.enqueue(new Callback<List<FieldRead>>() {
            @Override
            public void onResponse(Call<List<FieldRead>> call, Response<List<FieldRead>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    fieldList = response.body();
                    fieldsAdapter.setFields(fieldList);
                } else {
                    Toast.makeText(FieldsActivity.this, "Failed to load fields", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<FieldRead>> call, Throwable t) {
                Toast.makeText(FieldsActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onEditClick(FieldRead field) {
        Intent intent = new Intent(this, EditFieldActivity.class);
        intent.putExtra("FIELD_ID", field.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(FieldRead field) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Field")
                .setMessage("Are you sure you want to delete this field?")
                .setPositiveButton("Delete", (dialog, which) -> deleteField(field.getId()))
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteField(int fieldId) {
        FieldsApi apiService = ApiClient.getClient(this).create(FieldsApi.class);
        Call<Void> call = apiService.deleteField(fieldId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(FieldsActivity.this, "Field deleted successfully", Toast.LENGTH_SHORT).show();
                    loadFields(); // Refresh the list
                } else {
                    Toast.makeText(FieldsActivity.this, "Failed to delete field", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(FieldsActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFields(); // Refresh fields when returning to the activity
    }
}
