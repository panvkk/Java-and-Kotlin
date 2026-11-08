package com.kpanov.clicker.controller;

import com.kpanov.clicker.controller.dto.GetBalanceResponseDto;
import com.kpanov.clicker.controller.dto.IncreaseBalanceRequestDto;
import com.kpanov.clicker.service.ClickerService;
import com.kpanov.common.util.Constants;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.kpanov.common.util.Constants.USERNAME_HEADER;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/clicker")
public class ClickerController {
    private final ClickerService clickerService;

    @PatchMapping
    public ResponseEntity<Void> increaseUserBalance(
            @Parameter(hidden = true)
            @RequestHeader(USERNAME_HEADER) String userEmail,
            @RequestBody IncreaseBalanceRequestDto dto
    ) {
        clickerService.increaseBalance(userEmail, dto.balanceIncrease());

        return ResponseEntity.ok().build();
    }

    @GetMapping("balance/{user_email}")
    public ResponseEntity<GetBalanceResponseDto> getUserBalance(
            @PathVariable("user_email") String userEmail
    ) {
        var responseBalance = clickerService.getUserBalance(userEmail);
        var response = new GetBalanceResponseDto(responseBalance);

        return ResponseEntity.ok().body(response);
    }
}
