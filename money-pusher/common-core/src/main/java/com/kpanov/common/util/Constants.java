package com.kpanov.common.util;

import java.util.List;

public class Constants {

    public static final String BLACKLISTED_KEY_BEGINNING = "blacklisted_access_token: ";

    public static final String AUTH_HEADER_BEGINNING = "Bearer ";

    public static final String USERNAME_HEADER = "X-User-Name";

    public static final List<String> WHITE_LIST_URLS = List.of(
            "/actuator/**",
            "/",
            "/index.html",
            "/api/v1/auth/**",

            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    );
}
