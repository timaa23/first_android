package com.example.androidpv_016.services;

import com.example.androidpv_016.constants.URLs;
import com.example.androidpv_016.network.CategoriesAPI;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CategoryNetwork {
    private static CategoryNetwork instance;
    private Retrofit retrofit;

    public CategoryNetwork() {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .client(httpClient)
                .baseUrl(URLs.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static CategoryNetwork getInstance() {
        if (instance == null)
            instance = new CategoryNetwork();
        return instance;
    }

    public CategoriesAPI getJsonApi() {
        return retrofit.create(CategoriesAPI.class);
    }
}
