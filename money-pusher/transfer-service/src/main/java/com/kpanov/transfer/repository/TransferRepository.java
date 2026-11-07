package com.kpanov.transfer.repository;

import com.kpanov.transfer.repository.entity.TransferEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransferRepository extends JpaRepository<TransferEntity, Long> {

    @Query(value =
            "SELECT * FROM transfers_table " +
            "WHERE initiator_email = :email OR target_id = :email " +
            "ORDER BY crated_at DESC", nativeQuery = true)
    List<TransferEntity> getUserTransfers(String email);
}
