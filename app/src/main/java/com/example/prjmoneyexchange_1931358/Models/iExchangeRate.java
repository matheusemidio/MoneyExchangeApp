package com.example.prjmoneyexchange_1931358.Models;

import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface iExchangeRate
{
    @GET("latest/{base_code}")
    Call<JsonObject> getConversionRates(@Path("base_code") String base_code);
}
