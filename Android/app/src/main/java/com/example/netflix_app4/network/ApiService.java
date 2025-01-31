package com.example.netflix_app4.network;

import com.example.netflix_app4.model.LoginRequest;
import com.example.netflix_app4.model.LoginResponse;
import com.example.netflix_app4.model.RegisterResponse;
import com.example.netflix_app4.model.TokenResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {
    @POST("tokens")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);


    @Multipart
    @POST("users")
    Call<RegisterResponse> register(
            @Part("userName") RequestBody userName,
            @Part("name") RequestBody name,
            @Part("email") RequestBody email,
            @Part("password") RequestBody password,
            @Part MultipartBody.Part profilePicture
    );

    @GET("tokens")
    Call<TokenResponse> validateToken(@Header("Authorization") String token);

}

