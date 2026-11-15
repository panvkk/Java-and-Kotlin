package com.kpanov.auth.controller;

import com.kpanov.auth.config.property.AuthJwtProperties;
import com.kpanov.auth.controller.dto.AuthenticationRequestDto;
import com.kpanov.auth.controller.dto.AuthenticationResponseDto;
import com.kpanov.auth.controller.dto.RegisterRequestDto;
import com.kpanov.auth.controller.mapper.ControllerMappers;
import com.kpanov.auth.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

import static com.kpanov.common.util.Constants.REFRESH_TOKEN_COOKIE;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final ControllerMappers mappers;
    private final AuthenticationService authenticationService;
    private final AuthJwtProperties jwtProperties;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponseDto> register(
            @Valid @RequestBody RegisterRequestDto request
    ) {
        var responseModel = authenticationService.register(mappers.toModel(request));
        var refreshCookie = getRefreshTokenCookie(responseModel.refreshToken());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(mappers.toDto(responseModel));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponseDto> authenticate(
            @Valid @RequestBody AuthenticationRequestDto request
    ) {
        var responseModel = authenticationService.login(mappers.toModel(request));
        var refreshCookie = getRefreshTokenCookie(responseModel.refreshToken());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(mappers.toDto(responseModel));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponseDto> refreshToken(
            @CookieValue(value = REFRESH_TOKEN_COOKIE, required = false) String token
    ) {
        if(token == null || token.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var response = authenticationService.refreshToken(token) ;
        var refreshCookie = getRefreshTokenCookie(response.refreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(mappers.toDto(response));
    }

    private ResponseCookie getRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE, refreshToken)
                .httpOnly(true)
                .secure(true)
                .maxAge(Duration.ofMillis(jwtProperties.refreshTokenExpiration()))
                .sameSite("Lax")
                .path("/api/v1")
                .build();
    }
}
