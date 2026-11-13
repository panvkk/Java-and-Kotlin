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

    @Id @GeneratedValue
    private Long id;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "saga_phase")
    private TransferSagaPhase sagaPhase;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
