package com.example.netflix_app4.network;

import android.content.Context;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static Properties properties = new Properties();

    public static void loadConfig(Context context) {
        try {
            InputStream inputStream = context.getAssets().open("config.properties");
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getBaseUrl() {
        return properties.getProperty("backend_url", "http://localhost/"); // Provide a default URL in case the key is missing
    }
}
