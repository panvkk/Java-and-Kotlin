package com.kpanov.auth.service.model;

public record RegisterRequest(
        String nickname,
        String email,
        String password
) { }
