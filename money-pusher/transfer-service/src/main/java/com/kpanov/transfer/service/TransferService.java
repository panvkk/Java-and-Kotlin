package com.kpanov.transfer.service;

import com.kpanov.common.event.transfer.TransferMetadata;
import com.kpanov.common.event.transfer.TransferRequestEvent;
import com.kpanov.transfer.repository.TransferRepository;
import com.kpanov.transfer.repository.entity.TransferEntity;
import com.kpanov.transfer.repository.entity.TransferStatus;
import com.kpanov.transfer.service.kafka.TransferProducer;
import com.kpanov.transfer.service.model.TransferRequestModel;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransferService {
    private final TransferRepository repository;
    private final TransferProducer transferProducer;

    public List<TransferEntity> getUserTransfers(String userEmail) {
        return repository.getUserTransfers(userEmail);
    }

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

        var event = new TransferRequestEvent(
                new TransferMetadata(
                        createdTransfer.getId(),
                        createdTransfer.getInitiatorEmail(),
                        createdTransfer.getTargetEmail(),
                        createdTransfer.getTransferAmount()
                )
        );
        transferProducer.publishTransferRequest(event, createdTransfer.getId());
        return createdTransfer;
    }

    @Transactional
    public void processTransferSuccess(Long transferId) {
        repository.updateTransferStatus(transferId, TransferStatus.SUCCESS);
    }

    @Transactional
    public void processTransferFailed(Long transferId) {
        repository.updateTransferStatus(transferId, TransferStatus.FAILED);
    }
}
