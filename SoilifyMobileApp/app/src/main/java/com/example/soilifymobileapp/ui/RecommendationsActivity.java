package com.example.soilifymobileapp.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soilifymobileapp.R;
import com.example.soilifymobileapp.models.ChatRequest;
import com.example.soilifymobileapp.models.ChatResponse;
import com.example.soilifymobileapp.models.Recommendation;
import com.example.soilifymobileapp.network.AiApi;
import com.example.soilifymobileapp.network.ApiClient;
import com.example.soilifymobileapp.ui.adapters.RecommendationsAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecommendationsActivity extends AppCompatActivity {

    private RecommendationsAdapter adapter;
    private final List<Recommendation> recommendationList = new ArrayList<>();
    private EditText etChatMessage;
    private RecyclerView rvRecommendations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations);

        rvRecommendations = findViewById(R.id.rvRecommendations);
        etChatMessage = findViewById(R.id.etChatMessage);
        Button btnSendMessage = findViewById(R.id.btnSendMessage);

        rvRecommendations.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecommendationsAdapter(this, recommendationList);
        rvRecommendations.setAdapter(adapter);

        btnSendMessage.setOnClickListener(v -> {
            String message = etChatMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                getAiRecommendation(message);
            }
        });
    }

    private void getAiRecommendation(String message) {
        AiApi aiApi = ApiClient.getClient(this).create(AiApi.class);
        ChatRequest chatRequest = new ChatRequest(message, null, null);

        recommendationList.add(new Recommendation("You", message));
        adapter.notifyDataSetChanged();

        Call<ChatResponse> call = aiApi.chatWithAi(chatRequest);
        call.enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String aiResponse = response.body().getResponse();
                    recommendationList.add(new Recommendation("AI", aiResponse));
                    adapter.notifyDataSetChanged();
                    // Smooth scroll to the last item
                    if (adapter.getItemCount() > 0) {
                        rvRecommendations.smoothScrollToPosition(adapter.getItemCount() - 1);
                    }
                } else {
                    Toast.makeText(RecommendationsActivity.this, "Failed to get recommendation", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                Toast.makeText(RecommendationsActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
