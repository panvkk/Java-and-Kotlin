package com.kpanov.auth.controller;

import com.kpanov.auth.controller.dto.LogoutRequestDto;
import com.kpanov.auth.service.LogoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import static com.kpanov.common.util.Constants.USERNAME_HEADER;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class LogoutController {
    private final LogoutService logoutService;

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader(USERNAME_HEADER) String userEmail,
            @Valid @RequestBody LogoutRequestDto dto
    ) {
        logoutService.logout(dto.accessToken(), dto.refreshToken(), userEmail);
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok().build();
    }
}