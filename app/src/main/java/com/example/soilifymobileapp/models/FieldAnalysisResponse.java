package com.example.soilifymobileapp.models;

import com.google.gson.annotations.SerializedName;

public class FieldAnalysisResponse {
    @SerializedName("analysis")
    private String analysis;

    @SerializedName("conversation_id")
    private Integer conversationId;

    @SerializedName("field_name")
    private String fieldName;

    @SerializedName("model")
    private String model;

    public FieldAnalysisResponse() {}

    // Getters and Setters
    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public Integer getConversationId() {
        return conversationId;
    }

    public void setConversationId(Integer conversationId) {
        this.conversationId = conversationId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
