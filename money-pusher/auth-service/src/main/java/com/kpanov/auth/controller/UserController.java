package com.kpanov.auth.controller;

import com.kpanov.auth.controller.dto.UserProfileResponseDto;
import com.kpanov.auth.controller.mapper.ControllerMappers;
import com.kpanov.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final ControllerMappers mappers;

    /** в будущем должно быть возможным получить профиль юзера без аутентификации,
     * но пока для теста нужен защищённый эндпоинт */
    @GetMapping("/{user_email}")
    public ResponseEntity<UserProfileResponseDto> getUserProfile(
            @PathVariable(value = "user_email") String email
    ) {
        var response = userService.getUserByEmail(email);
        var responseDto = mappers.toDto(response);
        return ResponseEntity.ok(responseDto);
    }
}
