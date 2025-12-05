package com.example.soilifymobileapp.network;

import android.content.Context;

import com.example.soilifymobileapp.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    public static final String BASE_URL = BuildConfig.BASE_URL;
    private static Retrofit retrofit = null;
    
    // Timeout values in seconds - AI endpoints can take longer
    private static final int CONNECT_TIMEOUT = 30;
    private static final int READ_TIMEOUT = 120;  // 2 minutes for AI responses
    private static final int WRITE_TIMEOUT = 30;

    /**
     * Get Retrofit client with authentication interceptor
     * @param context Application context to access SharedPreferences
     * @return Configured Retrofit instance
     */
    public static Retrofit getClient(Context context) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        
        // Set timeouts for AI endpoints which can take longer
        httpClient.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);
        httpClient.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS);
        httpClient.writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);
        
        httpClient.addInterceptor(loggingInterceptor);
        
        // Always add the auth interceptor - it will automatically add token if available
        if (context != null) {
            httpClient.addInterceptor(new AuthInterceptor(context));
        }

        OkHttpClient client = httpClient.build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }

    /**
     * Get Retrofit client without authentication (for login/signup)
     * @return Configured Retrofit instance without auth interceptor
     */
    public static Retrofit getClientNoAuth() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        
        // Set timeouts
        httpClient.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);
        httpClient.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS);
        httpClient.writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);
        
        httpClient.addInterceptor(loggingInterceptor);

        OkHttpClient client = httpClient.build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }
}
