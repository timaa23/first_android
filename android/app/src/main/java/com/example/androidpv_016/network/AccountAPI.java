package com.example.androidpv_016.network;

import com.example.androidpv_016.dto.BaseResponseDTO;
import com.example.androidpv_016.dto.account.EditUserDTO;
import com.example.androidpv_016.dto.account.LoginDTO;
import com.example.androidpv_016.dto.account.RegisterDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AccountAPI {
    @POST("/api/Account/login")
    public Call<BaseResponseDTO<String>> login(@Body LoginDTO model);

    @POST("/api/Account/register")
    public Call<BaseResponseDTO> register(@Body RegisterDTO model);

    @POST("/api/Account/update")
    public Call<BaseResponseDTO<String>> edit(@Body EditUserDTO model);
}
