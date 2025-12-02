package com.example.soilifymobileapp.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soilifymobileapp.R;
import com.example.soilifymobileapp.adapters.RecommendationsAdapter;
import com.example.soilifymobileapp.models.Recommendation;

import java.util.ArrayList;
import java.util.List;

public class RecommendationsActivity extends AppCompatActivity {

    private RecyclerView rvRecommendations;
    private RecommendationsAdapter adapter;
    private List<Recommendation> recommendationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations);

        rvRecommendations = findViewById(R.id.rvRecommendations);
        rvRecommendations.setLayoutManager(new LinearLayoutManager(this));

        loadPlaceholderRecommendations();

        adapter = new RecommendationsAdapter(this, recommendationList);
        rvRecommendations.setAdapter(adapter);
    }

    private void loadPlaceholderRecommendations() {
        recommendationList = new ArrayList<>();
        recommendationList.add(new Recommendation("Add Nitrogen", "Your soil is low on nitrogen. Add a nitrogen-rich fertilizer to improve leaf growth."));
        recommendationList.add(new Recommendation("Adjust pH", "Your soil is too acidic. Consider adding lime to raise the pH for optimal nutrient absorption."));
        recommendationList.add(new Recommendation("Crop Suggestion", "Given your soil's high potassium levels, potatoes or carrots would thrive."));
        recommendationList.add(new Recommendation("Watering Advice", "Water early in the morning to reduce evaporation. Your soil moisture is currently adequate."));
        recommendationList.add(new Recommendation("Crop Rotation", "Rotate crops to prevent soil-borne diseases and nutrient depletion."));
    }
}
