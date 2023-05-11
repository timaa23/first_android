package com.example.androidpv_016.account;

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
import com.example.androidpv_016.constants.Validation;
import com.example.androidpv_016.dto.BaseResponseDTO;
import com.example.androidpv_016.dto.account.RegisterDTO;
import com.example.androidpv_016.dto.account.ValidationRegisterErrorDTO;
import com.example.androidpv_016.services.ApplicationNetwork;
import com.example.androidpv_016.utils.CommonUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends BaseActivity {
    private final int SELECT_IMAGE_RESULT = 1000;

    ImageView IVPreviewImage;
    TextInputEditText txtName;
    TextInputEditText txtLastName;
    TextInputEditText txtEmail;
    TextInputEditText txtPassword;
    TextInputEditText txtConfirmPassword;
    TextInputLayout tfName;
    TextInputLayout tfLastName;
    TextInputLayout tfEmail;
    TextInputLayout tfPassword;
    TextInputLayout tfConfirmPassword;

    Uri uri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        IVPreviewImage = findViewById(R.id.IVPreviewImage);

        txtName = findViewById(R.id.txtName);
        txtLastName = findViewById(R.id.txtLastName);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        txtConfirmPassword = findViewById(R.id.txtConfirmPassword);

        tfName = findViewById(R.id.tfName);
        tfLastName = findViewById(R.id.tfLastName);
        tfEmail = findViewById(R.id.tfEmail);
        tfPassword = findViewById(R.id.tfPassword);
        tfConfirmPassword = findViewById(R.id.tfConfirmPassword);

        setupError();
    }

    // register button click
    public void onClickRegister(View view) {
        if (!CheckAllRegisterFields()) return;

        String name = txtName.getText().toString().trim();
        String lastName = txtLastName.getText().toString().trim();
        String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();
        String confirmPassword = txtConfirmPassword.getText().toString().trim();

        RegisterDTO model = new RegisterDTO();

        model.setFirstName(name);
        model.setLastName(lastName);
        model.setEmail(email);
        model.setPassword(password);
        model.setConfirmPassword(confirmPassword);
        model.setImageBase64(uriToBase64(uri));

        CommonUtils.showLoading();

        requestServer(model);
    }

    private void requestServer(RegisterDTO registerDTO) {
        ApplicationNetwork
                .getInstance()
                .getAccountApi()
                .register(registerDTO)
                .enqueue(new Callback<BaseResponseDTO>() {
                    @Override
                    public void onResponse(Call<BaseResponseDTO> call, Response<BaseResponseDTO> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            try {
                                String resp = response.errorBody().string();
                                showErrorsServer(resp);
                            } catch (IOException e) {
                                System.out.println("Error response");
                            }
                        }

                        CommonUtils.hideLoading();
                    }

                    @Override
                    public void onFailure(Call<BaseResponseDTO> call, Throwable t) {
                        Toast.makeText(RegisterActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                        CommonUtils.hideLoading();
                    }
                });
    }

    private void showErrorsServer(String json) {
        Gson gson = new Gson();
        ValidationRegisterErrorDTO result = gson.fromJson(json, ValidationRegisterErrorDTO.class);

        if (result.getErrors().getFirstName() != null) {
            tfName.setError(result.getErrors().getFirstName()[0]);
        }
        if (result.getErrors().getLastName() != null) {
            tfLastName.setError(result.getErrors().getLastName()[0]);
        }
        if (result.getErrors().getEmail() != null) {
            tfEmail.setError(result.getErrors().getEmail()[0]);
        }
        if (result.getErrors().getPassword() != null) {
            tfPassword.setError(result.getErrors().getPassword()[0]);
        }
        if (result.getErrors().getConfirmPassword() != null) {
            tfConfirmPassword.setError(result.getErrors().getConfirmPassword()[0]);
        }
        if (result.getErrors().getImageBase64() != null) {
            Toast.makeText(RegisterActivity.this, result.getErrors().getImageBase64()[0], Toast.LENGTH_LONG).show();
        }
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
        } else Toast.makeText(RegisterActivity.this, "Помилка!", Toast.LENGTH_LONG).show();

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

    private boolean CheckAllRegisterFields() {
        boolean isValid = true;

        String name = txtName.getText().toString().trim();
        String lastName = txtLastName.getText().toString().trim();
        String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();
        String confirmPassword = txtConfirmPassword.getText().toString().trim();

        if (name.isEmpty() || name.length() < 3) {
            tfName.setError(getString(R.string.register_name_required));
            isValid = false;
        } else tfName.setError("");

        if (lastName.isEmpty() || lastName.length() < 3) {
            tfLastName.setError(getString(R.string.register_lastName_required));
            isValid = false;
        } else tfLastName.setError("");

        if (!validateEmail(email)) {
            tfEmail.setError(getString(R.string.register_email_required));
            isValid = false;
        } else tfEmail.setError("");

        if (password.isEmpty() || password.length() < 6) {
            tfPassword.setError(getString(R.string.register_password_required));
            isValid = false;
        } else {
            if (!password.equals(confirmPassword)) {
                tfPassword.setError(getString(R.string.register_differentPasswords));
                tfConfirmPassword.setError(getString(R.string.register_differentPasswords));
                isValid = false;
            } else {
                tfPassword.setError("");
                tfConfirmPassword.setError("");
            }
        }

        if (uriToBase64(uri).isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Оберіть фотографію", Toast.LENGTH_LONG).show();
            isValid = false;
        }

        return isValid;
    }

    private boolean validateEmail(String email) {
        Matcher matcher = Validation.VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.matches();
    }

    private void setupError() {
        txtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String name = txtName.getText().toString().trim();

                if (name.isEmpty() || name.length() < 3) {
                    tfName.setError(getString(R.string.register_name_required));
                } else tfName.setError("");
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        txtLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String lastName = txtLastName.getText().toString().trim();

                if (lastName.isEmpty() || lastName.length() < 3) {
                    tfLastName.setError(getString(R.string.register_lastName_required));
                } else tfLastName.setError("");
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        txtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String email = txtEmail.getText().toString().trim();

                if (!validateEmail(email)) {
                    tfEmail.setError(getString(R.string.register_email_required));
                } else tfEmail.setError("");
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        TextWatcher passwordTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String password = txtPassword.getText().toString().trim();
                String confirmPassword = txtConfirmPassword.getText().toString().trim();

                if (password.isEmpty() || password.length() < 6) {
                    tfPassword.setError(getString(R.string.register_password_required));
                } else {
                    if (!password.equals(confirmPassword)) {
                        tfPassword.setError(getString(R.string.register_differentPasswords));
                        tfConfirmPassword.setError(getString(R.string.register_differentPasswords));
                    } else {
                        tfPassword.setError("");
                        tfConfirmPassword.setError("");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        txtPassword.addTextChangedListener(passwordTextWatcher);
        txtConfirmPassword.addTextChangedListener(passwordTextWatcher);
    }
}