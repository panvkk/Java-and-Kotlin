package com.kpanov.transfer.repository.entity;

import jakarta.persistence.*;
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

    @Column(name = "transfer_amount")
    private Long transferAmount;

    @Column(name = "initiator_email")
    private String initiatorEmail;

    @Column(name = "target_email")
    private String targetEmail;

    @Column(name = "additional_message")
    private String additionalMessage;

    @Enumerated(EnumType.STRING)
    private TransferStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
