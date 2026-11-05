package com.kpanov.transfer.repository;

import com.kpanov.transfer.repository.entity.TransferEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransferRepository extends JpaRepository<Long, TransferEntity> {

    List<TransferEntity> getTransfersByInitiatorId(Long id);

    List<TransferEntity> getTransfersByTargetId(Long id);
}
