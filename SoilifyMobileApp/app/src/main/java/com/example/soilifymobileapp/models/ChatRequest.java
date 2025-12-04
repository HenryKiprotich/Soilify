package com.example.soilifymobileapp.models;

import com.google.gson.annotations.SerializedName;

public class ChatRequest {
    @SerializedName("message")
    private String message;

    @SerializedName("session_id")
    private String sessionId;

    @SerializedName("context")
    private String context;

    public ChatRequest(String message, String sessionId, String context) {
        this.message = message;
        this.sessionId = sessionId;
        this.context = context;
    }

    // Getters and Setters
}
