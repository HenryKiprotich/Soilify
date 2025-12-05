package com.example.soilifymobileapp.models;

import com.google.gson.annotations.SerializedName;

public class ChatResponse {
    @SerializedName("response")
    private String response;

    @SerializedName("conversation_id")
    private Integer conversationId;  // Backend returns int

    @SerializedName("session_id")
    private String sessionId;

    @SerializedName("model")
    private String model;

    public ChatResponse() {}

    // Getters and Setters
    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Integer getConversationId() {
        return conversationId;
    }

    public void setConversationId(Integer conversationId) {
        this.conversationId = conversationId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
