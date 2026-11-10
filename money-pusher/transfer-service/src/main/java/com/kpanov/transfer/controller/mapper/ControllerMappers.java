package com.kpanov.transfer.controller.mapper;

import com.kpanov.transfer.controller.dto.TransferRequestDto;
import com.kpanov.transfer.controller.dto.TransferResponseDto;
import com.kpanov.transfer.repository.entity.TransferEntity;
import com.kpanov.transfer.service.model.TransferRequestModel;
import org.springframework.stereotype.Component;

@Component
public class ControllerMappers {

    public TransferResponseDto toDto(TransferEntity entity) {
        return new TransferResponseDto(
                entity.getId(),
                entity.getTransferAmount(),
                entity.getInitiatorEmail(),
                entity.getTargetEmail(),
                entity.getAdditionalMessage(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }

    public TransferRequestModel toModel(TransferRequestDto dto, String initiatorEmail) {
        return new TransferRequestModel(
                initiatorEmail,
                dto.targetUserEmail(),
                dto.transferAmount(),
                dto.additionalMessage()
        );
    }
}
