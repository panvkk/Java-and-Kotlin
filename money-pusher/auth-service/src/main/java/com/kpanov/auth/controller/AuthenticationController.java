package com.kpanov.auth.controller;

import com.kpanov.auth.controller.dto.AuthenticationRequestDto;
import com.kpanov.auth.controller.dto.AuthenticationResponseDto;
import com.kpanov.auth.controller.dto.RegisterRequestDto;
import com.kpanov.auth.controller.mapper.ControllerMappers;
import com.kpanov.auth.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final ControllerMappers mappers;
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponseDto> register(
            @RequestBody RegisterRequestDto request
    ) {
        var responseModel = authenticationService.register(mappers.toModel(request));
        return ResponseEntity.ok(mappers.toDto(responseModel));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponseDto> authenticate(
            @RequestBody AuthenticationRequestDto request
    ) {
        var responseModel = authenticationService.authenticate(mappers.toModel(request));
        return ResponseEntity.ok(mappers.toDto(responseModel));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponseDto> refreshToken(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        if(authHeader == null || !authHeader.contains(HEADER_BEGINNING)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        final String token = authHeader.substring(HEADER_BEGINNING.length());
        var response = mappers.toDto(authenticationService.refreshToken(token));

        return ResponseEntity.ok(response);
    }

    static private final String HEADER_BEGINNING = "Bearer ";
}
