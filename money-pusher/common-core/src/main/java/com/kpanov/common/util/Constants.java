package com.kpanov.common.util;

import java.util.List;

public class Constants {
    public static final Integer LEADERBOARD_LENGTH = 10;


    public static final String BLACKLISTED_KEY_BEGINNING = "blacklisted_access_token: ";

    public static final String AUTH_HEADER_BEGINNING = "Bearer ";

    public static final String USERNAME_HEADER = "X-User-Name";

    public static final List<String> WHITE_LIST_URLS = List.of(
            "/actuator/**",
            "/",
            "/index.html",
            "/api/v1/auth/**",
            "/api/v1/leaderboard",

            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",

            "/favicon.ico",
            "/swagger-resources",
            "/swagger-resources/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    );

    public static final String USER_EVENTS = "user_events";
    public static final String COMPENSATE_DEDUCTION_EVENTS = "compensate_deduction_events";
    public static final String DEDUCTION_FAILED_EVENTS = "deduction_failed_events";
    public static final String DEDUCTION_SUCCESS_EVENTS = "deduction_success_events";
    public static final String DEPOSIT_FAILED_EVENTS = "deposit_failed_events";
    public static final String DEPOSIT_SUCCESS_EVENTS = "deposit_success_events";
    public static final String INSUFFICIENT_FUNDS_EVENTS = "insufficient_funds_events";
    public static final String TRANSFER_REQUEST_EVENTS = "transfer_events";

}
