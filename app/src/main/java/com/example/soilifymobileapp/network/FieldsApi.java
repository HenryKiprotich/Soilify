package com.example.soilifymobileapp.network;

import com.example.soilifymobileapp.models.Field;
import com.example.soilifymobileapp.models.FieldCreate;
import com.example.soilifymobileapp.models.FieldRead;
import com.example.soilifymobileapp.models.FieldUpdate;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface FieldsApi {

    @GET("api/fields")
    Call<List<FieldRead>> getAllFields();

    @GET("api/fields/{field_id}")
    Call<FieldRead> getField(@Path("field_id") int fieldId);

    @POST("api/fields")
    Call<FieldRead> createField(@Body FieldCreate field);

    @PUT("api/fields/{field_id}")
    Call<FieldRead> updateField(@Path("field_id") int fieldId, @Body FieldUpdate field);

    @DELETE("api/fields/{field_id}")
    Call<Void> deleteField(@Path("field_id") int fieldId);
}
