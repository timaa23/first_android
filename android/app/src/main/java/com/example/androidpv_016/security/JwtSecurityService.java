package com.example.androidpv_016.security;

public interface JwtSecurityService {
    void saveJwtToken(String token);

    String getToken();

    void deleteToken();

    boolean isAuth();
}
