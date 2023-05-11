package com.example.androidpv_016.category;

import androidx.annotation.Nullable;

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

import com.example.androidpv_016.BaseActivity;
import com.example.androidpv_016.ChangeImageActivity;
import com.example.androidpv_016.MainActivity;
import com.example.androidpv_016.R;
import com.example.androidpv_016.dto.BaseResponseDTO;
import com.example.androidpv_016.dto.category.CategoryItemDTO;
import com.example.androidpv_016.dto.category.CreateCategoryDTO;
import com.example.androidpv_016.services.ApplicationNetwork;
import com.example.androidpv_016.utils.CommonUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryCreateActivity extends BaseActivity {
    private final int SELECT_IMAGE_RESULT = 1000;

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
        setContentView(R.layout.activity_category_create);
        IVPreviewImage = findViewById(R.id.IVPreviewImage);

        txtCategoryName = findViewById(R.id.txtCategoryName);
        txtCategoryDescription = findViewById(R.id.txtCategoryDescription);
        txtCategoryPriority = findViewById(R.id.txtCategoryPriority);

        tfCategoryName = findViewById(R.id.tfCategoryName);
        tfCategoryDescription = findViewById(R.id.tfCategoryDescription);
        tfCategoryPriority = findViewById(R.id.tfCategoryPriority);

        setupError();
    }

    // create category button click
    public void onClickCreateCategory(View view) {
        if (!CheckAllCategoryFields()) return;

        CreateCategoryDTO model = new CreateCategoryDTO();

        String categoryName = txtCategoryName.getText().toString().trim();
        String categoryDescription = txtCategoryDescription.getText().toString().trim();
        int categoryPriority = !txtCategoryPriority.getText().toString().isEmpty() ? Integer.parseInt(txtCategoryPriority.getText().toString()) : -1;

        model.setName(categoryName);
        model.setDescription(categoryDescription);
        model.setPriority(categoryPriority);
        model.setImageBase64(uriToBase64(uri));

        CommonUtils.showLoading();

        requestServer(model);
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
        } else Toast.makeText(CategoryCreateActivity.this, "Помилка!", Toast.LENGTH_LONG).show();

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

    private void requestServer(CreateCategoryDTO createCategoryDTO) {
        ApplicationNetwork
                .getInstance()
                .getJsonApi()
                .createCategory(createCategoryDTO)
                .enqueue(new Callback<BaseResponseDTO<CategoryItemDTO>>() {
                    @Override
                    public void onResponse(Call<BaseResponseDTO<CategoryItemDTO>> call, Response<BaseResponseDTO<CategoryItemDTO>> response) {
                        Toast.makeText(CategoryCreateActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(CategoryCreateActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        CommonUtils.hideLoading();
                    }

                    @Override
                    public void onFailure(Call<BaseResponseDTO<CategoryItemDTO>> call, Throwable t) {
                        Toast.makeText(CategoryCreateActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                        CommonUtils.hideLoading();
                    }
                });
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

        if (uriToBase64(uri).isEmpty()) {
            Toast.makeText(CategoryCreateActivity.this, "Оберіть фотографію", Toast.LENGTH_LONG).show();
            isValid = false;
        }

        return isValid;
    }
}