package com.kpanov.auth.controller;

import com.kpanov.auth.controller.dto.LogoutRequestDto;
import com.kpanov.auth.service.LogoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import static com.kpanov.auth.util.Constants.HEADER_BEGINNING;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class LogoutController {
    private final LogoutService logoutService;

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestBody LogoutRequestDto request,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader
    ) {
        final String accessToken;
        if(authHeader == null || !authHeader.startsWith(HEADER_BEGINNING)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        accessToken = authHeader.substring(HEADER_BEGINNING.length());

        logoutService.logout(accessToken, request.requestToken());
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok().build();
    }
}
