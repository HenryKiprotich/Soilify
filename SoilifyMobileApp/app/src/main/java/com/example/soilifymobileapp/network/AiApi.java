package com.example.soilifymobileapp.network;

import com.example.soilifymobileapp.models.ChatRequest;
import com.example.soilifymobileapp.models.ChatResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AiApi {
    @POST("api/ai/chat")
    Call<ChatResponse> chatWithAi(@Body ChatRequest chatRequest);
}
