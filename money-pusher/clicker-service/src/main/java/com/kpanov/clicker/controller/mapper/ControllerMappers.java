package com.kpanov.clicker.controller.mapper;

import com.kpanov.clicker.controller.dto.LeaderboardResponseDto;
import com.kpanov.clicker.controller.dto.LeaderboardResponseItemDto;
import com.kpanov.clicker.repository.entity.BalanceEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ControllerMappers {
    public LeaderboardResponseDto toDto(List<BalanceEntity> entities) {
        var items = entities.stream().map(entity ->
            new LeaderboardResponseItemDto(
                    entity.getBalance(),
                    entity.getUserNickname(),
                    entity.getUserEmail()
            )
        ).toList();

        return new LeaderboardResponseDto(items);
    }
}
