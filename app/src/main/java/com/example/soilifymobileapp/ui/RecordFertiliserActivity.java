package com.example.soilifymobileapp.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soilifymobileapp.R;
import com.example.soilifymobileapp.models.FertiliserUsageCreate;
import com.example.soilifymobileapp.models.FertiliserUsageRead;
import com.example.soilifymobileapp.models.FertiliserUsageUpdate;
import com.example.soilifymobileapp.models.FieldOption;
import com.example.soilifymobileapp.network.ApiClient;
import com.example.soilifymobileapp.network.FertiliserApi;
import com.example.soilifymobileapp.ui.adapters.FertiliserUsageAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecordFertiliserActivity extends AppCompatActivity {

    private FertiliserUsageAdapter adapter;
    private final List<FertiliserUsageRead> usageList = new ArrayList<>();
    private final List<FieldOption> fieldOptions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_fertiliser);

        RecyclerView recyclerViewFertiliser = findViewById(R.id.recyclerViewFertiliser);
        FloatingActionButton fabAddFertiliser = findViewById(R.id.fabAddFertiliser);

        recyclerViewFertiliser.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FertiliserUsageAdapter(usageList, new FertiliserUsageAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(FertiliserUsageRead usage) {
                showAddEditFertiliserDialog(usage);
            }

            @Override
            public void onDeleteClick(FertiliserUsageRead usage) {
                deleteFertiliserUsage(usage.getId());
            }
        });
        recyclerViewFertiliser.setAdapter(adapter);

        fabAddFertiliser.setOnClickListener(v -> showAddEditFertiliserDialog(null));

        loadFertiliserUsage();
        loadFieldOptions();
    }

    private void loadFertiliserUsage() {
        FertiliserApi fertiliserApi = ApiClient.getClient(this).create(FertiliserApi.class);
        Call<List<FertiliserUsageRead>> call = fertiliserApi.getAllFertiliserUsage();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<FertiliserUsageRead>> call, @NonNull Response<List<FertiliserUsageRead>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    usageList.clear();
                    usageList.addAll(response.body());
                    adapter.setUsageList(usageList);
                } else {
                    Toast.makeText(RecordFertiliserActivity.this, "Failed to load records", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<FertiliserUsageRead>> call, @NonNull Throwable t) {
                Toast.makeText(RecordFertiliserActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadFieldOptions() {
        FertiliserApi fertiliserApi = ApiClient.getClient(this).create(FertiliserApi.class);
        Call<List<FieldOption>> call = fertiliserApi.getFieldsForDropdown();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<FieldOption>> call, @NonNull Response<List<FieldOption>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    fieldOptions.clear();
                    fieldOptions.addAll(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<FieldOption>> call, @NonNull Throwable t) {
                Toast.makeText(RecordFertiliserActivity.this, "Failed to load fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddEditFertiliserDialog(final FertiliserUsageRead usage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(usage == null ? "Add Fertiliser Record" : "Edit Fertiliser Record");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_fertiliser, null);
        final Spinner spField = view.findViewById(R.id.spField);
        final EditText etFertiliserType = view.findViewById(R.id.etFertiliserType);
        final EditText etAmount = view.findViewById(R.id.etAmount);
        final EditText etWeather = view.findViewById(R.id.etWeather);
        final EditText etNotes = view.findViewById(R.id.etNotes);
        final Button btnSelectDate = view.findViewById(R.id.btnSelectDate);

        ArrayAdapter<FieldOption> fieldAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fieldOptions);
        fieldAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spField.setAdapter(fieldAdapter);

        final Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        
        // Parse existing date if editing
        if (usage != null && usage.getDate() != null) {
            try {
                java.util.Date parsedDate = dateFormat.parse(usage.getDate());
                if (parsedDate != null) {
                    calendar.setTime(parsedDate);
                }
            } catch (Exception e) {
                // Use current date if parsing fails
            }
        }

        btnSelectDate.setText(dateFormat.format(calendar.getTime()));

        btnSelectDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(RecordFertiliserActivity.this,
                    (view1, year, month, dayOfMonth) -> {
                        calendar.set(year, month, dayOfMonth);
                        btnSelectDate.setText(dateFormat.format(calendar.getTime()));
                    },
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        if (usage != null) {
            for (int i = 0; i < fieldOptions.size(); i++) {
                if (fieldOptions.get(i).getId() == usage.getFieldId()) {
                    spField.setSelection(i);
                    break;
                }
            }
            etFertiliserType.setText(usage.getFertiliserType());
            etAmount.setText(String.valueOf(usage.getAmountKg()));
            etWeather.setText(usage.getWeather());
            etNotes.setText(usage.getNotes());
        }

        builder.setView(view);

        builder.setPositiveButton(usage == null ? "Add" : "Update", (dialog, which) -> {
            int selectedFieldId = ((FieldOption) spField.getSelectedItem()).getId();
            String fertiliserType = etFertiliserType.getText().toString().trim();
            float amount = Float.parseFloat(etAmount.getText().toString().trim());
            String weather = etWeather.getText().toString().trim();
            String notes = etNotes.getText().toString().trim();
            String dateStr = dateFormat.format(calendar.getTime()); // Format as YYYY-MM-DD string

            if (usage == null) {
                FertiliserUsageCreate newUsage = new FertiliserUsageCreate(selectedFieldId, fertiliserType, amount, weather, notes, dateStr);
                createFertiliserUsage(newUsage);
            } else {
                FertiliserUsageUpdate updatedUsage = new FertiliserUsageUpdate(selectedFieldId, fertiliserType, amount, weather, notes, dateStr);
                updateFertiliserUsage(usage.getId(), updatedUsage);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void createFertiliserUsage(FertiliserUsageCreate newUsage) {
        FertiliserApi fertiliserApi = ApiClient.getClient(this).create(FertiliserApi.class);
        Call<FertiliserUsageRead> call = fertiliserApi.createFertiliserUsage(newUsage);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<FertiliserUsageRead> call, @NonNull Response<FertiliserUsageRead> response) {
                if (response.isSuccessful()) {
                    loadFertiliserUsage();
                    Toast.makeText(RecordFertiliserActivity.this, "Record created successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RecordFertiliserActivity.this, "Failed to create record", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<FertiliserUsageRead> call, @NonNull Throwable t) {
                Toast.makeText(RecordFertiliserActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateFertiliserUsage(int usageId, FertiliserUsageUpdate updatedUsage) {
        FertiliserApi fertiliserApi = ApiClient.getClient(this).create(FertiliserApi.class);
        Call<FertiliserUsageRead> call = fertiliserApi.updateFertiliserUsage(usageId, updatedUsage);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<FertiliserUsageRead> call, @NonNull Response<FertiliserUsageRead> response) {
                if (response.isSuccessful()) {
                    loadFertiliserUsage();
                    Toast.makeText(RecordFertiliserActivity.this, "Record updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RecordFertiliserActivity.this, "Failed to update record", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<FertiliserUsageRead> call, @NonNull Throwable t) {
                Toast.makeText(RecordFertiliserActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteFertiliserUsage(int usageId) {
        FertiliserApi fertiliserApi = ApiClient.getClient(this).create(FertiliserApi.class);
        Call<Void> call = fertiliserApi.deleteFertiliserUsage(usageId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    loadFertiliserUsage();
                    Toast.makeText(RecordFertiliserActivity.this, "Record deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RecordFertiliserActivity.this, "Failed to delete record", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(RecordFertiliserActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
