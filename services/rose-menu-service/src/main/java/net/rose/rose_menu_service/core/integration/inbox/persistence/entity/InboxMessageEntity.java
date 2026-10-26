package net.rose.rose_menu_service.core.integration.inbox.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "inbox_messages")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class InboxMessageEntity {

    @Id
    @Column(name = "event_id", nullable = false, updatable = false)
    private UUID eventId;

    @Column(
            name = "event_type",
            nullable = false,
            length = 128,
            updatable = false
    )
    private String eventType;

    @Column(
            name = "event_version",
            nullable = false,
            updatable = false
    )
    private int eventVersion;

    @Column(
            name = "producer",
            nullable = false,
            length = 128,
            updatable = false
    )
    private String producer;

    @Column(
            name = "correlation_id",
            nullable = false,
            updatable = false
    )
    private UUID correlationId;

    @Column(
            name = "occurred_at",
            nullable = false,
            updatable = false
    )
    private Instant occurredAt;

    @Column(
            name = "processed_at",
            nullable = false,
            updatable = false
    )
    private Instant processedAt;
}