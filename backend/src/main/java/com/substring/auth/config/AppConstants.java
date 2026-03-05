package com.substring.auth.config;

public class AppConstants {

    public static String[] AUTH_PUBLIC_URL = {
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            "/api/v1/auth/refresh",
            "/oauth2/**",
            "/login/**",
            "/error"
    };
}
