package com.example.androidpv_016.network;

import com.example.androidpv_016.dto.BaseResponseDTO;
import com.example.androidpv_016.dto.category.CategoryItemDTO;
import com.example.androidpv_016.dto.category.CreateCategoryDTO;
import com.example.androidpv_016.dto.category.UpdateCategoryDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CategoriesAPI {
    @GET("/api/Category/getAll")
    public Call<BaseResponseDTO<List<CategoryItemDTO>>> getCategoryList();

    @GET("/api/Category/getById/{id}")
    public Call<BaseResponseDTO<CategoryItemDTO>> getCategory(@Path("id") int id);

    @POST("/api/Category/create")
    public Call<BaseResponseDTO<CategoryItemDTO>> createCategory(@Body CreateCategoryDTO model);

    @PUT("/api/Category/edit/{id}")
    public Call<BaseResponseDTO<CategoryItemDTO>> updateCategory(@Path("id") int id, @Body UpdateCategoryDTO model);

    @DELETE("/api/Category/delete/{id}")
    public Call<BaseResponseDTO<CategoryItemDTO>> deleteCategory(@Path("id") int id);
}
