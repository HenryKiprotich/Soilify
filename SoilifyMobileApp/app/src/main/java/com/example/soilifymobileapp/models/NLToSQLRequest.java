package com.example.soilifymobileapp.models;

import com.google.gson.annotations.SerializedName;

public class NLToSQLRequest {
    @SerializedName("question")
    private String question;

    @SerializedName("session_id")
    private String sessionId;

    public NLToSQLRequest() {}

    public NLToSQLRequest(String question, String sessionId) {
        this.question = question;
        this.sessionId = sessionId;
    }

    // Getters and Setters
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
