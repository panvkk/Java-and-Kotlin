package com.kpanov.transfer.repository;

import com.kpanov.transfer.repository.entity.TransferEntity;
import com.kpanov.transfer.repository.entity.TransferSagaPhase;
import com.kpanov.transfer.repository.entity.TransferStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TransferRepository extends JpaRepository<TransferEntity, Long> {

    @Query(value =
            "SELECT * FROM transfers_table " +
            "WHERE initiator_email = :email OR target_email = :email " +
            "ORDER BY created_at DESC", nativeQuery = true)
    List<TransferEntity> getUserTransfers(@Param(value = "email") String email);

    Optional<TransferEntity> getTransferById(Long id);

    @Modifying
    @Query("UPDATE TransferEntity t SET t.status = :status WHERE t.id = :id")
    void updateTransferStatus(Long id, TransferStatus status);

    @Modifying
    @Query("UPDATE TransferEntity t SET t.sagaPhase = :newPhase " +
            "WHERE t.id = :id AND t.sagaPhase = :expectedPhase ")
    int updateTransferSagaPhaseIfExpected( // Возвращаем число обновлённых строк после запроса
            Long id,
            TransferSagaPhase expectedPhase,
            TransferSagaPhase newPhase
    );
}
