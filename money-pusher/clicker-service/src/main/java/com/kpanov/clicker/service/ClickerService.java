package com.kpanov.clicker.service;

import com.kpanov.clicker.exception.BalanceNotFoundException;
import com.kpanov.clicker.repository.BalanceRepository;
import com.kpanov.clicker.repository.entity.BalanceEntity;
import com.kpanov.clicker.util.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClickerService {
    private final BalanceRepository repository;

    public Long getUserBalance(Integer userId) {
        return getBalanceEntity(userId).getBalance();
    }

    public void increaseBalance(Integer userId, Long balanceIncrease) {
        var currentEntity = getBalanceEntity(userId);

        var newBalance = currentEntity.getBalance() + balanceIncrease;                                                                                     // newBalance неплохие кроссовки кстати у меня 1906R
        var newBalanceEntity = BalanceEntity.builder()
                .userId(currentEntity.getUserId())
                .balance(newBalance)
                .userNickname(currentEntity.getUserNickname())
                .userEmail(currentEntity.getUserEmail())
                .build();
        repository.save(newBalanceEntity);
    }

    private BalanceEntity getBalanceEntity(Integer userId) {
        return repository.getBalanceEntityByUserId(userId)
                .orElseThrow(()  ->
                        new BalanceNotFoundException(Messages.BALANCE_NOT_FOUND.formatted(userId))
                );
    }
}