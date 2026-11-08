package com.kpanov.clicker.service.kafka;

import com.kpanov.common.event.transfer.DepositSuccessEvent;
import com.kpanov.common.event.transfer.InsufficientFundsEvent;
import com.kpanov.common.event.transfer.DeductionFailedEvent;
import com.kpanov.common.event.transfer.DeductionSuccessEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.kpanov.common.util.Constants.*;

@Service
@RequiredArgsConstructor
public class TransferProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishDeductionSuccess(DeductionSuccessEvent event, Long id) {
        kafkaTemplate.send(DEDUCTION_SUCCESS_EVENTS, id.toString(), event);
    }

    public void publishDeductionFailed(DeductionFailedEvent event, Long id) {
        kafkaTemplate.send(DEDUCTION_FAILED_EVENTS, id.toString(), event);
    }

    public void publishInsufficientFunds(InsufficientFundsEvent event, Long id) {
        kafkaTemplate.send(INSUFFICIENT_FUNDS_EVENTS, id.toString(), event);
    }

    public void publishDepositSuccess(DepositSuccessEvent event, Long id) {
        kafkaTemplate.send(DEPOSIT_SUCCESS_EVENTS, id.toString(), event);
    }
}
