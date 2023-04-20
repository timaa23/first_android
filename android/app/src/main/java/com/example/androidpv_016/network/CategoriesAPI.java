package com.example.androidpv_016.network;

import com.example.androidpv_016.dto.BaseResponseDTO;
import com.example.androidpv_016.dto.category.CategoryItemDTO;
import com.example.androidpv_016.dto.category.CreateCategoryDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface CategoriesAPI {
    @GET("/api/Category/getAll")
    public Call<BaseResponseDTO<List<CategoryItemDTO>>> getCategoryList();

    @POST("/api/Category/create")
    public Call<BaseResponseDTO<CategoryItemDTO>> createCategory(@Body CreateCategoryDTO model);
}
