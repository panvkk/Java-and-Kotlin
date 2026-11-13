package com.kpanov.clicker.service;

import com.kpanov.clicker.repository.BalanceRepository;
import com.kpanov.clicker.repository.entity.BalanceEntity;
import com.kpanov.clicker.service.kafka.TransferProducer;
import com.kpanov.common.event.transfer.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceService {
    private final BalanceRepository repository;
    private final TransferProducer transferProducer;

    @Transactional
    public void deductMoney(
            Long transferId,
            String initiatorEmail,
            String targetEmail,
            Long transferAmount
    ) {
        var deductionFailed = validate(transferId, initiatorEmail, targetEmail, transferAmount);
        if(deductionFailed != null) {
            publishDeductionFailed(deductionFailed);
            return;
        }

        var initiatorBalance = repository.getBalanceWithLock(initiatorEmail);

        if(initiatorBalance.isPresent()) {
            var entity = initiatorBalance.get();
            var balanceRemain = entity.getBalance() - transferAmount;

            if(balanceRemain >= 0) {
                repository.updateBalance(initiatorEmail, balanceRemain);

                transferProducer.publishDeductionSuccess(
                        new DeductionSuccessEvent(
                                new TransferMetadata(
                                        transferId, initiatorEmail, targetEmail, transferAmount
                                )
                        ),
                        transferId
                );
            } else {
                transferProducer.publishInsufficientFunds(
                        new InsufficientFundsEvent(transferId, initiatorEmail), transferId
                );
            }
        } else {
            transferProducer.publishDeductionFailed(
                    new DeductionFailedEvent(
                            new TransferMetadata(
                                    transferId, initiatorEmail, targetEmail, transferAmount
                            )
                    ),
                    transferId
            );
        }
    }

    @Transactional
    public void compensateDeduction(
            String initiatorEmail,
            Long transferAmount
    ) {
        if(isBlank(initiatorEmail)) {
            log.error("Dropping compensate-deduction event: initiator email is blank");
            return;
        }

        if(transferAmount == null || transferAmount <= 0) {
            log.error("Dropping compensate-deduction event: transfer amount is not positive");
            return;
        }

        var initiatorBalance = repository.getBalanceWithLock(initiatorEmail);

        if(initiatorBalance.isPresent()) {
            var entity = initiatorBalance.get();
            var newBalance = entity.getBalance() + transferAmount;

            repository.updateBalance(initiatorEmail, newBalance);
        } else {
            // TODO такого и быть не должно быть, но надо обработать как-то хзз
        }
    }

    @Transactional
    public void depositMoney(
            Long transferId,
            String initiatorEmail,
            String targetEmail,
            Long transferAmount
    ) {
        var invalidEvent = validate(transferId, initiatorEmail, targetEmail, transferAmount);
        if(invalidEvent != null) {
            publishDepositFailed(invalidEvent.metadata());
            return;
        }

        var targetBalance = repository.getBalanceWithLock(targetEmail);

        if(targetBalance.isPresent()) {
            var entity = targetBalance.get();
            var newBalance = entity.getBalance() + transferAmount;

            repository.updateBalance(targetEmail, newBalance);

            transferProducer.publishDepositSuccess(
                    new DepositSuccessEvent(
                            new TransferMetadata(
                                    transferId, initiatorEmail, targetEmail, transferAmount
                            )
                    ),
                    transferId
            );

        } else {
            transferProducer.publishDepositFailed(
                    new DepositFailedEvent(
                            new TransferMetadata(
                                    transferId, initiatorEmail, targetEmail, transferAmount
                            )
                    ),
                    transferId
            );
        }
    }

    /**
     * Проверяет событие, пришедшее из Kafka. Возвращает {@link DeductionFailedEvent},
     * если переводить деньги нельзя, и {@code null}, если событие корректное.
     */
    private DeductionFailedEvent validate(
            Long transferId,
            String initiatorEmail,
            String targetEmail,
            Long transferAmount
    ) {
        var invalid = transferId == null
                || transferAmount == null
                || transferAmount <= 0
                || isBlank(initiatorEmail)
                || isBlank(targetEmail)
                || initiatorEmail.equals(targetEmail);

        if(!invalid) {
            return null;
        }

        return new DeductionFailedEvent(
                new TransferMetadata(transferId, initiatorEmail, targetEmail, transferAmount)
        );
    }

    private void publishDeductionFailed(DeductionFailedEvent event) {
        var metadata = event.metadata();

        if(metadata.id() == null) {
            log.error("Dropping invalid transfer event: transfer id is missing");
            return;
        }

        log.warn("Invalid transfer event, publishing deduction-failed: transferId={}", metadata.id());
        transferProducer.publishDeductionFailed(event, metadata.id());
    }

    private void publishDepositFailed(TransferMetadata metadata) {
        if(metadata.id() == null) {
            log.error("Dropping invalid transfer event: transfer id is missing");
            return;
        }

        log.warn("Invalid transfer event, publishing deposit-failed: transferId={}", metadata.id());
        transferProducer.publishDepositFailed(new DepositFailedEvent(metadata), metadata.id());
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
