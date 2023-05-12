package com.example.androidpv_016.account;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.androidpv_016.BaseActivity;
import com.example.androidpv_016.ChangeImageActivity;
import com.example.androidpv_016.MainActivity;
import com.example.androidpv_016.R;
import com.example.androidpv_016.category.CategoryEditActivity;
import com.example.androidpv_016.constants.URLs;
import com.example.androidpv_016.dto.BaseResponseDTO;
import com.example.androidpv_016.dto.account.EditUserDTO;
import com.example.androidpv_016.dto.account.UserDTO;
import com.example.androidpv_016.dto.category.CategoryItemDTO;
import com.example.androidpv_016.dto.category.UpdateCategoryDTO;
import com.example.androidpv_016.services.ApplicationNetwork;
import com.example.androidpv_016.utils.CommonUtils;
import com.example.androidpv_016.utils.UserUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditUserActivity extends BaseActivity {
    private final int SELECT_IMAGE_RESULT = 1000;

    ImageView IVPreviewImage;

    TextInputEditText txtFirstName;
    TextInputEditText txtLastName;

    TextInputLayout tfFirstName;
    TextInputLayout tfLastName;

    Uri uri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        IVPreviewImage = findViewById(R.id.IVPreviewImage);

        txtFirstName = findViewById(R.id.txtFirstName);
        txtLastName = findViewById(R.id.txtLastName);

        tfFirstName = findViewById(R.id.tfFirstName);
        tfLastName = findViewById(R.id.tfLastName);

        setFields();
    }

    public void onClickEdit(View view) {
        if (!CheckAllUserFields()) return;

        String firstName = txtFirstName.getText().toString().trim();
        String lastName = txtLastName.getText().toString().trim();

        EditUserDTO user = new EditUserDTO();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setImageBase64(uriToBase64(uri));

        CommonUtils.showLoading();

        editUserRequest(user);
    }

    private void editUserRequest(EditUserDTO editUserDTO) {
        ApplicationNetwork
                .getInstance()
                .getAccountApi()
                .edit(editUserDTO)
                .enqueue(new Callback<BaseResponseDTO<String>>() {
                    @Override
                    public void onResponse(Call<BaseResponseDTO<String>> call, Response<BaseResponseDTO<String>> response) {
                        if (response.isSuccessful()) {
                            UserUtils.setUserToken(response.body().getPayload());
                            Toast.makeText(EditUserActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(EditUserActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            try {
                                String resp = response.errorBody().string();
//                                showErrorsServer(resp);
                            } catch (IOException e) {
                                System.out.println("Error response");
                            }
                        }

                        CommonUtils.hideLoading();
                    }

                    @Override
                    public void onFailure(Call<BaseResponseDTO<String>> call, Throwable t) {
                        Toast.makeText(EditUserActivity.this, "Помилка підключення", Toast.LENGTH_LONG).show();
                        CommonUtils.hideLoading();
                    }
                });
    }

    private boolean CheckAllUserFields() {
        boolean isValid = true;

        String firstName = txtFirstName.getText().toString().trim();
        String lastName = txtLastName.getText().toString().trim();

        if (firstName.isEmpty() || firstName.length() < 3) {
            tfFirstName.setError(getString(R.string.category_name_required));
            isValid = false;
        } else tfFirstName.setError("");

        if (lastName.isEmpty() || lastName.length() < 3) {
            tfLastName.setError(getString(R.string.category_name_required));
            isValid = false;
        } else tfLastName.setError("");

        return isValid;
    }

    private void setFields() {
        UserDTO user = UserUtils.getUser();
        if (user == null) {
            Intent intent = new Intent(EditUserActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        txtFirstName.setText(user.getFirstName());
        txtLastName.setText(user.getLastName());

        String imageURL = URLs.BASE_IMAGE_URL + user.getImage();
        Glide.with(this)
                .load(imageURL)
                .into(IVPreviewImage);
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
        } else Toast.makeText(EditUserActivity.this, "Помилка!", Toast.LENGTH_LONG).show();

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
}