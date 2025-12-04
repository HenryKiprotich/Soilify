package com.example.soilifymobileapp.network;

import com.example.soilifymobileapp.models.Token;
import com.example.soilifymobileapp.models.UserCreate;
import com.example.soilifymobileapp.models.UserLogin;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {
    @POST("signup")
    Call<Token> signup(@Body UserCreate user);

    @POST("signin")
    Call<Token> signin(@Body UserLogin user);
}
