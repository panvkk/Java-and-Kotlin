package com.kpanov.transfer.service.kafka;

import com.kpanov.common.event.transfer.*;
import com.kpanov.transfer.repository.TransferRepository;
import com.kpanov.transfer.repository.entity.TransferSagaPhase;
import com.kpanov.transfer.service.TransferService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.kpanov.common.util.Constants.*;
import static com.kpanov.transfer.util.Messages.INVALID_SAGA_PHASE;
import static com.kpanov.transfer.util.Messages.TRANSFER_METADATA_MISSING;

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
            log.error(TRANSFER_METADATA_MISSING);
            return;
        }
        var metadata = event.metadata();
        var updatedRows = transferRepository.updateTransferSagaPhaseIfExpected(
                metadata.id(), TransferSagaPhase.DEDUCT, TransferSagaPhase.DEPOSIT
        );
        if(updatedRows == 0) {
            log.warn(INVALID_SAGA_PHASE.formatted(metadata.id()));

            transferProducer.publishCompensateDeduction(
                    new CompensateDeductionEvent(metadata),
                    metadata.id()
            );
            return;
        }

        transferProducer.publishDepositRequest(
                new DepositRequestEvent(metadata),
                metadata.id()
        );
    }

    @Transactional
    @KafkaListener(topics = DEPOSIT_SUCCESS_EVENTS)
    public void handleTransferSuccess(DepositSuccessEvent event) {
        var metadata = event.metadata();

        if(isInvalid(metadata)) {
            return;
        }
        var updatedRows = transferRepository.updateTransferSagaPhaseIfExpected(
                metadata.id(), TransferSagaPhase.DEPOSIT, TransferSagaPhase.FINISH
        );
        if(updatedRows == 0) {
            log.warn(INVALID_SAGA_PHASE.formatted(metadata.id()));
            return;
        }

        transferService.processTransferSuccess(metadata.id());
    }

    @Transactional
    @KafkaListener(topics = INSUFFICIENT_FUNDS_EVENTS)
    public void handleInsufficientFunds(InsufficientFundsEvent event) {
        if(event == null || event.transferId() == null) {
            log.warn(TRANSFER_METADATA_MISSING);
            return;
        }

        var updatedRows = transferRepository.updateTransferSagaPhaseIfExpected(
                event.transferId(), TransferSagaPhase.DEDUCT, TransferSagaPhase.FINISH
        );
        if(updatedRows == 0) {
            log.warn(INVALID_SAGA_PHASE.formatted(event.transferId()));
            return;
        }

        transferService.processTransferFailed(event.transferId());
    }

    @Transactional
    @KafkaListener(topics = DEDUCTION_FAILED_EVENTS)
    public void handleDeductionFailed(DeductionFailedEvent event) {
        var metadata = event.metadata();

        if(isInvalid(metadata)) {
            return;
        }
        var updatedRows = transferRepository.updateTransferSagaPhaseIfExpected(
                metadata.id(), TransferSagaPhase.DEDUCT, TransferSagaPhase.FINISH
        );
        if(updatedRows == 0) {
            log.warn(INVALID_SAGA_PHASE.formatted(metadata.id()));
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
        var updatedRows = transferRepository.updateTransferSagaPhaseIfExpected(
                metadata.id(), TransferSagaPhase.DEPOSIT, TransferSagaPhase.FINISH
        );
        if(updatedRows == 0) {
            log.warn(INVALID_SAGA_PHASE.formatted(metadata.id()));
            return;
        }

        transferProducer.publishCompensateDeduction(
                new CompensateDeductionEvent(metadata), metadata.id()
        );
        transferService.processTransferFailed(metadata.id());
    }

    private boolean isInvalid(TransferMetadata metadata) {
        if(metadata == null || metadata.id() == null) {
            log.warn(TRANSFER_METADATA_MISSING);
            return true;
        }

        return false;
    }
}
