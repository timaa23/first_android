package com.example.androidpv_016.utils;

import android.util.Log;

import com.example.androidpv_016.application.HomeApplication;
import com.example.androidpv_016.dto.account.UserDTO;
import com.google.gson.Gson;

public class UserUtils {
    public static void setUserToken(String token) {
        HomeApplication.getInstance().saveJwtToken(token);
    }

    public static String getUserToken() {
        return HomeApplication.getInstance().getToken();
    }

    public static void deleteUserToken() {
        HomeApplication.getInstance().deleteToken();
    }

    public static boolean isUserAuth() {
        return HomeApplication.getInstance().isAuth();
    }

    public static UserDTO getUser() {
        Gson gson = new Gson();
        try {
            String json = JWTUtils.decoded(getUserToken());
            UserDTO result = gson.fromJson(json, UserDTO.class);
            return result;
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
        return null;
    }
}
