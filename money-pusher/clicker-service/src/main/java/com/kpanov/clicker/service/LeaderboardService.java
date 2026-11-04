package com.kpanov.clicker.service;

import com.kpanov.clicker.repository.BalanceRepository;
import com.kpanov.clicker.repository.entity.BalanceEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.kpanov.common.util.Constants.LEADERBOARD_LENGTH;

@Service
@RequiredArgsConstructor
public class LeaderboardService {
    private final BalanceRepository balanceRepository;

    public List<BalanceEntity> getTopUsersByBalance() {
        return balanceRepository.getTopUsersByBalance(LEADERBOARD_LENGTH);
    }
}