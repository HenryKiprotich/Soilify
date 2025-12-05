package com.example.soilifymobileapp.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soilifymobileapp.R;
import com.example.soilifymobileapp.models.AIConversation;
import com.example.soilifymobileapp.models.ChatRequest;
import com.example.soilifymobileapp.models.ChatResponse;
import com.example.soilifymobileapp.models.FieldAnalysisResponse;
import com.example.soilifymobileapp.models.FieldRead;
import com.example.soilifymobileapp.models.NLToSQLRequest;
import com.example.soilifymobileapp.models.NLToSQLResponse;
import com.example.soilifymobileapp.network.AiApi;
import com.example.soilifymobileapp.network.ApiClient;
import com.example.soilifymobileapp.network.FieldsApi;
import com.example.soilifymobileapp.ui.adapters.AIConversationsAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AIActivity extends AppCompatActivity {

    private enum ChatMode {
        CHAT,       // General farming advice
        QUERY,      // Natural language to SQL queries
        ANALYZE     // Field analysis
    }

    private AIConversationsAdapter adapter;
    private final List<AIConversation> conversationList = new ArrayList<>();
    private EditText etChatMessage;
    private RecyclerView rvConversations;
    private TextView tvModeHint;
    private LinearLayout layoutFieldSelection;
    private Spinner spinnerFields;
    private Button btnModeChat, btnModeQuery, btnModeAnalyze, btnAnalyzeField;
    private LinearLayout layoutChatbox;

    private ChatMode currentMode = ChatMode.CHAT;
    private List<FieldRead> fieldsList = new ArrayList<>();
    private FieldRead selectedField = null;
    private String sessionId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai);

        initializeViews();
        setupRecyclerView();
        setupModeButtons();
        setupSendButton();
        setupFieldSpinner();
        loadUserFields();

        // Set initial mode
        setMode(ChatMode.CHAT);
    }

    private void initializeViews() {
        rvConversations = findViewById(R.id.rvConversations);
        etChatMessage = findViewById(R.id.etChatMessage);
        tvModeHint = findViewById(R.id.tvModeHint);
        layoutFieldSelection = findViewById(R.id.layout_field_selection);
        spinnerFields = findViewById(R.id.spinnerFields);
        btnModeChat = findViewById(R.id.btnModeChat);
        btnModeQuery = findViewById(R.id.btnModeQuery);
        btnModeAnalyze = findViewById(R.id.btnModeAnalyze);
        btnAnalyzeField = findViewById(R.id.btnAnalyzeField);
        layoutChatbox = findViewById(R.id.layout_chatbox);
    }

    private void setupRecyclerView() {
        rvConversations.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AIConversationsAdapter(this, conversationList);
        rvConversations.setAdapter(adapter);
    }

    private void setupModeButtons() {
        btnModeChat.setOnClickListener(v -> setMode(ChatMode.CHAT));
        btnModeQuery.setOnClickListener(v -> setMode(ChatMode.QUERY));
        btnModeAnalyze.setOnClickListener(v -> setMode(ChatMode.ANALYZE));

        btnAnalyzeField.setOnClickListener(v -> {
            if (selectedField != null) {
                analyzeSelectedField();
            } else {
                Toast.makeText(this, "Please select a field first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSendButton() {
        Button btnSendMessage = findViewById(R.id.btnSendMessage);
        btnSendMessage.setOnClickListener(v -> {
            String message = etChatMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                sendMessage(message);
            }
        });
    }

    private void setupFieldSpinner() {
        spinnerFields.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < fieldsList.size()) {
                    selectedField = fieldsList.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedField = null;
            }
        });
    }

    private void setMode(ChatMode mode) {
        currentMode = mode;

        // Update button states (highlight selected mode)
        btnModeChat.setAlpha(mode == ChatMode.CHAT ? 1.0f : 0.6f);
        btnModeQuery.setAlpha(mode == ChatMode.QUERY ? 1.0f : 0.6f);
        btnModeAnalyze.setAlpha(mode == ChatMode.ANALYZE ? 1.0f : 0.6f);

        // Show/hide UI elements based on mode
        switch (mode) {
            case CHAT:
                layoutFieldSelection.setVisibility(View.GONE);
                layoutChatbox.setVisibility(View.VISIBLE);
                tvModeHint.setText("Ask me anything about farming!");
                etChatMessage.setHint("Enter your farming question...");
                break;
            case QUERY:
                layoutFieldSelection.setVisibility(View.GONE);
                layoutChatbox.setVisibility(View.VISIBLE);
                tvModeHint.setText("Ask questions about your data (e.g., 'What's my total fertilizer usage?')");
                etChatMessage.setHint("Ask about your farm data...");
                break;
            case ANALYZE:
                layoutFieldSelection.setVisibility(View.VISIBLE);
                layoutChatbox.setVisibility(View.GONE);
                tvModeHint.setText("Select a field to get AI-powered analysis and recommendations");
                break;
        }
    }

    private void loadUserFields() {
        FieldsApi fieldsApi = ApiClient.getClient(this).create(FieldsApi.class);
        Call<List<FieldRead>> call = fieldsApi.getAllFields();

        call.enqueue(new Callback<List<FieldRead>>() {
            @Override
            public void onResponse(Call<List<FieldRead>> call, Response<List<FieldRead>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    fieldsList = response.body();
                    updateFieldSpinner();
                } else {
                    Toast.makeText(AIActivity.this, "Failed to load fields", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<FieldRead>> call, Throwable t) {
                Toast.makeText(AIActivity.this, "Error loading fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateFieldSpinner() {
        List<String> fieldNames = new ArrayList<>();
        for (FieldRead field : fieldsList) {
            fieldNames.add(field.getFieldName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                fieldNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFields.setAdapter(adapter);

        if (!fieldsList.isEmpty()) {
            selectedField = fieldsList.get(0);
        }
    }

    private void sendMessage(String message) {
        switch (currentMode) {
            case CHAT:
                getChatResponse(message);
                break;
            case QUERY:
                getQueryResponse(message);
                break;
            case ANALYZE:
                // In analyze mode, use the Analyze button instead
                Toast.makeText(this, "Use the Analyze button to analyze a field", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void getChatResponse(String message) {
        AiApi aiApi = ApiClient.getClient(this).create(AiApi.class);
        ChatRequest chatRequest = new ChatRequest(message, sessionId, null);

        // Add user message to conversation
        conversationList.add(new AIConversation("You", message));
        etChatMessage.setText("");

        // Add a thinking message
        final int thinkingMessagePosition = conversationList.size();
        conversationList.add(new AIConversation("AI", "Thinking..."));
        adapter.notifyDataSetChanged();
        scrollToBottom();

        Call<ChatResponse> call = aiApi.chatWithAi(chatRequest);
        call.enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                // Remove thinking message
                conversationList.remove(thinkingMessagePosition);

                if (response.isSuccessful() && response.body() != null) {
                    ChatResponse chatResponse = response.body();
                    sessionId = chatResponse.getSessionId(); // Store for conversation continuity
                    conversationList.add(new AIConversation("AI", chatResponse.getResponse()));
                } else {
                    conversationList.add(new AIConversation("AI", "Sorry, I had trouble getting a response. Please try again."));
                    Toast.makeText(AIActivity.this, "Failed to get response", Toast.LENGTH_SHORT).show();
                }
                adapter.notifyDataSetChanged();
                scrollToBottom();
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                // Remove thinking message
                conversationList.remove(thinkingMessagePosition);
                conversationList.add(new AIConversation("AI", "Sorry, an error occurred. Please check your connection and try again."));
                adapter.notifyDataSetChanged();
                scrollToBottom();
                Toast.makeText(AIActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getQueryResponse(String question) {
        AiApi aiApi = ApiClient.getClient(this).create(AiApi.class);
        NLToSQLRequest request = new NLToSQLRequest(question, sessionId);

        // Add user message to conversation
        conversationList.add(new AIConversation("You", question));
        etChatMessage.setText("");

        // Add thinking message
        final int thinkingMessagePosition = conversationList.size();
        conversationList.add(new AIConversation("AI", "Querying data..."));
        adapter.notifyDataSetChanged();
        scrollToBottom();

        Call<NLToSQLResponse> call = aiApi.askWithSql(request);
        call.enqueue(new Callback<NLToSQLResponse>() {
            @Override
            public void onResponse(Call<NLToSQLResponse> call, Response<NLToSQLResponse> response) {
                // Remove thinking message
                conversationList.remove(thinkingMessagePosition);

                if (response.isSuccessful() && response.body() != null) {
                    NLToSQLResponse sqlResponse = response.body();
                    String aiMessage = sqlResponse.getNaturalResponse();
                    conversationList.add(new AIConversation("AI", aiMessage));
                } else {
                    String errorMsg = "Failed to process query.";
                    if (response.code() == 400) {
                        errorMsg = "Invalid query - please try rephrasing.";
                    }
                    conversationList.add(new AIConversation("AI", "Sorry, " + errorMsg));
                    Toast.makeText(AIActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
                adapter.notifyDataSetChanged();
                scrollToBottom();
            }

            @Override
            public void onFailure(Call<NLToSQLResponse> call, Throwable t) {
                // Remove thinking message
                conversationList.remove(thinkingMessagePosition);
                conversationList.add(new AIConversation("AI", "Sorry, an error occurred while querying your data."));
                adapter.notifyDataSetChanged();
                scrollToBottom();
                Toast.makeText(AIActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void analyzeSelectedField() {
        if (selectedField == null) {
            Toast.makeText(this, "Please select a field", Toast.LENGTH_SHORT).show();
            return;
        }

        AiApi aiApi = ApiClient.getClient(this).create(AiApi.class);

        // Add a message showing what we're analyzing
        conversationList.add(new AIConversation("You", "Analyze field: " + selectedField.getFieldName()));
        
        // Add thinking message
        final int thinkingMessagePosition = conversationList.size();
        conversationList.add(new AIConversation("AI", "Analyzing..."));
        adapter.notifyDataSetChanged();
        scrollToBottom();

        // Show loading state
        btnAnalyzeField.setEnabled(false);
        btnAnalyzeField.setText("Analyzing...");

        Call<FieldAnalysisResponse> call = aiApi.analyzeField(selectedField.getId(), sessionId);
        call.enqueue(new Callback<FieldAnalysisResponse>() {
            @Override
            public void onResponse(Call<FieldAnalysisResponse> call, Response<FieldAnalysisResponse> response) {
                btnAnalyzeField.setEnabled(true);
                btnAnalyzeField.setText("Analyze");
                
                // Remove thinking message
                conversationList.remove(thinkingMessagePosition);

                if (response.isSuccessful() && response.body() != null) {
                    FieldAnalysisResponse analysisResponse = response.body();
                    String aiMessage = "📊 Analysis for " + analysisResponse.getFieldName() + ":\n\n" + analysisResponse.getAnalysis();
                    conversationList.add(new AIConversation("AI", aiMessage));
                } else {
                    String errorMsg = "Failed to analyze field.";
                    if (response.code() == 404) {
                        errorMsg = "Field not found or access denied.";
                    }
                    conversationList.add(new AIConversation("AI", "Sorry, " + errorMsg));
                    Toast.makeText(AIActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
                adapter.notifyDataSetChanged();
                scrollToBottom();
            }

            @Override
            public void onFailure(Call<FieldAnalysisResponse> call, Throwable t) {
                btnAnalyzeField.setEnabled(true);
                btnAnalyzeField.setText("Analyze");

                // Remove thinking message
                conversationList.remove(thinkingMessagePosition);
                conversationList.add(new AIConversation("AI", "Sorry, an error occurred during analysis."));
                adapter.notifyDataSetChanged();
                scrollToBottom();
                Toast.makeText(AIActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void scrollToBottom() {
        if (adapter.getItemCount() > 0) {
            rvConversations.smoothScrollToPosition(adapter.getItemCount() - 1);
        }
    }
}
