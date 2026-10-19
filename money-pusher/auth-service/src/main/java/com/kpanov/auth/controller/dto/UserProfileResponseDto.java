package com.kpanov.auth.controller.dto;

import com.kpanov.auth.repository.entity.Role;

public record UserProfileResponseDto(
        String nickname,
        String email,
        Role role
) { }