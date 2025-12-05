package com.example.soilifymobileapp.network;

import com.example.soilifymobileapp.models.ChatRequest;
import com.example.soilifymobileapp.models.ChatResponse;
import com.example.soilifymobileapp.models.FieldAnalysisResponse;
import com.example.soilifymobileapp.models.NLToSQLRequest;
import com.example.soilifymobileapp.models.NLToSQLResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AiApi {
    @POST("api/ai/chat")
    Call<ChatResponse> chatWithAi(@Body ChatRequest chatRequest);

    @POST("api/ai/ask-sql")
    Call<NLToSQLResponse> askWithSql(@Body NLToSQLRequest request);

    @POST("api/ai/analyze-field/{field_id}")
    Call<FieldAnalysisResponse> analyzeField(
            @Path("field_id") int fieldId,
            @Query("session_id") String sessionId
    );
}
