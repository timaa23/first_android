package com.example.androidpv_016;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.androidpv_016.category.CategoryAdapter;
import com.example.androidpv_016.category.CategoryEditActivity;
import com.example.androidpv_016.dto.BaseResponseDTO;
import com.example.androidpv_016.dto.category.CategoryItemDTO;
import com.example.androidpv_016.services.CategoryNetwork;
import com.example.androidpv_016.utils.CommonUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {
    CategoryAdapter adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(new CategoryAdapter(new ArrayList<>(), MainActivity.this::onClickEditCategory, MainActivity.this::onClickDeleteCategory));

        CommonUtils.showLoading();

        requestServer();
    }

    void requestServer() {
        CategoryNetwork
                .getInstance()
                .getJsonApi()
                .getCategoryList()
                .enqueue(new Callback<BaseResponseDTO<List<CategoryItemDTO>>>() {
                    @Override
                    public void onResponse(Call<BaseResponseDTO<List<CategoryItemDTO>>> call, Response<BaseResponseDTO<List<CategoryItemDTO>>> response) {
                        BaseResponseDTO<List<CategoryItemDTO>> categoryResponse = response.body();
                        if (categoryResponse.isSuccess()) {
                            if (response.body().getPayload() == null) return;
                            List<CategoryItemDTO> dataList = categoryResponse.getPayload();
                            dataList.sort(Comparator.comparingInt(CategoryItemDTO::getPriority));

                            adapter = new CategoryAdapter(dataList, MainActivity.this::onClickEditCategory, MainActivity.this::onClickDeleteCategory);
                            recyclerView.setAdapter(adapter);
                            CommonUtils.hideLoading();
                        } else {
                            Toast.makeText(MainActivity.this, categoryResponse.getMessage(), Toast.LENGTH_LONG).show();
                            CommonUtils.hideLoading();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponseDTO<List<CategoryItemDTO>>> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Помилка підключення", Toast.LENGTH_LONG).show();
                        CommonUtils.hideLoading();
                    }
                });
    }

    void requestServerRemoveCategory(int id) {
        CategoryNetwork
                .getInstance()
                .getJsonApi()
                .deleteCategory(id)
                .enqueue(new Callback<BaseResponseDTO<CategoryItemDTO>>() {
                    @Override
                    public void onResponse(Call<BaseResponseDTO<CategoryItemDTO>> call, Response<BaseResponseDTO<CategoryItemDTO>> response) {
                        BaseResponseDTO<CategoryItemDTO> categoryResponse = response.body();
                        Toast.makeText(MainActivity.this, categoryResponse.getMessage(), Toast.LENGTH_LONG).show();
                        requestServer();
                    }

                    @Override
                    public void onFailure(Call<BaseResponseDTO<CategoryItemDTO>> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Помилка підключення", Toast.LENGTH_LONG).show();
                        CommonUtils.hideLoading();
                    }
                });
    }

    private void onClickEditCategory(CategoryItemDTO categoryItemDTO) {
        Intent intent = new Intent(MainActivity.this, CategoryEditActivity.class);
        Bundle b = new Bundle();
        b.putInt("id", categoryItemDTO.getId());
        intent.putExtras(b);
        startActivity(intent);
    }

    private void onClickDeleteCategory(CategoryItemDTO categoryItemDTO) {
        CommonUtils.showLoading();
        requestServerRemoveCategory(categoryItemDTO.getId());
    }
}
