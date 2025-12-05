package com.example.soilifymobileapp.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soilifymobileapp.R;
import com.example.soilifymobileapp.models.AIConversation;
import com.example.soilifymobileapp.models.ChatRequest;
import com.example.soilifymobileapp.models.ChatResponse;
import com.example.soilifymobileapp.network.AiApi;
import com.example.soilifymobileapp.network.ApiClient;
import com.example.soilifymobileapp.ui.adapters.AIConversationsAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AIActivity extends AppCompatActivity {

    private AIConversationsAdapter adapter;
    private final List<AIConversation> conversationList = new ArrayList<>();
    private EditText etChatMessage;
    private RecyclerView rvConversations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai);

        rvConversations = findViewById(R.id.rvConversations);
        etChatMessage = findViewById(R.id.etChatMessage);
        Button btnSendMessage = findViewById(R.id.btnSendMessage);

        rvConversations.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AIConversationsAdapter(this, conversationList);
        rvConversations.setAdapter(adapter);

        btnSendMessage.setOnClickListener(v -> {
            String message = etChatMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                getAiResponse(message);
            }
        });
    }

    private void getAiResponse(String message) {
        AiApi aiApi = ApiClient.getClient(this).create(AiApi.class);
        ChatRequest chatRequest = new ChatRequest(message, null, null);

        conversationList.add(new AIConversation("You", message));
        adapter.notifyDataSetChanged();
        etChatMessage.setText("");

        Call<ChatResponse> call = aiApi.chatWithAi(chatRequest);
        call.enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String aiResponse = response.body().getResponse();
                    conversationList.add(new AIConversation("AI", aiResponse));
                    adapter.notifyDataSetChanged();
                    // Smooth scroll to the last item
                    if (adapter.getItemCount() > 0) {
                        rvConversations.smoothScrollToPosition(adapter.getItemCount() - 1);
                    }
                } else {
                    Toast.makeText(AIActivity.this, "Failed to get response", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                Toast.makeText(AIActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
