package com.example.netflix_app4.network;

import com.example.netflix_app4.model.LoginRequest;
import com.example.netflix_app4.model.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("api/tokens")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);
}