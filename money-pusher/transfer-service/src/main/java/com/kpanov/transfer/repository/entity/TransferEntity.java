package com.kpanov.transfer.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transfers_table")
public class TransferEntity {
    @Id @Column(name = "transfer_id")
    private Long transferId;

    @Column(name = "initiator_email")
    private String initiatorEmail;

    @Column(name = "target_email")
    private String targetEmail;

    @Column(name = "additional_message")
    private String additionalMessage;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
