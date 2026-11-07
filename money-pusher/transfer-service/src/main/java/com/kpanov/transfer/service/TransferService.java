package com.kpanov.transfer.service;

import com.kpanov.transfer.repository.TransferRepository;
import com.kpanov.transfer.repository.entity.TransferEntity;
import com.kpanov.transfer.repository.entity.TransferStatus;
import com.kpanov.transfer.service.kafka.TransferProducer;
import com.kpanov.transfer.service.model.TransferRequestModel;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransferService {
    private final TransferRepository repository;
    private final TransferProducer transferProducer;

    @Transactional
    public TransferEntity requestTransfer(TransferRequestModel model) {
        var entity = TransferEntity.builder()
                .initiatorEmail(model.initiatorUserEmail())
                .targetEmail(model.targetUserEmail())
                .additionalMessage(model.additionalMessage())
                .transferAmount(model.transferAmount())
                .status(TransferStatus.PENDING)
                .build();

        var createdTransfer = repository.save(entity);

        transferProducer.publishTransferRequest(
                createdTransfer.getTransferId(),
                createdTransfer.getInitiatorEmail(),
                createdTransfer.getTargetEmail(),
                createdTransfer.getTransferAmount()
        );
        return createdTransfer;
    }
}
