package com.kpanov.clicker.repository;

import com.kpanov.clicker.repository.entity.TransferOperationEntity;
import com.kpanov.common.model.TransferSagaPhase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;

public interface LedgerRepository extends JpaRepository<TransferOperationEntity, Long> {

    @Modifying
    @Query(
            value = "INSERT INTO transfer_operations (transfer_id, phase, initiator_email, target_email, transfer_amount, created_at) " +
                    "VALUES (:transferId, :phase, :initiatorEmail, :targetEmail, :transferAmount, :createdAt) " +
                    "ON CONFLICT (transfer_id, phase) DO NOTHING",
            nativeQuery = true
    )
    int insertPhaseIfAbsent(
            Long transferId,
            String phase,
            String initiatorEmail,
            String targetEmail,
            Long transferAmount,
            Instant createdAt
    );

    boolean existsByTransferIdAndPhase(Long transferId, TransferSagaPhase phase);
}
