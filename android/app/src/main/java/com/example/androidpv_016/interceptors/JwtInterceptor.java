package com.example.androidpv_016.interceptors;

import com.example.androidpv_016.utils.UserUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class JwtInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        String token = UserUtils.getUserToken();
        Request.Builder builder = chain.request().newBuilder();
        if (token != null && !token.isEmpty()) {
            builder.header("Authorization", "Bearer " + token);
        }
        return chain.proceed(builder.build());
    }
}
