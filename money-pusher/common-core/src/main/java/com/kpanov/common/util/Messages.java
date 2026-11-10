package com.kpanov.common.util;

public class Messages {
    public static final String TOKEN_EXPIRED = "JWT token is expired at: %s.";
    public static final String INVALID_JWT = "Invalid JWT token: %s";

    public static final String JWT_SECRET_BLANK = "Jwt secret cannot be blank.";
    public static final String JWT_SECRET_SHORT = "Jwt secret must be at least 32 characters long to ensure security.";
}
