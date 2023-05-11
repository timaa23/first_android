package com.example.androidpv_016.utils;

import com.example.androidpv_016.application.HomeApplication;
import com.example.androidpv_016.security.JwtSecurityService;

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
}
