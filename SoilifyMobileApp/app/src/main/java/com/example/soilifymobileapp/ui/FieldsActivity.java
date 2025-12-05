package com.example.soilifymobileapp.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soilifymobileapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FieldsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fields);

        RecyclerView recyclerViewFields = findViewById(R.id.recyclerViewFields);
        FloatingActionButton fabAddFertiliser = findViewById(R.id.fabAddFertiliser);
    }
}