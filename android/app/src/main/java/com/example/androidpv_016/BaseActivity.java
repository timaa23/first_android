package com.example.androidpv_016;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidpv_016.account.LoginActivity;
import com.example.androidpv_016.account.RegisterActivity;
import com.example.androidpv_016.category.CategoryCreateActivity;
import com.example.androidpv_016.utils.CommonUtils;
import com.example.androidpv_016.utils.UserUtils;

public class BaseActivity extends AppCompatActivity {
    public BaseActivity() {
        CommonUtils.setContext(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        if (UserUtils.isUserAuth()) {
            menu.setGroupVisible(R.id.group_anon,false);
            menu.setGroupVisible(R.id.group_main,true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menu_home:
                try {
                    intent = new Intent(BaseActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (Exception ex) {
                    System.out.println("--- Problem ---> " + ex.getMessage());
                }
                return true;
            case R.id.menu_create:
                try {
                    intent = new Intent(BaseActivity.this, CategoryCreateActivity.class);
                    startActivity(intent);
                    finish();
                } catch (Exception ex) {
                    System.out.println("--- Problem ---> " + ex.getMessage());
                }
                return true;
            case R.id.menu_login:
                try {
                    intent = new Intent(BaseActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } catch (Exception ex) {
                    System.out.println("--- Problem ---> " + ex.getMessage());
                }
                return true;
            case R.id.menu_register:
                try {
                    intent = new Intent(BaseActivity.this, RegisterActivity.class);
                    startActivity(intent);
                    finish();
                } catch (Exception ex) {
                    System.out.println("--- Problem ---> " + ex.getMessage());
                }
                return true;
            case R.id.menu_logout:
                try {
                    UserUtils.deleteUserToken();
                    intent = new Intent(BaseActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (Exception ex) {
                    System.out.println("--- Problem ---> " + ex.getMessage());
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
