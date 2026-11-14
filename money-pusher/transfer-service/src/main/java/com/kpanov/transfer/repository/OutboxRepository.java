package com.kpanov.transfer.repository;

import com.kpanov.transfer.repository.entity.OutboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxEntity, Long> {
    @Query(
            value = "SELECT * FROM outbox_table WHERE published_at IS NULL " +
                    "ORDER BY id LIMIT :limit " +
                    "FOR UPDATE SKIP LOCKED",
            nativeQuery = true)
    List<OutboxEntity> lockPending(@Param("limit") int limit);

    @Modifying
    @Query("UPDATE OutboxEntity o SET o.publishedAt = :now WHERE o.id = :id")
    int markPublished(@Param("id") Long id, @Param("now") Instant now);

    @Modifying
    @Query("DELETE FROM OutboxEntity o " +
            "WHERE o.publishedAt IS NOT NULL AND o.publishedAt < :before")
    int deletePublishedBefore(@Param("before") Instant before);
}
