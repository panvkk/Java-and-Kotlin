package com.kpanov.auth.controller.dto;

import com.kpanov.auth.repository.entity.Role;

public record RegisterRequestDto(
    String nickname,
    String email,
    String password,
    Role role
) { }
