package com.example.netflix_app4.network;

import android.content.Context;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.Properties;


public class RetrofitClient {
    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl("https://cf93-2a0d-6fc0-992-100-1c67-eb6b-c032-1e66.ngrok-free.app/api/") // Use 10.0.2.2 for host machine
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static String getBackendUrl(Context context) {
        try (InputStream inputStream = context.getAssets().open("config.properties")) {
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties.getProperty("backend_url");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}