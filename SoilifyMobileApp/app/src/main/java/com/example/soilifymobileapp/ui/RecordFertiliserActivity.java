package com.example.soilifymobileapp.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.soilifymobileapp.R;

public class RecordFertiliserActivity extends AppCompatActivity {

    private Spinner spCropType, spSoilType, spFertiliserType;
    private EditText etAmount, etFarmSize;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_fertiliser);

        spCropType = findViewById(R.id.spCropType);
        spSoilType = findViewById(R.id.spSoilType);
        spFertiliserType = findViewById(R.id.spFertiliserType);
        etAmount = findViewById(R.id.etAmount);
        etFarmSize = findViewById(R.id.etFarmSize);
        btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(v -> {
            // TODO: Validate inputs and call POST /fertiliser
            String crop = spCropType.getSelectedItem().toString();
            String amount = etAmount.getText().toString();

            if(amount.isEmpty()) {
                etAmount.setError("Required");
                return;
            }

            Toast.makeText(this, "Usage Recorded: " + crop, Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}