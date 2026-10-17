package com.kpanov.auth.controller.dto;

import com.kpanov.auth.repository.entity.Role;

public record RegisterRequestDto(
    String username,
    String email,
    String password,
    Role role
) { }
