package com.example.androidpv_016.account;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.androidpv_016.BaseActivity;
import com.example.androidpv_016.MainActivity;
import com.example.androidpv_016.R;
import com.example.androidpv_016.constants.Validation;
import com.example.androidpv_016.dto.BaseResponseDTO;
import com.example.androidpv_016.dto.account.LoginDTO;
import com.example.androidpv_016.dto.account.ValidationLoginErrorDTO;
import com.example.androidpv_016.services.ApplicationNetwork;
import com.example.androidpv_016.utils.CommonUtils;
import com.example.androidpv_016.utils.UserUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.regex.Matcher;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {
    TextInputEditText txtEmail;
    TextInputEditText txtPassword;

    TextInputLayout tfEmail;
    TextInputLayout tfPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);

        tfEmail = findViewById(R.id.tfEmail);
        tfPassword = findViewById(R.id.tfPassword);

        setupError();
    }

    public void onClickLogin(View view) {
        if (!CheckAllRegisterFields()) return;

        String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail(email);
        loginDTO.setPassword(password);

        CommonUtils.showLoading();

        requestServer(loginDTO);
    }

    private void requestServer(LoginDTO loginDTO) {
        ApplicationNetwork
                .getInstance()
                .getAccountApi()
                .login(loginDTO)
                .enqueue(new Callback<BaseResponseDTO<String>>() {
                    @Override
                    public void onResponse(Call<BaseResponseDTO<String>> call, Response<BaseResponseDTO<String>> response) {
                        if (response.isSuccessful()) {
                            UserUtils.setUserToken(response.body().getPayload());
                            Toast.makeText(LoginActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
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
                    public void onFailure(Call<BaseResponseDTO<String>> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                        CommonUtils.hideLoading();
                    }
                });
    }

    private void showErrorsServer(String json) {
        Gson gson = new Gson();
        ValidationLoginErrorDTO result = gson.fromJson(json, ValidationLoginErrorDTO.class);

        tfEmail.setError("Помилка входу! Не вірний логін або пароль!");
        tfPassword.setError("Помилка входу! Не вірний логін або пароль!");

        if (result.getErrors().getEmail() != null) {
            tfEmail.setError(result.getErrors().getEmail()[0]);
        }
        if (result.getErrors().getPassword() != null) {
            tfPassword.setError(result.getErrors().getPassword()[0]);
        }
    }

    private boolean CheckAllRegisterFields() {
        boolean isValid = true;

        String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();

        if (!validateEmail(email)) {
            tfEmail.setError(getString(R.string.register_email_required));
            isValid = false;
        } else tfEmail.setError("");

        if (password.isEmpty() || password.length() < 6) {
            tfPassword.setError(getString(R.string.register_password_required));
            isValid = false;
        } else tfPassword.setError("");

        return isValid;
    }

    private boolean validateEmail(String email) {
        Matcher matcher = Validation.VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.matches();
    }

    private void setupError() {
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

        txtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String password = txtPassword.getText().toString().trim();

                if (password.isEmpty() || password.length() < 6) {
                    tfPassword.setError(getString(R.string.register_password_required));
                } else tfPassword.setError("");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}