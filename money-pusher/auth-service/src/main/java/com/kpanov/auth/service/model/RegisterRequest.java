package com.kpanov.auth.service.model;

import com.kpanov.auth.repository.entity.Role;

public record RegisterRequest(
        String username,
        String email,
        String password,
        Role role
) { }
