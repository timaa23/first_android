package com.example.androidpv_016;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidpv_016.dto.BaseResponseDTO;
import com.example.androidpv_016.dto.category.CategoryItemDTO;
import com.example.androidpv_016.dto.category.CreateCategoryDTO;
import com.example.androidpv_016.services.CategoryNetwork;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCategory extends AppCompatActivity {
    private final int GALLERY_REQ_CODE = 1000;
    ImageView categoryImage;
    String categoryImageBase64;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
        categoryImage = findViewById(R.id.catImg);
    }

    public void OnClickAddCategory(View view) {
        TextView name = findViewById(R.id.nameField);
        TextView description = findViewById(R.id.descriptionField);
        TextView priority = findViewById(R.id.priorityField);

        String nameText = name.getText().toString();
        String descriptionText = description.getText().toString();
        int priorityText = Integer.parseInt(priority.getText().toString());

        CreateCategoryDTO categoryDTO = new CreateCategoryDTO(nameText, descriptionText, categoryImageBase64, priorityText);

        requestServer(categoryDTO);
    }

    void requestServer(CreateCategoryDTO createCategoryDTO) {
        CategoryNetwork
                .getInstance()
                .getJsonApi()
                .createCategory(createCategoryDTO)
                .enqueue(new Callback<BaseResponseDTO<CategoryItemDTO>>() {
                    @Override
                    public void onResponse(Call<BaseResponseDTO<CategoryItemDTO>> call, Response<BaseResponseDTO<CategoryItemDTO>> response) {
                        Toast.makeText(AddCategory.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                        setResult(Activity.RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<BaseResponseDTO<CategoryItemDTO>> call, Throwable t) {
                        Toast.makeText(AddCategory.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void OnClickAddImage(View view) {
        Intent iGallery = new Intent(Intent.ACTION_PICK);
        iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(iGallery, GALLERY_REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == GALLERY_REQ_CODE) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                categoryImageBase64 = encodeImage(selectedImage);
                categoryImage.setImageURI(imageUri);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return "data:image/jpeg;base64," + encImage;
    }
}