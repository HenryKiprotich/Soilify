package com.example.soilifymobileapp.models;

import com.google.gson.annotations.SerializedName;

public class ChatResponse {
    @SerializedName("response")
    private String response;

    @SerializedName("conversation_id")
    private String conversationId;

    @SerializedName("session_id")
    private String sessionId;

    @SerializedName("model")
    private String model;

    // Getters and Setters
    public String getResponse() {
        return response;
    }

    public String getConversationId() {
        return conversationId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getModel() {
        return model;
    }
}
