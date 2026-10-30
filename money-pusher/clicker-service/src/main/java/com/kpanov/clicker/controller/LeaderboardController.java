package com.kpanov.clicker.controller;

import com.kpanov.clicker.controller.dto.LeaderboardResponseDto;
import com.kpanov.clicker.controller.mapper.ControllerMappers;
import com.kpanov.clicker.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/leaderboard")
public class LeaderboardController {
    private final LeaderboardService leaderboardService;
    private final ControllerMappers mappers;

    @GetMapping
    public ResponseEntity<LeaderboardResponseDto> getLeaderboard() {
        var entities = leaderboardService.getTopUsersByBalance();
        return ResponseEntity.ok(mappers.toDto(entities));
    }
}
