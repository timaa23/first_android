package com.example.androidpv_016.category;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.androidpv_016.ChangeImageActivity;
import com.example.androidpv_016.MainActivity;
import com.example.androidpv_016.R;
import com.example.androidpv_016.constants.URLs;
import com.example.androidpv_016.dto.BaseResponseDTO;
import com.example.androidpv_016.dto.category.CategoryItemDTO;
import com.example.androidpv_016.dto.category.UpdateCategoryDTO;
import com.example.androidpv_016.services.CategoryNetwork;
import com.example.androidpv_016.utils.CommonUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryEditActivity extends AppCompatActivity {
    private final int SELECT_IMAGE_RESULT = 1000;

    CategoryItemDTO category = null;

    ImageView IVPreviewImage;
    TextInputEditText txtCategoryName;
    TextInputEditText txtCategoryDescription;
    TextInputEditText txtCategoryPriority;

    TextInputLayout tfCategoryName;
    TextInputLayout tfCategoryDescription;
    TextInputLayout tfCategoryPriority;

    Uri uri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.androidpv_016.R.layout.activity_category_edit);

        IVPreviewImage = findViewById(R.id.IVPreviewImage);

        txtCategoryName = findViewById(R.id.txtCategoryName);
        txtCategoryDescription = findViewById(R.id.txtCategoryDescription);
        txtCategoryPriority = findViewById(R.id.txtCategoryPriority);

        tfCategoryName = findViewById(R.id.tfCategoryName);
        tfCategoryDescription = findViewById(R.id.tfCategoryDescription);
        tfCategoryPriority = findViewById(R.id.tfCategoryPriority);

        setCurrentCategory();
        setupError();
    }

    public void onClickCreateCategory(View view) {
        if (!CheckAllCategoryFields()) return;

        UpdateCategoryDTO model = new UpdateCategoryDTO();

        String categoryName = txtCategoryName.getText().toString().trim();
        String categoryDescription = txtCategoryDescription.getText().toString().trim();
        int categoryPriority = !txtCategoryPriority.getText().toString().isEmpty() ? Integer.parseInt(txtCategoryPriority.getText().toString()) : -1;

        model.setName(categoryName);
        model.setDescription(categoryDescription);
        model.setPriority(categoryPriority);
        model.setImageBase64(uriToBase64(uri));

        CommonUtils.showLoading();

        updateCategory(category.getId(), model);
    }

    // select image button click
    public void onClickSelectImage(View view) {
        Intent intent = new Intent(this, ChangeImageActivity.class);
        startActivityForResult(intent, SELECT_IMAGE_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == SELECT_IMAGE_RESULT) {
            uri = data.getParcelableExtra("croppedUri");
            IVPreviewImage.setImageURI(uri);
        } else Toast.makeText(CategoryEditActivity.this, "Помилка!", Toast.LENGTH_LONG).show();

    }

    boolean CheckAllCategoryFields() {
        boolean isValid = true;

        String categoryName = txtCategoryName.getText().toString().trim();
        String categoryDescription = txtCategoryDescription.getText().toString().trim();
        int categoryPriority = 0;

        try {
            categoryPriority = Integer.parseInt(txtCategoryPriority.getText().toString());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        if (categoryName.isEmpty() || categoryName.length() < 3) {
            tfCategoryName.setError(getString(R.string.category_name_required));
            isValid = false;
        } else tfCategoryName.setError("");

        if (categoryDescription.isEmpty() || categoryDescription.length() < 3) {
            tfCategoryDescription.setError(getString(R.string.category_description_required));
            isValid = false;
        } else tfCategoryDescription.setError("");

        if (categoryPriority < 1) {
            tfCategoryPriority.setError(getString(R.string.category_priority_required));
            isValid = false;
        } else tfCategoryPriority.setError("");

        return isValid;
    }

    private void setupError() {
        txtCategoryName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String categoryName = txtCategoryName.getText().toString().trim();

                if (categoryName.isEmpty() || categoryName.length() < 3) {
                    tfCategoryName.setError(getString(R.string.category_name_required));
                } else tfCategoryName.setError("");
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        txtCategoryDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String categoryDescription = txtCategoryDescription.getText().toString().trim();

                if (categoryDescription.isEmpty() || categoryDescription.length() < 3) {
                    tfCategoryDescription.setError(getString(R.string.category_description_required));
                } else tfCategoryDescription.setError("");
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        txtCategoryPriority.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int categoryPriority = 0;

                try {
                    categoryPriority = Integer.parseInt(txtCategoryPriority.getText().toString());
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }

                if (categoryPriority < 1) {
                    tfCategoryPriority.setError(getString(R.string.category_priority_required));
                } else tfCategoryPriority.setError("");
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private String uriToBase64(Uri uri) {
        try {
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            byte[] byteArr = bytes.toByteArray();
            return "data:image/jpeg;base64," + Base64.encodeToString(byteArr, Base64.DEFAULT);
        } catch (Exception ex) {
            return "";
        }
    }

    void setCurrentCategory() {
        Bundle b = getIntent().getExtras();

        int categoryId = -1;
        if (b != null)
            categoryId = b.getInt("id");

        CommonUtils.showLoading();
        requestServer(categoryId);
    }

    void setFields() {
        txtCategoryName.setText(category.getName());
        txtCategoryDescription.setText(category.getDescription());
        txtCategoryPriority.setText(Integer.toString(category.getPriority()));

        String imageURL = URLs.BASE_IMAGE_URL + category.getImage();
        Glide.with(this)
                .load(imageURL)
                .into(IVPreviewImage);
    }

    void requestServer(int categoryId) {
        CategoryNetwork
                .getInstance()
                .getJsonApi()
                .getCategory(categoryId)
                .enqueue(new Callback<BaseResponseDTO<CategoryItemDTO>>() {
                    @Override
                    public void onResponse(Call<BaseResponseDTO<CategoryItemDTO>> call, Response<BaseResponseDTO<CategoryItemDTO>> response) {
                        BaseResponseDTO<CategoryItemDTO> categoryResponse = response.body();
                        if (categoryResponse.isSuccess()) {
                            if (response.body().getPayload() == null) return;
                            category = categoryResponse.getPayload();

                            setFields();

                            CommonUtils.hideLoading();
                        } else {
                            Toast.makeText(CategoryEditActivity.this, categoryResponse.getMessage(), Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(CategoryEditActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                            CommonUtils.hideLoading();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponseDTO<CategoryItemDTO>> call, Throwable t) {
                        Toast.makeText(CategoryEditActivity.this, "Помилка підключення", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(CategoryEditActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        CommonUtils.hideLoading();
                    }
                });
    }

    void updateCategory(int categoryId, UpdateCategoryDTO updateCategoryDTO) {
        CategoryNetwork
                .getInstance()
                .getJsonApi()
                .updateCategory(categoryId, updateCategoryDTO)
                .enqueue(new Callback<BaseResponseDTO<CategoryItemDTO>>() {
                    @Override
                    public void onResponse(Call<BaseResponseDTO<CategoryItemDTO>> call, Response<BaseResponseDTO<CategoryItemDTO>> response) {
                        BaseResponseDTO<CategoryItemDTO> categoryResponse = response.body();
                        if (categoryResponse.isSuccess()) {
                            Toast.makeText(CategoryEditActivity.this, categoryResponse.getMessage(), Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(CategoryEditActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                            CommonUtils.hideLoading();
                        } else {
                            Toast.makeText(CategoryEditActivity.this, categoryResponse.getMessage(), Toast.LENGTH_LONG).show();
                            CommonUtils.hideLoading();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponseDTO<CategoryItemDTO>> call, Throwable t) {
                        Toast.makeText(CategoryEditActivity.this, "Помилка підключення", Toast.LENGTH_LONG).show();
                        CommonUtils.hideLoading();
                    }
                });
    }
}