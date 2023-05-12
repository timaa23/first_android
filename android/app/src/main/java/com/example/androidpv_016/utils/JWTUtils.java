package com.example.androidpv_016.utils;

import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;

public class JWTUtils {
    public static String decoded(String JWTEncoded) throws Exception {
        try {
            String[] split = JWTEncoded.split("\\.");
            return getJson(split[1]);
        } catch (UnsupportedEncodingException e) {
            Log.e("Error", e.getMessage());
        }
        return null;
    }

    private static String getJson(String strEncoded) throws UnsupportedEncodingException {
        byte[] decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE);
        return new String(decodedBytes, "UTF-8");
    }
}
