package com.example.androidpv_016;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.androidpv_016.account.LoginActivity;
import com.example.androidpv_016.account.RegisterActivity;
import com.example.androidpv_016.category.CategoryListActivity;
import com.example.androidpv_016.utils.UserUtils;

public class MainActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (UserUtils.isUserAuth()) {
            Intent intent = new Intent(MainActivity.this, CategoryListActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void onClickLogin(View view) {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void onClickRegister(View view) {
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }
}
