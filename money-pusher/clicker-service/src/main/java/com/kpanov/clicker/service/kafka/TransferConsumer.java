package com.kpanov.clicker.service.kafka;

import com.kpanov.clicker.service.BalanceService;
import com.kpanov.common.event.transfer.CompensateDeductionEvent;
import com.kpanov.common.event.transfer.DeductionSuccessEvent;
import com.kpanov.common.event.transfer.DepositRequestEvent;
import com.kpanov.common.event.transfer.TransferRequestEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.kpanov.common.util.Constants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferConsumer {
    private final BalanceService balanceService;

    @KafkaListener(topics = TRANSFER_REQUEST_EVENTS)
    public void handleTransferRequest(TransferRequestEvent event) {
        if(event == null || event.metadata() == null) {
            log.error("Dropping transfer-request event: metadata is missing");
            return;
        }

        var metadata = event.metadata();
        balanceService.deductMoney(
                metadata.id(),
                metadata.initiatorEmail(),
                metadata.targetEmail(),
                metadata.transferAmount()
        );
    }

    @KafkaListener(topics = DEPOSIT_REQUEST_EVENTS)
    public void handleDepositRequest(DepositRequestEvent event) {
        if(event == null || event.metadata() == null) {
            log.error("Dropping transfer-request event: metadata is missing");
            return;
        }

        var metadata = event.metadata();
        balanceService.depositMoney(
                metadata.id(),
                metadata.initiatorEmail(),
                metadata.targetEmail(),
                metadata.transferAmount()
        );
    }

    @KafkaListener(topics = COMPENSATE_DEDUCTION_EVENTS)
    public void handleCompensateDeduction(CompensateDeductionEvent event) {
        if(event == null || event.metadata() == null) {
            log.error("Dropping compensate-deduction event: metadata is missing");
            return;
        }

        var metadata = event.metadata();

        balanceService.compensateDeduction(metadata.initiatorEmail(), metadata.transferAmount());
    }
}
