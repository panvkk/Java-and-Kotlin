package com.kpanov.clicker.service;

import com.kpanov.clicker.exception.BalanceNotFoundException;
import com.kpanov.clicker.repository.BalanceRepository;
import com.kpanov.clicker.repository.entity.BalanceEntity;
import com.kpanov.clicker.util.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClickerService {
    private final BalanceRepository repository;

    public Long getUserBalance(Integer userId) {
        return getBalanceEntity(userId)
                .orElseThrow(()  ->
                        new BalanceNotFoundException(Messages.BALANCE_NOT_FOUND.formatted(userId))
                )
                .getBalance();
    }

    public void increaseBalance(Integer userId, Long balanceIncrease) {
        var currentEntity = getBalanceEntity(userId).orElse(
                BalanceEntity.builder()
                        .userId(userId)
                        .balance(0L)
                        .userEmail("")
                        .userNickname("")
                        .build()
        );

        var newBalance = currentEntity.getBalance() + balanceIncrease;                                                                                     // newBalance неплохие кроссовки кстати у меня 1906R
        var newBalanceEntity = BalanceEntity.builder()
                .userId(currentEntity.getUserId())
                .balance(newBalance)
                .userNickname(currentEntity.getUserNickname())
                .userEmail(currentEntity.getUserEmail())
                .build();

        repository.save(newBalanceEntity);
    }

    private Optional<BalanceEntity> getBalanceEntity(Integer userId) {
        return repository.getBalanceEntityByUserId(userId);
    }
}