package com.example.soilifymobileapp.models;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class NLToSQLResponse {
    @SerializedName("question")
    private String question;

    @SerializedName("sql_query")
    private String sqlQuery;

    @SerializedName("results")
    private Map<String, Object> results;  // Contains "columns" and "rows"

    @SerializedName("natural_response")
    private String naturalResponse;

    @SerializedName("conversation_id")
    private Integer conversationId;

    public NLToSQLResponse() {}

    // Getters and Setters
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getSqlQuery() {
        return sqlQuery;
    }

    public void setSqlQuery(String sqlQuery) {
        this.sqlQuery = sqlQuery;
    }

    public Map<String, Object> getResults() {
        return results;
    }

    public void setResults(Map<String, Object> results) {
        this.results = results;
    }

    public String getNaturalResponse() {
        return naturalResponse;
    }

    public void setNaturalResponse(String naturalResponse) {
        this.naturalResponse = naturalResponse;
    }

    public Integer getConversationId() {
        return conversationId;
    }

    public void setConversationId(Integer conversationId) {
        this.conversationId = conversationId;
    }
}
