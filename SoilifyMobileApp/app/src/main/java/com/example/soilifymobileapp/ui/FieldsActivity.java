package com.example.soilifymobileapp.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soilifymobileapp.R;
import com.example.soilifymobileapp.models.FieldCreate;
import com.example.soilifymobileapp.models.FieldRead;
import com.example.soilifymobileapp.models.FieldUpdate;
import com.example.soilifymobileapp.network.ApiClient;
import com.example.soilifymobileapp.network.FieldsApi;
import com.example.soilifymobileapp.ui.adapters.FieldsAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FieldsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewFields;
    private FloatingActionButton fabAddField;
    private FieldsAdapter adapter;
    private List<FieldRead> fieldList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fields);

        recyclerViewFields = findViewById(R.id.recyclerViewFields);
        fabAddField = findViewById(R.id.fabAddField);

        recyclerViewFields.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FieldsAdapter(fieldList, new FieldsAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(FieldRead field) {
                showAddFieldDialog(field);
            }

            @Override
            public void onDeleteClick(FieldRead field) {
                deleteField(field.getId());
            }
        });
        recyclerViewFields.setAdapter(adapter);

        fabAddField.setOnClickListener(v -> showAddFieldDialog(null));

        loadFields();
    }

    private void loadFields() {
        FieldsApi fieldsApi = ApiClient.getClient(getToken()).create(FieldsApi.class);
        Call<List<FieldRead>> call = fieldsApi.getAllFields();
        call.enqueue(new Callback<List<FieldRead>>() {
            @Override
            public void onResponse(Call<List<FieldRead>> call, Response<List<FieldRead>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    fieldList.clear();
                    fieldList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(FieldsActivity.this, "Failed to load fields", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<FieldRead>> call, Throwable t) {
                Toast.makeText(FieldsActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddFieldDialog(final FieldRead field) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(field == null ? "Add Field" : "Edit Field");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_field, null);
        final EditText etFieldName = view.findViewById(R.id.etFieldName);
        final EditText etSoilType = view.findViewById(R.id.etSoilType);
        final EditText etCropType = view.findViewById(R.id.etCropType);
        final EditText etSize = view.findViewById(R.id.etSize);
        etSize.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        if (field != null) {
            etFieldName.setText(field.getFieldName());
            etSoilType.setText(field.getSoilType());
            etCropType.setText(field.getCropType());
            etSize.setText(String.valueOf(field.getSizeHectares()));
        }

        builder.setView(view);

        builder.setPositiveButton(field == null ? "Add" : "Update", (dialog, which) -> {
            String fieldName = etFieldName.getText().toString().trim();
            String soilType = etSoilType.getText().toString().trim();
            String cropType = etCropType.getText().toString().trim();
            float size = Float.parseFloat(etSize.getText().toString().trim());

            if (field == null) {
                FieldCreate newField = new FieldCreate(fieldName, soilType, cropType, size);
                createField(newField);
            } else {
                FieldUpdate updatedField = new FieldUpdate(fieldName, soilType, cropType, size);
                updateField(field.getId(), updatedField);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void createField(FieldCreate newField) {
        FieldsApi fieldsApi = ApiClient.getClient(getToken()).create(FieldsApi.class);
        Call<FieldRead> call = fieldsApi.createField(newField);
        call.enqueue(new Callback<FieldRead>() {
            @Override
            public void onResponse(Call<FieldRead> call, Response<FieldRead> response) {
                if (response.isSuccessful()) {
                    loadFields();
                    Toast.makeText(FieldsActivity.this, "Field created successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FieldsActivity.this, "Failed to create field", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FieldRead> call, Throwable t) {
                Toast.makeText(FieldsActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateField(int fieldId, FieldUpdate updatedField) {
        FieldsApi fieldsApi = ApiClient.getClient(getToken()).create(FieldsApi.class);
        Call<FieldRead> call = fieldsApi.updateField(fieldId, updatedField);
        call.enqueue(new Callback<FieldRead>() {
            @Override
            public void onResponse(Call<FieldRead> call, Response<FieldRead> response) {
                if (response.isSuccessful()) {
                    loadFields();
                    Toast.makeText(FieldsActivity.this, "Field updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FieldsActivity.this, "Failed to update field", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FieldRead> call, Throwable t) {
                Toast.makeText(FieldsActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteField(int fieldId) {
        FieldsApi fieldsApi = ApiClient.getClient(getToken()).create(FieldsApi.class);
        Call<Void> call = fieldsApi.deleteField(fieldId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    loadFields();
                    Toast.makeText(FieldsActivity.this, "Field deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FieldsActivity.this, "Failed to delete field", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(FieldsActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("auth", Context.MODE_PRIVATE);
        return sharedPreferences.getString("token", null);
    }
}
