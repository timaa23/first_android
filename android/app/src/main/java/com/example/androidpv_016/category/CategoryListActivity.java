package com.example.androidpv_016.category;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.androidpv_016.BaseActivity;
import com.example.androidpv_016.MainActivity;
import com.example.androidpv_016.R;
import com.example.androidpv_016.dto.BaseResponseDTO;
import com.example.androidpv_016.dto.category.CategoryItemDTO;
import com.example.androidpv_016.services.ApplicationNetwork;
import com.example.androidpv_016.utils.CommonUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryListActivity extends BaseActivity {
    CategoryAdapter adapter;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(new CategoryAdapter(new ArrayList<>(), CategoryListActivity.this::onClickEditCategory, CategoryListActivity.this::onClickDeleteCategory));

        CommonUtils.showLoading();
        requestServer();
    }
    void requestServer() {
        ApplicationNetwork
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

                            adapter = new CategoryAdapter(dataList, CategoryListActivity.this::onClickEditCategory, CategoryListActivity.this::onClickDeleteCategory);
                            recyclerView.setAdapter(adapter);
                            CommonUtils.hideLoading();
                        } else {
                            Toast.makeText(CategoryListActivity.this, categoryResponse.getMessage(), Toast.LENGTH_LONG).show();
                            CommonUtils.hideLoading();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponseDTO<List<CategoryItemDTO>>> call, Throwable t) {
                        Toast.makeText(CategoryListActivity.this, "Помилка підключення", Toast.LENGTH_LONG).show();
                        CommonUtils.hideLoading();
                    }
                });
    }
    void requestServerRemoveCategory(int id) {
        ApplicationNetwork
                .getInstance()
                .getJsonApi()
                .deleteCategory(id)
                .enqueue(new Callback<BaseResponseDTO>() {
                    @Override
                    public void onResponse(Call<BaseResponseDTO> call, Response<BaseResponseDTO> response) {
                        BaseResponseDTO<CategoryItemDTO> categoryResponse = response.body();
                        Toast.makeText(CategoryListActivity.this, categoryResponse.getMessage(), Toast.LENGTH_LONG).show();
                        requestServer();
                    }

                    @Override
                    public void onFailure(Call<BaseResponseDTO> call, Throwable t) {
                        Toast.makeText(CategoryListActivity.this, "Помилка підключення", Toast.LENGTH_LONG).show();
                        CommonUtils.hideLoading();
                    }
                });
    }

    private void onClickEditCategory(CategoryItemDTO categoryItemDTO) {
        Intent intent = new Intent(CategoryListActivity.this, CategoryEditActivity.class);
        Bundle b = new Bundle();
        b.putInt("id", categoryItemDTO.getId());
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }

    private void onClickDeleteCategory(CategoryItemDTO categoryItemDTO) {
        CommonUtils.showLoading();
        requestServerRemoveCategory(categoryItemDTO.getId());
    }
}