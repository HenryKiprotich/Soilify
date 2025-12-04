package com.example.soilifymobileapp.network;

import com.example.soilifymobileapp.models.FertiliserUsageCreate;
import com.example.soilifymobileapp.models.FertiliserUsageRead;
import com.example.soilifymobileapp.models.FertiliserUsageUpdate;
import com.example.soilifymobileapp.models.FieldOption;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface FertiliserApi {
    @GET("fertiliser-usage/fields-dropdown")
    Call<List<FieldOption>> getFieldsForDropdown();

    @GET("fertiliser-usage")
    Call<List<FertiliserUsageRead>> getAllFertiliserUsage();

    @GET("fertiliser-usage/{usage_id}")
    Call<FertiliserUsageRead> getFertiliserUsage(@Path("usage_id") int usageId);

    @POST("fertiliser-usage")
    Call<FertiliserUsageRead> createFertiliserUsage(@Body FertiliserUsageCreate usage);

    @PUT("fertiliser-usage/{usage_id}")
    Call<FertiliserUsageRead> updateFertiliserUsage(@Path("usage_id") int usageId, @Body FertiliserUsageUpdate usageUpdate);

    @DELETE("fertiliser-usage/{usage_id}")
    Call<Void> deleteFertiliserUsage(@Path("usage_id") int usageId);
}
