package com.kpanov.auth.service.model;

public record AuthenticationResponse(String accessToken, String refreshToken) { }