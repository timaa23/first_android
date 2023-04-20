package com.example.androidpv_016;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.androidpv_016.category.CategoryAdapter;
import com.example.androidpv_016.dto.BaseResponseDTO;
import com.example.androidpv_016.dto.category.CategoryItemDTO;
import com.example.androidpv_016.services.CategoryNetwork;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private final int ADD_CATEGORY_REQ_CODE = 500;
    CategoryAdapter adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(new CategoryAdapter(new ArrayList<>()));

        requestServer();
    }

    public void OpenNewActivity(View view) {
        Intent intent = new Intent(this, AddCategory.class);
        startActivityForResult(intent, ADD_CATEGORY_REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == ADD_CATEGORY_REQ_CODE) {
            requestServer();
        }
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
                            dataList.sort(Comparator.comparingInt(item -> item.getPriority()));

                            adapter = new CategoryAdapter(dataList);
                            recyclerView.setAdapter(adapter);
                        } else {
                            Toast.makeText(MainActivity.this, categoryResponse.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponseDTO<List<CategoryItemDTO>>> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Помилка підключення", Toast.LENGTH_LONG).show();
                    }
                });
    }
}
