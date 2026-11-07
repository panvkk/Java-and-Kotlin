package com.kpanov.transfer.service.kafka;

import com.kpanov.common.event.transfer.TransferMetadata;
import com.kpanov.common.event.transfer.TransferRequestEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.kpanov.common.util.Constants.TRANSFER_EVENTS;

@Service
@RequiredArgsConstructor
public class TransferProducer {
    private final KafkaTemplate<Long, TransferRequestEvent> kafkaTemplate;

    public void publishTransferRequest(TransferMetadata) {
        var event = new TransferRequestEvent(
                new TransferMetadata(id, initiatorEmail, targetEmail, transferAmount)
        );
        kafkaTemplate.send(TRANSFER_EVENTS, id, event);
    }
}
