package com.recruitguard.app;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient
{
    //TODO: replace with Nhlayi's IP address: "http:her_ip_address:8080/ CMD(ipconfig) for school IP;
    //private static final String BASE_URL = "http://10.143.55.104:8080/";

    private static final String BASE_URL = "http://192.168.0.133:8080/";

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