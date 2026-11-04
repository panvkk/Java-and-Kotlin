package com.kpanov.clicker.controller.dto;

import java.util.List;

public record LeaderboardResponseDto(
        List<LeaderboardResponseItemDto> items
) { }