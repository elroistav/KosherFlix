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
                    .baseUrl("http://172.18.101.242:4000/api/") // Backend URL
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