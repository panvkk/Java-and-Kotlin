package com.kpanov.auth.controller;

import com.kpanov.auth.controller.dto.LogoutRequestDto;
import com.kpanov.auth.service.LogoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

import static com.kpanov.common.util.Constants.REFRESH_TOKEN_COOKIE;
import static com.kpanov.common.util.Constants.USERNAME_HEADER;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class LogoutController {
    private final LogoutService logoutService;

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader(USERNAME_HEADER) String userEmail,
            @CookieValue(value = REFRESH_TOKEN_COOKIE, required = false) String refreshToken,
            @Valid @RequestBody LogoutRequestDto dto
    ) {
        if(refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        logoutService.logout(dto.accessToken(), refreshToken, userEmail);
        SecurityContextHolder.clearContext();

        var expiredRefreshCookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE, "")
                .httpOnly(true)
                .secure(true)
                .maxAge(Duration.ZERO)
                .sameSite("Lax")
                .path("/api/v1")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, expiredRefreshCookie.toString())
                .build();
    }
}