package com.example.soilifymobileapp.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soilifymobileapp.R;
import com.example.soilifymobileapp.models.Field;
import com.example.soilifymobileapp.network.ApiClient;
import com.example.soilifymobileapp.network.FieldApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditFieldActivity extends AppCompatActivity {

    private EditText editTextFieldName, editTextSoilType, editTextCropType, editTextSize;
    private Button buttonSave;
    private int fieldId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_field);

        editTextFieldName = findViewById(R.id.editTextFieldName);
        editTextSoilType = findViewById(R.id.editTextSoilType);
        editTextCropType = findViewById(R.id.editTextCropType);
        editTextSize = findViewById(R.id.editTextSize);
        buttonSave = findViewById(R.id.buttonSave);

        fieldId = getIntent().getIntExtra("FIELD_ID", -1);
        if (fieldId == -1) {
            Toast.makeText(this, "Error: Field ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadFieldDetails();

        buttonSave.setOnClickListener(v -> saveField());
    }

    private void loadFieldDetails() {
        FieldApi apiService = ApiClient.getClient(this).create(FieldApi.class);
        Call<Field> call = apiService.getField(fieldId);

        call.enqueue(new Callback<Field>() {
            @Override
            public void onResponse(Call<Field> call, Response<Field> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Field field = response.body();
                    editTextFieldName.setText(field.getFieldName());
                    editTextSoilType.setText(field.getSoilType());
                    editTextCropType.setText(field.getCropType());
                    editTextSize.setText(String.valueOf(field.getSizeHectares()));
                } else {
                    Toast.makeText(EditFieldActivity.this, "Failed to load field details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Field> call, Throwable t) {
                Toast.makeText(EditFieldActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveField() {
        String fieldName = editTextFieldName.getText().toString().trim();
        String soilType = editTextSoilType.getText().toString().trim();
        String cropType = editTextCropType.getText().toString().trim();
        String sizeStr = editTextSize.getText().toString().trim();

        if (fieldName.isEmpty() || soilType.isEmpty() || cropType.isEmpty() || sizeStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double size = Double.parseDouble(sizeStr);

        Field field = new Field();
        field.setFieldName(fieldName);
        field.setSoilType(soilType);
        field.setCropType(cropType);
        field.setSizeHectares(size);

        FieldApi apiService = ApiClient.getClient(this).create(FieldApi.class);
        Call<Field> call = apiService.updateField(fieldId, field);

        call.enqueue(new Callback<Field>() {
            @Override
            public void onResponse(Call<Field> call, Response<Field> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditFieldActivity.this, "Field updated successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Go back to FieldsActivity
                } else {
                    Toast.makeText(EditFieldActivity.this, "Failed to update field", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Field> call, Throwable t) {
                Toast.makeText(EditFieldActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
