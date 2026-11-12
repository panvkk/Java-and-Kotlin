package com.kpanov.clicker.service;

import com.kpanov.clicker.exception.BalanceNotFoundException;
import com.kpanov.clicker.repository.BalanceRepository;
import com.kpanov.clicker.repository.entity.BalanceEntity;
import com.kpanov.clicker.util.Messages;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClickerService {
    private final BalanceRepository repository;

    public Long getUserBalance(String userEmail) {
        return getBalanceEntity(userEmail)
                .getBalance();
    }

    @Transactional
    public void increaseBalance(String userEmail, Long balanceIncrease) {
        repository.increaseBalance(userEmail, balanceIncrease);
    }

    @Transactional
    public void createNewUserBalance(
            Integer userId,
            String nickname,
            String email
    ) {
        if(userId == null) {
            log.error("Dropping user-created event: user id is missing");
            return;
        }

        if(repository.existsById(userId)) {
            return;
        }

        if(email == null || email.isBlank()) {
            log.error("Dropping user-created event: user id={} has no email", userId);
            return;
        }

        var newBalance = BalanceEntity.builder()
                .userId(userId)
                .userNickname(nickname)
                .userEmail(email)
                .balance(0L)
                .build();

        repository.save(newBalance);
    }

    private BalanceEntity getBalanceEntity(String userEmail) {
        return repository.getBalanceEntityByUserEmail(userEmail)
                .orElseThrow(()  ->
                        new BalanceNotFoundException(Messages.BALANCE_NOT_FOUND.formatted(userEmail))
                );
    }
}