package com.recruitguard.app;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    // Change this to Nhlayi's actual IP when at school
    private static final String BASE_URL = "http://192.168.0.179:8080/";

    private static Retrofit retrofit = null;

    public static ApiService getService()
    {
        if (retrofit == null)
        {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}