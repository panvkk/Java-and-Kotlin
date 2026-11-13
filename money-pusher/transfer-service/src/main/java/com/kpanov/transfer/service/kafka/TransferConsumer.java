package com.kpanov.transfer.service.kafka;

import com.kpanov.common.event.transfer.*;
import com.kpanov.transfer.repository.TransferRepository;
import com.kpanov.transfer.repository.entity.TransferSagaPhase;
import com.kpanov.transfer.service.TransferService;
import com.kpanov.transfer.util.Messages;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.kpanov.common.util.Constants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferConsumer {
    private final TransferService transferService;
    private final TransferRepository transferRepository;
    private final TransferProducer transferProducer;

    @Transactional
    @KafkaListener(topics = DEDUCTION_SUCCESS_EVENTS)
    public void handleDeductionSuccess(DeductionSuccessEvent event) {
        if(event == null || event.metadata() == null) {
            log.error("Dropping deduction-success event: metadata is missing");
            return;
        }
        var metadata = event.metadata();

        var updatedRows = transferRepository.updateTransferSagaPhaseIfExpected(
                metadata.id(), TransferSagaPhase.DEDUCT, TransferSagaPhase.DEPOSIT
        );
        if(updatedRows == 0) {
            log.warn(Messages.INVALID_SAGA_PHASE.formatted(metadata.id()));
            return;
        }

        transferProducer.publishDepositRequest(
                new DepositRequestEvent(metadata),
                metadata.id()
        );
    }

    @KafkaListener(topics = DEPOSIT_SUCCESS_EVENTS)
    public void handleTransferSuccess(DepositSuccessEvent event) {
        var metadata = event.metadata();

        if(isInvalid(metadata)) {
            return;
        }

        transferService.processTransferSuccess(metadata.id());
    }

    @KafkaListener(topics = INSUFFICIENT_FUNDS_EVENTS)
    public void handleInsufficientFunds(InsufficientFundsEvent event) {
        if(event == null || event.transferId() == null) {
            log.warn("Dropping insufficient-funds event: transfer id is missing");
            return;
        }

        transferService.processTransferFailed(event.transferId());
    }

    @KafkaListener(topics = DEDUCTION_FAILED_EVENTS)
    public void handleDeductionFailed(DeductionFailedEvent event) {
        var metadata = event.metadata();

        if(isInvalid(metadata)) {
            return;
        }

        transferService.processTransferFailed(metadata.id());
    }

    @Transactional
    @KafkaListener(topics = DEPOSIT_FAILED_EVENTS)
    public void handleDepositFailed(DepositFailedEvent event) {
        var metadata = event.metadata();

        if(isInvalid(metadata)) {
            return;
        }

        transferProducer.publishCompensateDeduction(
                new CompensateDeductionEvent(metadata), metadata.id()
        );
        transferService.processTransferFailed(metadata.id());
    }

    private boolean isInvalid(TransferMetadata metadata) {
        if(metadata == null || metadata.id() == null) {
            log.warn("Dropping transfer event: metadata or transfer id is missing");
            return true;
        }

        return false;
    }
}
