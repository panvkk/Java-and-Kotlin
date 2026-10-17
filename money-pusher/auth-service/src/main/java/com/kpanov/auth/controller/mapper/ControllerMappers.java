package com.kpanov.auth.controller.mapper;

import com.kpanov.auth.controller.dto.AuthenticationRequestDto;
import com.kpanov.auth.controller.dto.AuthenticationResponseDto;
import com.kpanov.auth.controller.dto.RegisterRequestDto;
import com.kpanov.auth.service.model.AuthenticationRequest;
import com.kpanov.auth.service.model.AuthenticationResponse;
import com.kpanov.auth.service.model.RegisterRequest;
import org.springframework.stereotype.Component;

@Component
public class ControllerMappers {

    public AuthenticationRequest toModel(AuthenticationRequestDto dto) {
        return new AuthenticationRequest(dto.email(), dto.password());
    }

    public RegisterRequest toModel(RegisterRequestDto dto) {
        return new RegisterRequest(dto.username(), dto.email(), dto.password(), dto.role());
    }

    public AuthenticationResponseDto toDto(AuthenticationResponse model) {
        return new AuthenticationResponseDto(model.accessToken(), model.refreshToken());
    }
}
