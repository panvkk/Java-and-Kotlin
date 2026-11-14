package com.kpanov.clicker.repository.entity;

import com.kpanov.common.model.TransferSagaPhase;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "transfer_operations",
        uniqueConstraints = @UniqueConstraint(
                name = "transfer_phase", columnNames = { "transfer_id", "phase"})
)
public class TransferOperationEntity {
    @Id @GeneratedValue
    private Long id;

    @Column(name = "transfer_id")
    private Long transferId;

    @Enumerated(EnumType.STRING)
    private TransferSagaPhase phase;

    @Column(name = "initiator_email")
    private String initiatorEmail;

    @Column(name = "transfer_amount")
    private Long transferAmount;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}