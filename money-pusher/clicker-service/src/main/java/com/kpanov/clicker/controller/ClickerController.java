package com.kpanov.clicker.controller;

import com.kpanov.clicker.controller.dto.IncreaseBalanceRequestDto;
import com.kpanov.clicker.service.ClickerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/clicker")
public class ClickerController {
    private final ClickerService clickerService;

    @PatchMapping()
    public ResponseEntity<Void> increaseUserBalance(
            @RequestBody IncreaseBalanceRequestDto dto
    ) {
        clickerService.increaseBalance(dto.userId(), dto.balanceIncrease());

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<>
}
