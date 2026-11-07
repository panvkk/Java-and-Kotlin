package com.kpanov.clicker.service.kafka;

import com.kpanov.clicker.service.BalanceService;
import com.kpanov.common.event.transfer.TransferRequestEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.kpanov.common.util.Constants.TRANSFER_EVENTS;

@Service
@RequiredArgsConstructor
public class TransferConsumer {
    private final BalanceService balanceService;

    @KafkaListener(topics = TRANSFER_EVENTS)
    public void handleUserCreated(TransferRequestEvent event) {

    }
}
