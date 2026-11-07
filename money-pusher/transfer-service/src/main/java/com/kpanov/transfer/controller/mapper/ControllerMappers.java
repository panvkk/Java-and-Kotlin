package com.kpanov.transfer.controller.mapper;

import com.kpanov.transfer.controller.dto.TransferResponseDto;
import com.kpanov.transfer.repository.entity.TransferEntity;
import org.springframework.stereotype.Component;

@Component
public class ControllerMappers {

    public TransferResponseDto toDto(TransferEntity entity) {
        return new TransferResponseDto(
                entity.getTransferId(),
                entity.getTransferAmount(),
                entity.getInitiatorEmail(),
                entity.getTargetEmail(),
                entity.getAdditionalMessage(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}
