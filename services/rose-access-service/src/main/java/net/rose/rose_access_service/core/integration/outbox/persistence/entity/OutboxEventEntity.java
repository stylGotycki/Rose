package net.rose.rose_access_service.core.integration.outbox.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import net.rose.rose_access_service.core.integration.outbox.domain.model.OutboxEventStatus;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OutboxEventEntity {

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
            name = "exchange_name",
            nullable = false,
            length = 128,
            updatable = false
    )
    private String exchangeName;

    @Column(
            name = "routing_key",
            nullable = false,
            length = 255,
            updatable = false
    )
    private String routingKey;

    @Column(
            name = "payload",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private OutboxEventStatus status;

    @Column(name = "occurred_at", nullable = false, updatable = false)
    private Instant occurredAt;

    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(name = "attempts", nullable = false)
    private int attempts;

    @Column(name = "next_attempt_at", nullable = false)
    private Instant nextAttemptAt;

    @Column(name = "last_error", length = 2000)
    private String lastError;

    public void markPublished(Instant publishedAt) {
        this.status = OutboxEventStatus.PUBLISHED;
        this.publishedAt = publishedAt;
        this.lastError = null;
    }

    public void registerFailure(
            String error,
            Instant nextAttemptAt
    ) {
        this.attempts++;
        this.lastError = truncate(error);
        this.nextAttemptAt = nextAttemptAt;
    }

    private String truncate(String value) {
        if (value == null) {
            return null;
        }

        return value.length() <= 2000
                ? value
                : value.substring(0, 2000);
    }
}