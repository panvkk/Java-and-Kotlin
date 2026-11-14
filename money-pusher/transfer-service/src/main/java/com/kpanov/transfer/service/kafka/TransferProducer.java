package com.kpanov.transfer.service.kafka;

import com.kpanov.common.event.transfer.CompensateDeductionEvent;
import com.kpanov.common.event.transfer.DepositRequestEvent;
import com.kpanov.common.event.transfer.TransferRequestEvent;
import com.kpanov.transfer.service.outbox.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.kpanov.common.util.Constants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferProducer {
    private final OutboxService outboxService;

    public void publishTransferRequest(TransferRequestEvent event, Long id) {
        outboxService.enqueue(TRANSFER_REQUEST_EVENTS, id.toString(), event);
    }

    public void publishDepositRequest(DepositRequestEvent event, Long id) {
        outboxService.enqueue(DEPOSIT_REQUEST_EVENTS, id.toString(), event);
    }

    public void publishCompensateDeduction(CompensateDeductionEvent event, Long id) {
        outboxService.enqueue(COMPENSATE_DEDUCTION_EVENTS, id.toString(), event);
    }
}
