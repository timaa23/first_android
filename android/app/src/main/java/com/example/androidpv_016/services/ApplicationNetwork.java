package com.example.androidpv_016.services;

import com.example.androidpv_016.constants.URLs;
import com.example.androidpv_016.interceptors.JwtInterceptor;
import com.example.androidpv_016.network.AccountAPI;
import com.example.androidpv_016.network.CategoriesAPI;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApplicationNetwork {
    private static ApplicationNetwork instance;
    private final Retrofit retrofit;

    public ApplicationNetwork() {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(new JwtInterceptor())
                .build();

        retrofit = new Retrofit.Builder()
                .client(httpClient)
                .baseUrl(URLs.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static ApplicationNetwork getInstance() {
        if (instance == null)
            instance = new ApplicationNetwork();
        return instance;
    }

    public CategoriesAPI getJsonApi() {
        return retrofit.create(CategoriesAPI.class);
    }

    public AccountAPI getAccountApi() {
        return retrofit.create(AccountAPI.class);
    }
}
