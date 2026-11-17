package com.kpanov.clicker.service.kafka;

import com.kpanov.clicker.service.outbox.OutboxService;
import com.kpanov.common.event.transfer.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.kpanov.common.util.Constants.*;

@Service
@RequiredArgsConstructor
public class TransferProducer {
    private final OutboxService outboxService;

    public void publishDeductionSuccess(DeductionSuccessEvent event, Long id) {
        outboxService.enqueue(DEDUCTION_SUCCESS_EVENTS, id.toString(), event);
    }

    public void publishDeductionFailed(DeductionFailedEvent event, Long id) {
        outboxService.enqueue(DEDUCTION_FAILED_EVENTS, id.toString(), event);
    }

    public void publishInsufficientFunds(InsufficientFundsEvent event, Long id) {
        outboxService.enqueue(INSUFFICIENT_FUNDS_EVENTS, id.toString(), event);
    }

    public void publishDepositSuccess(DepositSuccessEvent event, Long id) {
        outboxService.enqueue(DEPOSIT_SUCCESS_EVENTS, id.toString(), event);
    }

    public void publishDepositFailed(DepositFailedEvent event, Long id) {
        outboxService.enqueue(DEPOSIT_FAILED_EVENTS, id.toString(), event);
    }
}
