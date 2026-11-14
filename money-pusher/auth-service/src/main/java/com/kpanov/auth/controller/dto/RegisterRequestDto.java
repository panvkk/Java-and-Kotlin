package com.kpanov.auth.controller.dto;

import com.kpanov.auth.repository.entity.Role;
import com.kpanov.common.util.Constants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;

@Validated
public record RegisterRequestDto(
        @NotBlank String nickname,
        @NotBlank @Email String email,
        @NotBlank @Size(min = Constants.MIN_USER_PASSWORD_SIZE) String password
) { }
